package aurora.supply_wok.platform.suppliers.application.internal.commandservices;

import aurora.supply_wok.platform.suppliers.application.commandservices.CatalogItemCommandService;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;
import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.DeleteCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.UpdateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.repositories.CatalogItemRepository;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the CatalogItemCommandService interface.
 */
@Service
public class CatalogItemCommandServiceImpl implements CatalogItemCommandService {

    private final CatalogItemRepository catalogItemRepository;
    private final SupplierRepository supplierRepository;

    public CatalogItemCommandServiceImpl(CatalogItemRepository catalogItemRepository, SupplierRepository supplierRepository) {
        this.catalogItemRepository = catalogItemRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Optional<Long> handle(CreateCatalogItemCommand command) {
        if (!supplierRepository.existsById(command.supplierId())) {
            return Optional.empty();
        }

        var catalogItem = new CatalogItem(command);
        var savedCatalogItem = catalogItemRepository.save(catalogItem);
        return Optional.ofNullable(savedCatalogItem.getId());
    }

    @Override
    public Optional<CatalogItem> handle(UpdateCatalogItemCommand command) {
        if (!supplierRepository.existsById(command.supplierId())) {
            return Optional.empty();
        }

        var catalogItem = catalogItemRepository.findByIdAndSupplierId(command.catalogItemId(), command.supplierId());
        if (catalogItem.isEmpty()) {
            return Optional.empty();
        }

        var updated = catalogItem.get().updateInformation(command);
        return Optional.of(catalogItemRepository.save(updated));
    }

    @Override
    public boolean handle(DeleteCatalogItemCommand command) {
        if (!supplierRepository.existsById(command.supplierId())) {
            return false;
        }

        var catalogItem = catalogItemRepository.findByIdAndSupplierId(command.catalogItemId(), command.supplierId());
        if (catalogItem.isEmpty()) {
            return false;
        }

        catalogItemRepository.delete(catalogItem.get());
        return true;
    }
}
