package aurora.supply_wok.platform.purchasing;

import aurora.supply_wok.platform.purchasing.application.commandservices.PurchaseOrderCommandService;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderItemCommand;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderCreatedIntegrationEvent;
import aurora.supply_wok.platform.suppliers.application.commandservices.SupplierCommandService;
import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateSupplierCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RecordApplicationEvents
@Transactional
class PurchaseOrderCreatedIntegrationEventTests {

    @Autowired
    private ApplicationEvents applicationEvents;

    @Autowired
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Autowired
    private SupplierCommandService supplierCommandService;

    @Test
    void handle_whenPurchaseOrderCreated_publishesPurchaseOrderCreatedIntegrationEvent() {
        // Arrange
        var supplier = supplierCommandService.handle(new CreateSupplierCommand(
                "Insumos del Chef",
                "Juan Perez",
                "insumos-chef-event-test@restaurante.com",
                "+51987654321",
                "Verduras",
                "2026-08-01",
                "99%",
                "24h"
        ));

        String orderCode = "PO-INTEG-EVENT-999";
        var createCommand = new CreatePurchaseOrderCommand(
                orderCode,
                supplier.getId(),
                supplier.getName(),
                "Wok Central Miraflores",
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                "HIGH",
                "PENDING",
                List.of(new CreatePurchaseOrderItemCommand(
                        null,
                        10L,
                        "Cebollin Chino",
                        BigDecimal.valueOf(15),
                        BigDecimal.valueOf(3.20),
                        "KG"
                ))
        );

        // Act
        var createdOrder = purchaseOrderCommandService.handle(createCommand);

        // Assert
        assertThat(createdOrder).isPresent();

        var matchingEvents = applicationEvents.stream(PurchaseOrderCreatedIntegrationEvent.class)
                .filter(event -> orderCode.equals(event.code()))
                .toList();

        assertThat(matchingEvents).hasSize(1);
        var publishedEvent = matchingEvents.getFirst();
        assertThat(publishedEvent.purchaseOrderId()).isEqualTo(createdOrder.get().getId());
        assertThat(publishedEvent.supplierId()).isEqualTo(supplier.getId());
        assertThat(publishedEvent.supplierName()).isEqualTo("Insumos del Chef");
        assertThat(publishedEvent.restaurantName()).isEqualTo("Wok Central Miraflores");
        assertThat(publishedEvent.priority()).isEqualTo("High");
        assertThat(publishedEvent.status()).isEqualTo("Pending");
        assertThat(publishedEvent.restaurantId()).isEqualTo(1L);
        assertThat(publishedEvent.supplierEmail()).isEqualTo("insumos-chef-event-test@restaurante.com");
    }
}
