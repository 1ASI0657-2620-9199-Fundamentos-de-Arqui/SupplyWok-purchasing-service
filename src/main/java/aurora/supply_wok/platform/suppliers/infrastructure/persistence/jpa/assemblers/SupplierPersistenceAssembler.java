package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.assemblers;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities.SupplierPersistenceEntity;

/**
 * Static assembler between supplier domain and persistence representations.
 */
public final class SupplierPersistenceAssembler {

    private SupplierPersistenceAssembler() {
    }

    public static Supplier toDomainFromPersistence(SupplierPersistenceEntity entity) {
        if (entity == null) return null;

        var supplier = new Supplier();
        supplier.setId(entity.getId());
        supplier.setUuid(entity.getUuid());
        supplier.setName(entity.getName());
        supplier.setContactName(entity.getContactName());
        supplier.setEmail(entity.getEmail());
        supplier.setPhone(entity.getPhone());
        supplier.setCategory(entity.getCategory());
        supplier.setLinkedDate(entity.getLinkedDate());
        supplier.setSla(entity.getSla());
        supplier.setResponseTime(entity.getResponseTime());
        return supplier;
    }

    public static SupplierPersistenceEntity toPersistenceFromDomain(Supplier supplier) {
        if (supplier == null) return null;

        var entity = new SupplierPersistenceEntity();
        if (supplier.getId() != null) {
            entity.setId(supplier.getId());
        }
        entity.setUuid(supplier.getUuid());
        entity.setName(supplier.getName());
        entity.setContactName(supplier.getContactName());
        entity.setEmail(supplier.getEmail());
        entity.setPhone(supplier.getPhone());
        entity.setCategory(supplier.getCategory());
        entity.setLinkedDate(supplier.getLinkedDate());
        entity.setSla(supplier.getSla());
        entity.setResponseTime(supplier.getResponseTime());
        return entity;
    }
}
