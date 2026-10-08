package aurora.supply_wok.platform.purchasing.application.internal.eventhandlers;

import aurora.supply_wok.platform.purchasing.domain.model.events.PurchaseOrderCreatedEvent;
import aurora.supply_wok.platform.purchasing.domain.model.events.PurchaseOrderStatusChangedEvent;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderCreatedIntegrationEvent;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderStatusChangedIntegrationEvent;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Publishes purchase-order domain events as integration events.
 */
@Service
public class PurchaseOrderDomainEventHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final SupplierRepository supplierRepository;

    public PurchaseOrderDomainEventHandler(ApplicationEventPublisher eventPublisher,
                                           SupplierRepository supplierRepository) {
        this.eventPublisher = eventPublisher;
        this.supplierRepository = supplierRepository;
    }

    @EventListener
    public void on(PurchaseOrderCreatedEvent event) {
        String supplierEmail = resolveSupplierEmail(event.supplierId());
        eventPublisher.publishEvent(new PurchaseOrderCreatedIntegrationEvent(
                event.purchaseOrderId(),
                event.code(),
                event.supplierId(),
                event.supplierName(),
                event.restaurantName(),
                event.priority(),
                event.status(),
                event.restaurantId(),
                supplierEmail
        ));
    }

    @EventListener
    public void on(PurchaseOrderStatusChangedEvent event) {
        String supplierEmail = resolveSupplierEmail(event.supplierId());
        eventPublisher.publishEvent(new PurchaseOrderStatusChangedIntegrationEvent(
                event.purchaseOrderId(),
                event.code(),
                event.supplierId(),
                event.supplierName(),
                event.restaurantName(),
                event.previousStatus().name(),
                event.currentStatus().name(),
                event.restaurantId(),
                supplierEmail
        ));
    }

    private String resolveSupplierEmail(Long supplierId) {
        if (supplierId == null) {
            return "";
        }
        return supplierRepository.findById(supplierId)
                .map(Supplier::getEmail)
                .orElse("");
    }
}
