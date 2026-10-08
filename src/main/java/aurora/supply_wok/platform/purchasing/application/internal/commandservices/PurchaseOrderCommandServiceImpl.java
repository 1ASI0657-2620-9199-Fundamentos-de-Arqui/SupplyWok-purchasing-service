package aurora.supply_wok.platform.purchasing.application.internal.commandservices;

import aurora.supply_wok.platform.purchasing.application.commandservices.PurchaseOrderCommandService;
import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderItemCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.DeletePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.UpdatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.UpdatePurchaseOrderStatusCommand;
import aurora.supply_wok.platform.purchasing.domain.model.entities.PurchaseOrderItem;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderPriority;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderStatus;
import aurora.supply_wok.platform.purchasing.domain.repositories.PurchaseOrderRepository;
import aurora.supply_wok.platform.suppliers.interfaces.acl.SuppliersContextFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the PurchaseOrderCommandService interface.
 */
@Service
public class PurchaseOrderCommandServiceImpl implements PurchaseOrderCommandService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SuppliersContextFacade suppliersContextFacade;

    public PurchaseOrderCommandServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                                           SuppliersContextFacade suppliersContextFacade) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.suppliersContextFacade = suppliersContextFacade;
    }

    @Override
    public Optional<PurchaseOrder> handle(CreatePurchaseOrderCommand command) {
        var supplierName = suppliersContextFacade.getSupplierIdentityById(command.supplierId())
                .map(supplierIdentity -> supplierIdentity.name())
                .filter(name -> !name.isBlank());
        if (supplierName.isEmpty()) {
            return Optional.empty();
        }

        var items = validateAndMapItems(command.items());
        var priority = parsePriority(command.priority());
        var status = parseStatus(command.status() == null || command.status().isBlank() ? "Pending" : command.status());

        if (status != EPurchaseOrderStatus.Pending) {
            throw new IllegalArgumentException("New purchase orders must start as Pending.");
        }

        validateOrderData(
                command.code(),
                command.restaurantName(),
                null,
                command.supplierId(),
                command.orderDate(),
                command.estimatedDate(),
                command.priority(),
                command.status() == null || command.status().isBlank() ? "Pending" : command.status(),
                items
        );

        var order = new PurchaseOrder(command, supplierName.get(), priority, status, items);
        var savedOrder = purchaseOrderRepository.save(order);
        suppliersContextFacade.ensureSupplierClientLinked(savedOrder.getSupplierId(), savedOrder.getRestaurantName());
        return Optional.of(savedOrder);
    }

    @Override
    public Optional<PurchaseOrder> handle(UpdatePurchaseOrderCommand command) {
        var order = purchaseOrderRepository.findById(command.id());
        if (order.isEmpty()) {
            return Optional.empty();
        }

        var supplierName = suppliersContextFacade.getSupplierIdentityById(command.supplierId())
                .map(supplierIdentity -> supplierIdentity.name())
                .filter(name -> !name.isBlank());
        if (supplierName.isEmpty()) {
            return Optional.empty();
        }

        var items = validateAndMapItems(command.items());
        var priority = parsePriority(command.priority());
        var status = parseStatus(command.status());

        validateOrderData(
                command.code(),
                command.restaurantName(),
                command.id(),
                command.supplierId(),
                command.orderDate(),
                command.estimatedDate(),
                command.priority(),
                command.status(),
                items
        );

        if (!order.get().canTransitionTo(status)) {
            throw new IllegalArgumentException("Invalid purchase order status transition.");
        }

        order.get().updateInformation(
                command.code().trim(),
                command.supplierId(),
                supplierName.get().trim(),
                command.restaurantName().trim(),
                command.orderDate(),
                command.estimatedDate(),
                priority,
                status,
                items
        );
        var savedOrder = purchaseOrderRepository.save(order.get());
        suppliersContextFacade.ensureSupplierClientLinked(savedOrder.getSupplierId(), savedOrder.getRestaurantName());
        return Optional.of(savedOrder);
    }

    @Override
    public Optional<PurchaseOrder> handle(UpdatePurchaseOrderStatusCommand command) {
        var order = purchaseOrderRepository.findById(command.id());
        if (order.isEmpty()) {
            return Optional.empty();
        }

        var status = parseStatus(command.status());
        order.get().updateStatus(status);
        return Optional.of(purchaseOrderRepository.save(order.get()));
    }

    @Override
    public boolean handle(DeletePurchaseOrderCommand command) {
        var order = purchaseOrderRepository.findById(command.id());
        if (order.isEmpty()) {
            return false;
        }

        purchaseOrderRepository.delete(order.get());
        return true;
    }

    private void validateOrderData(String code,
                                   String restaurantName,
                                   Long excludedId,
                                   Long supplierId,
                                   LocalDate orderDate,
                                   LocalDate estimatedDate,
                                   String priority,
                                   String status,
                                   List<PurchaseOrderItem> items) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Purchase order code is required.");
        }
        if (restaurantName == null || restaurantName.isBlank()) {
            throw new IllegalArgumentException("Restaurant name is required.");
        }
        if (supplierId == null || supplierId <= 0) {
            throw new IllegalArgumentException("Supplier is required.");
        }
        if (orderDate == null) {
            throw new IllegalArgumentException("Order date is required.");
        }
        if (estimatedDate != null && estimatedDate.isBefore(orderDate)) {
            throw new IllegalArgumentException("Estimated date cannot be earlier than order date.");
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("At least one purchase order item is required.");
        }

        parsePriority(priority);
        parseStatus(status);

        var normalizedCode = code.trim();
        var duplicated = excludedId == null
                ? purchaseOrderRepository.existsByCode(normalizedCode)
                : purchaseOrderRepository.existsByCodeAndIdNot(normalizedCode, excludedId);
        if (duplicated) {
            throw new IllegalArgumentException("Purchase order code already exists.");
        }
    }

    private List<PurchaseOrderItem> validateAndMapItems(List<CreatePurchaseOrderItemCommand> itemCommands) {
        if (itemCommands == null) {
            throw new IllegalArgumentException("At least one purchase order item is required.");
        }

        var items = new ArrayList<PurchaseOrderItem>();
        for (var itemCommand : itemCommands) {
            if (itemCommand == null) {
                throw new IllegalArgumentException("Purchase order item is required.");
            }
            if (itemCommand.productName() == null || itemCommand.productName().isBlank()) {
                throw new IllegalArgumentException("Product name is required.");
            }
            if (itemCommand.quantity() == null || itemCommand.quantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero.");
            }
            if (itemCommand.unitPrice() == null || itemCommand.unitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Unit price must be greater than zero.");
            }
            if (itemCommand.unitType() == null || itemCommand.unitType().isBlank()) {
                throw new IllegalArgumentException("Unit type is required.");
            }

            var item = new PurchaseOrderItem(
                    itemCommand.inventoryItemId(),
                    itemCommand.productName().trim(),
                    itemCommand.quantity().setScale(2, RoundingMode.HALF_UP),
                    itemCommand.unitPrice().setScale(2, RoundingMode.HALF_UP),
                    itemCommand.unitType().trim()
            );
            item.setId(itemCommand.id());
            items.add(item);
        }
        return items;
    }

    private EPurchaseOrderPriority parsePriority(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Invalid purchase order priority.");
        }

        var normalized = value.trim();
        return java.util.Arrays.stream(EPurchaseOrderPriority.values())
                .filter(priority -> priority.name().equalsIgnoreCase(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid purchase order priority."));
    }

    private EPurchaseOrderStatus parseStatus(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Invalid purchase order status.");
        }

        var normalized = value.replace(" ", "").replace("-", "").trim();
        return java.util.Arrays.stream(EPurchaseOrderStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid purchase order status."));
    }
}
