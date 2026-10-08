package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.assemblers;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Client;
import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities.ClientPersistenceEntity;

/**
 * Static assembler between client domain and persistence representations.
 */
public final class ClientPersistenceAssembler {

    private ClientPersistenceAssembler() {
    }

    public static Client toDomainFromPersistence(ClientPersistenceEntity entity) {
        if (entity == null) return null;

        var client = new Client();
        client.setId(entity.getId());
        client.setName(entity.getName());
        client.setDistrict(entity.getDistrict());
        client.setStatus(entity.getStatus());
        return client;
    }

    public static ClientPersistenceEntity toPersistenceFromDomain(Client client) {
        if (client == null) return null;

        var entity = new ClientPersistenceEntity();
        if (client.getId() != null) {
            entity.setId(client.getId());
        }
        entity.setName(client.getName());
        entity.setDistrict(client.getDistrict());
        entity.setStatus(client.getStatus());
        return entity;
    }
}
