package aurora.supply_wok.platform.purchasing.interfaces.acl;

import aurora.supply_wok.platform.purchasing.application.commandservices.PurchaseOrderCommandService;
import aurora.supply_wok.platform.purchasing.application.queryservices.PurchaseOrderQueryService;
import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderItemCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.UpdatePurchaseOrderStatusCommand;
import aurora.supply_wok.platform.purchasing.domain.model.entities.PurchaseOrderItem;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrdersBySupplierIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderPriority;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchasingContextFacadeTests {

    @Mock
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Mock
    private PurchaseOrderQueryService purchaseOrderQueryService;

    @InjectMocks
    private PurchasingContextFacade purchasingContextFacade;

    @Test
    void createPurchaseOrder_whenCommandSucceeds_returnsOrderId() {
        // Arrange
        var order = new PurchaseOrder();
        order.setId(77L);
        when(purchaseOrderCommandService.handle(any(CreatePurchaseOrderCommand.class))).thenReturn(Optional.of(order));

        // Act
        var result = purchasingContextFacade.createPurchaseOrder(
                "PO-ACL-1",
                2L,
                "Supplier A",
                "Restaurant B",
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                "High",
                List.of(new CreatePurchaseOrderItemCommand(null, 1L, "Aceite", BigDecimal.ONE, BigDecimal.TEN, "L"))
        );

        // Assert
        assertThat(result).contains(77L);
        verify(purchaseOrderCommandService).handle(any(CreatePurchaseOrderCommand.class));
    }

    @Test
    void getPurchaseOrdersBySupplierId_whenOrdersFound_returnsMappedAclResources() {
        // Arrange
        var order = new PurchaseOrder();
        order.setId(10L);
        order.setCode("PO-10");
        order.setSupplierId(3L);
        order.setSupplierName("Supplier 3");
        order.setRestaurantName("Chifa 3");
        order.setOrderDate(LocalDate.of(2026, 4, 1));
        order.setEstimatedDate(LocalDate.of(2026, 4, 3));
        order.setPriority(EPurchaseOrderPriority.Medium);
        order.setStatus(EPurchaseOrderStatus.InTransit);
        order.setItems(List.of(new PurchaseOrderItem(5L, "Pollo", BigDecimal.TEN, BigDecimal.TWO, "KG")));

        when(purchaseOrderQueryService.handle(any(GetPurchaseOrdersBySupplierIdQuery.class)))
                .thenReturn(List.of(order));

        // Act
        var result = purchasingContextFacade.getPurchaseOrdersBySupplierId(3L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().status()).isEqualTo("In Transit");
        assertThat(result.getFirst().code()).isEqualTo("PO-10");
        verify(purchaseOrderQueryService).handle(any(GetPurchaseOrdersBySupplierIdQuery.class));
    }

    @Test
    void updatePurchaseOrderStatus_whenCommandHandled_returnsTrue() {
        // Arrange
        var order = new PurchaseOrder();
        order.setId(10L);
        when(purchaseOrderCommandService.handle(any(UpdatePurchaseOrderStatusCommand.class)))
                .thenReturn(Optional.of(order));

        // Act
        boolean updated = purchasingContextFacade.updatePurchaseOrderStatus(10L, "Confirmed");

        // Assert
        assertThat(updated).isTrue();
        verify(purchaseOrderCommandService).handle(any(UpdatePurchaseOrderStatusCommand.class));
    }

    @Test
    void calculateSupplierSla_whenOrdersQueried_returnsCalculatedRate() {
        // Arrange
        var deliveredOrder = new PurchaseOrder();
        deliveredOrder.setSupplierId(3L);
        deliveredOrder.setStatus(EPurchaseOrderStatus.Delivered);

        when(purchaseOrderQueryService.handle(any(GetPurchaseOrdersBySupplierIdQuery.class)))
                .thenReturn(List.of(deliveredOrder));

        // Act
        var rate = purchasingContextFacade.calculateSupplierSla(3L);

        // Assert
        assertThat(rate).isEqualTo(new BigDecimal("100.00"));
    }
}
