package aurora.supply_wok.platform.suppliers.domain.repositories;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.SupplierClient;

/**
 * Supplier-client link repository port.
 */
public interface SupplierClientRepository {
    SupplierClient save(SupplierClient supplierClient);

    boolean existsBySupplierIdAndClientId(Long supplierId, Long clientId);
}
