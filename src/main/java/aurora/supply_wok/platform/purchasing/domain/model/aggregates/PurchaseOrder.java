package aurora.supply_wok.platform.purchasing.domain.model.aggregates;

import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.entities.PurchaseOrderItem;
import aurora.supply_wok.platform.purchasing.domain.model.events.PurchaseOrderCreatedEvent;
import aurora.supply_wok.platform.purchasing.domain.model.events.PurchaseOrderStatusChangedEvent;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderPriority;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderStatus;
import aurora.supply_wok.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Purchase order aggregate root.
 */
@Getter
public class PurchaseOrder extends AbstractDomainAggregateRoot<PurchaseOrder> {

    @Setter
    private Long id;

    @Setter
    private String code;

    @Setter
    private Long supplierId;

    @Setter
    private String supplierName;

    @Setter
    private String restaurantName;

    @Setter
    private LocalDate orderDate;

    @Setter
    private LocalDate estimatedDate;

    @Setter
    private EPurchaseOrderPriority priority;

    @Setter
    private EPurchaseOrderStatus status;

    @Setter
    private List<PurchaseOrderItem> items;

    public PurchaseOrder() {
        this.code = "";
        this.supplierId = 0L;
        this.supplierName = "";
        this.restaurantName = "";
        this.orderDate = LocalDate.now();
        this.estimatedDate = null;
        this.priority = EPurchaseOrderPriority.Medium;
        this.status = EPurchaseOrderStatus.Pending;
        this.items = new ArrayList<>();
    }

    public PurchaseOrder(
            String code,
            Long supplierId,
            String supplierName,
            String restaurantName,
            LocalDate orderDate,
            LocalDate estimatedDate,
            EPurchaseOrderPriority priority,
            EPurchaseOrderStatus status,
            List<PurchaseOrderItem> items
    ) {
        this();
        this.code = code;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.restaurantName = restaurantName;
        this.orderDate = orderDate;
        this.estimatedDate = estimatedDate;
        this.priority = priority;
        this.status = status;
        replaceItems(items);
    }

    public PurchaseOrder(CreatePurchaseOrderCommand command, String resolvedSupplierName,
                         EPurchaseOrderPriority resolvedPriority, EPurchaseOrderStatus resolvedStatus,
                         List<PurchaseOrderItem> items) {
        this(
                command.code(),
                command.supplierId(),
                resolvedSupplierName,
                command.restaurantName(),
                command.orderDate(),
                command.estimatedDate(),
                resolvedPriority,
                resolvedStatus,
                items
        );
    }

    public PurchaseOrder updateInformation(
            String code,
            Long supplierId,
            String supplierName,
            String restaurantName,
            LocalDate orderDate,
            LocalDate estimatedDate,
            EPurchaseOrderPriority priority,
            EPurchaseOrderStatus status,
            List<PurchaseOrderItem> items
    ) {
        var previousStatus = this.status;
        this.code = code;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.restaurantName = restaurantName;
        this.orderDate = orderDate;
        this.estimatedDate = estimatedDate;
        this.priority = priority;
        this.status = status;
        replaceItems(items);
        if (previousStatus != null && previousStatus != status) {
            registerDomainEvent(PurchaseOrderStatusChangedEvent.from(this, previousStatus));
        }
        return this;
    }

    public boolean canTransitionTo(EPurchaseOrderStatus nextStatus) {
        if (nextStatus == null) {
            return false;
        }
        if (status == nextStatus) {
            return true;
        }
        if (status == EPurchaseOrderStatus.Delivered) {
            return false;
        }
        if (nextStatus == EPurchaseOrderStatus.Delayed) {
            return status == EPurchaseOrderStatus.Pending
                    || status == EPurchaseOrderStatus.Confirmed
                    || status == EPurchaseOrderStatus.InTransit;
        }

        return switch (status) {
            case Pending -> nextStatus == EPurchaseOrderStatus.Confirmed;
            case Confirmed -> nextStatus == EPurchaseOrderStatus.InTransit;
            case InTransit -> nextStatus == EPurchaseOrderStatus.Delivered;
            case Delayed, Delivered -> false;
        };
    }

    public void updateStatus(EPurchaseOrderStatus nextStatus) {
        if (!canTransitionTo(nextStatus)) {
            throw new IllegalArgumentException("Invalid purchase order status transition.");
        }

        var previousStatus = this.status;
        this.status = nextStatus;
        if (previousStatus != nextStatus) {
            registerDomainEvent(PurchaseOrderStatusChangedEvent.from(this, previousStatus));
        }
    }

    public void onCreated() {
        registerDomainEvent(PurchaseOrderCreatedEvent.from(this));
    }

    private void replaceItems(List<PurchaseOrderItem> nextItems) {
        this.items = nextItems == null ? new ArrayList<>() : new ArrayList<>(nextItems);
    }
}
