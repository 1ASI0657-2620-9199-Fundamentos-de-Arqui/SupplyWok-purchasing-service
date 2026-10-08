package aurora.supply_wok.platform.suppliers.domain.repositories;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Client;

import java.util.List;
import java.util.Optional;

/**
 * Client repository port.
 */
public interface ClientRepository {
    Client save(Client client);

    List<Client> findAllBySupplierId(Long supplierId);

    Optional<Client> findByNameIgnoreCase(String name);
}
