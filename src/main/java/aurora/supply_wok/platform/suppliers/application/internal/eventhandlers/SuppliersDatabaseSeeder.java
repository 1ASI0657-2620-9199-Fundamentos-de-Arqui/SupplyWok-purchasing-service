package aurora.supply_wok.platform.suppliers.application.internal.eventhandlers;

import aurora.supply_wok.platform.suppliers.application.commandservices.CatalogItemCommandService;
import aurora.supply_wok.platform.suppliers.application.commandservices.SupplierCommandService;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateSupplierCommand;
import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import aurora.supply_wok.platform.suppliers.domain.repositories.CatalogItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class SuppliersDatabaseSeeder {
    private final SupplierRepository supplierRepository;
    private final SupplierCommandService supplierCommandService;
    private final CatalogItemCommandService catalogItemCommandService;
    private final CatalogItemRepository catalogItemRepository;

    public SuppliersDatabaseSeeder(
            SupplierRepository supplierRepository,
            SupplierCommandService supplierCommandService,
            CatalogItemCommandService catalogItemCommandService,
            CatalogItemRepository catalogItemRepository) {
        this.supplierRepository = supplierRepository;
        this.supplierCommandService = supplierCommandService;
        this.catalogItemCommandService = catalogItemCommandService;
        this.catalogItemRepository = catalogItemRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void on(ApplicationReadyEvent event) {
        String email = "proveedor@ejemplo.com";
        Supplier supplier = supplierRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    log.info("Supplier not found, creating during seeding...");
                    return supplierCommandService.handle(new CreateSupplierCommand(
                            "Verduras Frescas S.A.",
                            "Carlos Gomez",
                            email,
                            "+51888888888",
                            "Verduras",
                            "2026-07-08",
                            "99%",
                            "24h"
                    ));
                });

        if (catalogItemRepository.findAllBySupplierId(supplier.getId()).isEmpty()) {
            log.info("Seeding supplier catalog items...");
            catalogItemCommandService.handle(new CreateCatalogItemCommand(
                    supplier.getId(),
                    "Tomate Premium",
                    "Verduras",
                    BigDecimal.valueOf(2.50),
                    ECatalogUnit.KG,
                    "Entregas en la mañana"
            ));

            catalogItemCommandService.handle(new CreateCatalogItemCommand(
                    supplier.getId(),
                    "Cebolla Blanca",
                    "Verduras",
                    BigDecimal.valueOf(1.50),
                    ECatalogUnit.KG,
                    "Entregas en la mañana"
            ));
            log.info("Supplier catalog items seeded successfully.");
        }
    }
}
