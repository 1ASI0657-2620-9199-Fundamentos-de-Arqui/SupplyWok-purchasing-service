package aurora.supply_wok.platform.suppliers.domain.repositories;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;

import java.util.List;
import java.util.Optional;

/**
 * Supplier repository port.
 */
public interface SupplierRepository {
    Supplier save(Supplier supplier);

    List<Supplier> findAll();

    Optional<Supplier> findById(Long id);

    Optional<Supplier> findByEmailIgnoreCase(String email);

    boolean existsById(Long id);
}
