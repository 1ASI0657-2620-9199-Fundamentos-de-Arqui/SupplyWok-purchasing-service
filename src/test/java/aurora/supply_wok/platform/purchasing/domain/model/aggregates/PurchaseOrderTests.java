package aurora.supply_wok.platform.purchasing.domain.model.aggregates;

import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.entities.PurchaseOrderItem;
import aurora.supply_wok.platform.purchasing.domain.model.events.PurchaseOrderCreatedEvent;
import aurora.supply_wok.platform.purchasing.domain.model.events.PurchaseOrderStatusChangedEvent;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderPriority;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PurchaseOrderTests {

    @Test
    void canTransitionTo_whenSameStatus_returnsTrue() {
        // Arrange
        var order = new PurchaseOrder();
        order.setStatus(EPurchaseOrderStatus.Pending);

        // Act
        boolean canTransition = order.canTransitionTo(EPurchaseOrderStatus.Pending);

        // Assert
        assertThat(canTransition).isTrue();
    }

    @Test
    void canTransitionTo_whenTargetStatusIsNull_returnsFalse() {
        // Arrange
        var order = new PurchaseOrder();

        // Act
        boolean canTransition = order.canTransitionTo(null);

        // Assert
        assertThat(canTransition).isFalse();
    }

    @Test
    void canTransitionTo_whenOrderIsDelivered_returnsFalse() {
        // Arrange
        var order = new PurchaseOrder();
        order.setStatus(EPurchaseOrderStatus.Delivered);

        // Act
        boolean canTransition = order.canTransitionTo(EPurchaseOrderStatus.Delayed);

        // Assert
        assertThat(canTransition).isFalse();
    }

    @Test
    void canTransitionTo_whenPendingToConfirmed_returnsTrue() {
        // Arrange
        var order = new PurchaseOrder();
        order.setStatus(EPurchaseOrderStatus.Pending);

        // Act
        boolean canTransition = order.canTransitionTo(EPurchaseOrderStatus.Confirmed);

        // Assert
        assertThat(canTransition).isTrue();
    }

    @Test
    void canTransitionTo_whenPendingToInTransit_returnsFalse() {
        // Arrange
        var order = new PurchaseOrder();
        order.setStatus(EPurchaseOrderStatus.Pending);

        // Act
        boolean canTransition = order.canTransitionTo(EPurchaseOrderStatus.InTransit);

        // Assert
        assertThat(canTransition).isFalse();
    }

    @Test
    void canTransitionTo_whenInTransitToDelayed_returnsTrue() {
        // Arrange
        var order = new PurchaseOrder();
        order.setStatus(EPurchaseOrderStatus.InTransit);

        // Act
        boolean canTransition = order.canTransitionTo(EPurchaseOrderStatus.Delayed);

        // Assert
        assertThat(canTransition).isTrue();
    }

    @Test
    void canTransitionTo_whenInTransitToDelivered_returnsTrue() {
        // Arrange
        var order = new PurchaseOrder();
        order.setStatus(EPurchaseOrderStatus.InTransit);

        // Act
        boolean canTransition = order.canTransitionTo(EPurchaseOrderStatus.Delivered);

        // Assert
        assertThat(canTransition).isTrue();
    }

    @Test
    void updateStatus_whenTransitionIsInvalid_throwsIllegalArgumentException() {
        // Arrange
        var order = new PurchaseOrder();
        order.setStatus(EPurchaseOrderStatus.Pending);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> order.updateStatus(EPurchaseOrderStatus.Delivered));
    }

    @Test
    void updateStatus_whenTransitionIsValid_updatesStatusAndRegistersDomainEvent() {
        // Arrange
        var order = new PurchaseOrder();
        order.setStatus(EPurchaseOrderStatus.Pending);

        // Act
        order.updateStatus(EPurchaseOrderStatus.Confirmed);

        // Assert
        assertThat(order.getStatus()).isEqualTo(EPurchaseOrderStatus.Confirmed);
        assertThat(order.domainEvents()).hasSize(1);
        assertThat(order.domainEvents().iterator().next()).isInstanceOf(PurchaseOrderStatusChangedEvent.class);
    }

    @Test
    void updateInformation_whenStatusChanges_registersPurchaseOrderStatusChangedEvent() {
        // Arrange
        var order = new PurchaseOrder();
        order.setStatus(EPurchaseOrderStatus.Pending);
        var items = List.of(new PurchaseOrderItem(1L, "Wok Oil", BigDecimal.valueOf(5), BigDecimal.valueOf(10.0), "L"));

        // Act
        order.updateInformation(
                "PO-100",
                2L,
                "Supplier ABC",
                "Dragon Chifa",
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                EPurchaseOrderPriority.High,
                EPurchaseOrderStatus.Confirmed,
                items
        );

        // Assert
        assertThat(order.getCode()).isEqualTo("PO-100");
        assertThat(order.getStatus()).isEqualTo(EPurchaseOrderStatus.Confirmed);
        assertThat(order.domainEvents()).hasSize(1);
        assertThat(order.domainEvents().iterator().next()).isInstanceOf(PurchaseOrderStatusChangedEvent.class);
    }

    @Test
    void onCreated_whenInvoked_registersPurchaseOrderCreatedEvent() {
        // Arrange
        var order = new PurchaseOrder();

        // Act
        order.onCreated();

        // Assert
        assertThat(order.domainEvents()).hasSize(1);
        assertThat(order.domainEvents().iterator().next()).isInstanceOf(PurchaseOrderCreatedEvent.class);
    }
}
