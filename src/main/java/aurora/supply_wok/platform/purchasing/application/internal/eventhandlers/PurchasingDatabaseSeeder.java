package aurora.supply_wok.platform.purchasing.application.internal.eventhandlers;

import aurora.supply_wok.platform.purchasing.application.commandservices.PurchaseOrderCommandService;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderItemCommand;
import aurora.supply_wok.platform.purchasing.infrastructure.persistence.jpa.repositories.PurchaseOrderPersistenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class PurchasingDatabaseSeeder {
    private final PurchaseOrderPersistenceRepository purchaseOrderPersistenceRepository;
    private final PurchaseOrderCommandService purchaseOrderCommandService;

    public PurchasingDatabaseSeeder(
            PurchaseOrderPersistenceRepository purchaseOrderPersistenceRepository,
            PurchaseOrderCommandService purchaseOrderCommandService) {
        this.purchaseOrderPersistenceRepository = purchaseOrderPersistenceRepository;
        this.purchaseOrderCommandService = purchaseOrderCommandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void on(ApplicationReadyEvent event) {
        if (purchaseOrderPersistenceRepository.findAll().isEmpty()) {
            log.info("Seeding purchase orders...");
            purchaseOrderCommandService.handle(new CreatePurchaseOrderCommand(
                    "PO-2026-001",
                    1L,
                    "Verduras Frescas S.A.",
                    "Wok Restaurante Central",
                    LocalDate.now(),
                    LocalDate.now().plusDays(2),
                    "HIGH",
                    "PENDING",
                    List.of(new CreatePurchaseOrderItemCommand(
                            null,
                            1L,
                            "Tomate Premium",
                            BigDecimal.valueOf(20),
                            BigDecimal.valueOf(2.50),
                            "KG"
                    ))
            ));
            log.info("Purchase orders seeded successfully.");
        }
    }
}
