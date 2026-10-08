package aurora.supply_wok.platform.purchasing.application.internal.eventhandlers;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.events.PurchaseOrderCreatedEvent;
import aurora.supply_wok.platform.purchasing.domain.model.events.PurchaseOrderStatusChangedEvent;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderPriority;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderStatus;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderCreatedIntegrationEvent;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderStatusChangedIntegrationEvent;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderDomainEventHandlerTests {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private PurchaseOrderDomainEventHandler handler;

    @Test
    void on_whenPurchaseOrderCreatedEvent_publishesIntegrationEvent() {
        // Arrange
        var supplier = new Supplier();
        supplier.setId(1L);
        supplier.setEmail("supplier@test.com");
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        var order = new PurchaseOrder("PO-301", 1L, "Sup", "Rest", null, null,
                EPurchaseOrderPriority.High, EPurchaseOrderStatus.Pending, Collections.emptyList(), 5L);
        order.setId(301L);
        var domainEvent = PurchaseOrderCreatedEvent.from(order);

        // Act
        handler.on(domainEvent);

        // Assert
        var captor = ArgumentCaptor.forClass(PurchaseOrderCreatedIntegrationEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        var published = captor.getValue();
        assertThat(published.purchaseOrderId()).isEqualTo(301L);
        assertThat(published.code()).isEqualTo("PO-301");
        assertThat(published.supplierId()).isEqualTo(1L);
        assertThat(published.restaurantId()).isEqualTo(5L);
        assertThat(published.supplierEmail()).isEqualTo("supplier@test.com");
    }

    @Test
    void on_whenPurchaseOrderStatusChangedEvent_publishesIntegrationEvent() {
        // Arrange
        var supplier = new Supplier();
        supplier.setId(1L);
        supplier.setEmail("supplier@test.com");
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        var order = new PurchaseOrder("PO-302", 1L, "Sup", "Rest", null, null,
                EPurchaseOrderPriority.Medium, EPurchaseOrderStatus.Confirmed, Collections.emptyList(), 5L);
        order.setId(302L);
        var domainEvent = PurchaseOrderStatusChangedEvent.from(order, EPurchaseOrderStatus.Pending);

        // Act
        handler.on(domainEvent);

        // Assert
        var captor = ArgumentCaptor.forClass(PurchaseOrderStatusChangedIntegrationEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        var published = captor.getValue();
        assertThat(published.purchaseOrderId()).isEqualTo(302L);
        assertThat(published.previousStatus()).isEqualTo("Pending");
        assertThat(published.currentStatus()).isEqualTo("Confirmed");
        assertThat(published.restaurantId()).isEqualTo(5L);
        assertThat(published.supplierEmail()).isEqualTo("supplier@test.com");
    }
}
