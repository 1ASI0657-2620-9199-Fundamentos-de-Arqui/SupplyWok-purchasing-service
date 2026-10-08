package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.assemblers;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;
import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities.CatalogItemPersistenceEntity;
import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities.SupplierPersistenceEntity;

/**
 * Static assembler between catalog item domain and persistence representations.
 */
public final class CatalogItemPersistenceAssembler {

    private CatalogItemPersistenceAssembler() {
    }

    public static CatalogItem toDomainFromPersistence(CatalogItemPersistenceEntity entity) {
        if (entity == null) return null;

        var catalogItem = new CatalogItem();
        catalogItem.setId(entity.getId());
        catalogItem.setSupplierId(entity.getSupplier().getId());
        catalogItem.setName(entity.getName());
        catalogItem.setCategory(entity.getCategory());
        catalogItem.setPrice(entity.getPrice());
        catalogItem.setUnit(entity.getUnit());
        catalogItem.setDeliveryConditions(entity.getDeliveryConditions());
        return catalogItem;
    }

    public static CatalogItemPersistenceEntity toPersistenceFromDomain(CatalogItem catalogItem) {
        if (catalogItem == null) return null;

        var entity = new CatalogItemPersistenceEntity();
        if (catalogItem.getId() != null) {
            entity.setId(catalogItem.getId());
        }

        var supplier = new SupplierPersistenceEntity();
        supplier.setId(catalogItem.getSupplierId());

        entity.setSupplier(supplier);
        entity.setName(catalogItem.getName());
        entity.setCategory(catalogItem.getCategory());
        entity.setPrice(catalogItem.getPrice());
        entity.setUnit(catalogItem.getUnit());
        entity.setDeliveryConditions(catalogItem.getDeliveryConditions());
        return entity;
    }
}
