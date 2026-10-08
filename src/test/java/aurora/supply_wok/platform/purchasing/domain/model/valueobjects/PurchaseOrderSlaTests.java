package aurora.supply_wok.platform.purchasing.domain.model.valueobjects;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseOrderSlaTests {

    @Test
    void constructor_whenNoOrdersForSupplier_returnsZeroComplianceRate() {
        // Arrange
        Long supplierId = 10L;
        List<PurchaseOrder> orders = Collections.emptyList();

        // Act
        var sla = new PurchaseOrderSla(supplierId, orders);

        // Assert
        assertThat(sla.getComplianceRate()).isEqualTo(new BigDecimal("0.00"));
        assertThat(sla.getTotalClosedOrders()).isZero();
    }

    @Test
    void constructor_whenOnlyDeliveredOrders_returnsHundredPercentComplianceRate() {
        // Arrange
        Long supplierId = 10L;
        var order1 = new PurchaseOrder();
        order1.setSupplierId(supplierId);
        order1.setStatus(EPurchaseOrderStatus.Delivered);

        var order2 = new PurchaseOrder();
        order2.setSupplierId(supplierId);
        order2.setStatus(EPurchaseOrderStatus.Delivered);

        // Act
        var sla = new PurchaseOrderSla(supplierId, List.of(order1, order2));

        // Assert
        assertThat(sla.getComplianceRate()).isEqualTo(new BigDecimal("100.00"));
        assertThat(sla.getDeliveredOrders()).isEqualTo(2);
        assertThat(sla.getDelayedOrders()).isZero();
        assertThat(sla.getTotalClosedOrders()).isEqualTo(2);
    }

    @Test
    void constructor_whenMixedDeliveredAndDelayedOrders_calculatesCorrectComplianceRate() {
        // Arrange
        Long supplierId = 5L;
        var order1 = new PurchaseOrder();
        order1.setSupplierId(supplierId);
        order1.setStatus(EPurchaseOrderStatus.Delivered);

        var order2 = new PurchaseOrder();
        order2.setSupplierId(supplierId);
        order2.setStatus(EPurchaseOrderStatus.Delayed);

        var order3 = new PurchaseOrder();
        order3.setSupplierId(supplierId);
        order3.setStatus(EPurchaseOrderStatus.Pending); // open, not closed

        // Act
        var sla = new PurchaseOrderSla(supplierId, List.of(order1, order2, order3));

        // Assert
        assertThat(sla.getComplianceRate()).isEqualTo(new BigDecimal("50.00"));
        assertThat(sla.getDeliveredOrders()).isEqualTo(1);
        assertThat(sla.getDelayedOrders()).isEqualTo(1);
        assertThat(sla.getTotalClosedOrders()).isEqualTo(2);
    }

    @Test
    void constructor_whenOrdersBelongToDifferentSuppliers_filtersOnlyTargetSupplier() {
        // Arrange
        Long targetSupplierId = 1L;
        Long otherSupplierId = 2L;

        var targetOrder = new PurchaseOrder();
        targetOrder.setSupplierId(targetSupplierId);
        targetOrder.setStatus(EPurchaseOrderStatus.Delivered);

        var otherOrder = new PurchaseOrder();
        otherOrder.setSupplierId(otherSupplierId);
        otherOrder.setStatus(EPurchaseOrderStatus.Delayed);

        // Act
        var sla = new PurchaseOrderSla(targetSupplierId, List.of(targetOrder, otherOrder));

        // Assert
        assertThat(sla.getDeliveredOrders()).isEqualTo(1);
        assertThat(sla.getDelayedOrders()).isZero();
        assertThat(sla.getComplianceRate()).isEqualTo(new BigDecimal("100.00"));
    }
}
