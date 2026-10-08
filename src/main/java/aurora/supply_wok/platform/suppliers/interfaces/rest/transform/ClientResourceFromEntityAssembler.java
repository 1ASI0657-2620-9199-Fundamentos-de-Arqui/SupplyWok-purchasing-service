package aurora.supply_wok.platform.suppliers.interfaces.rest.transform;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Client;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.ClientResource;

/**
 * Assembler to convert a Client entity to a ClientResource.
 */
public class ClientResourceFromEntityAssembler {

    public static ClientResource toResourceFromEntity(Client entity) {
        return new ClientResource(entity.getId(), entity.getName(), entity.getDistrict(), entity.getStatus());
    }
}
