package aurora.supply_wok.platform.suppliers.domain.model.aggregates;

import aurora.supply_wok.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Supplier-client link aggregate.
 */
@Getter
public class SupplierClient extends AbstractDomainAggregateRoot<SupplierClient> {

    @Setter
    private Long id;

    @Setter
    private Long supplierId;

    @Setter
    private Long clientId;

    public SupplierClient() {
    }

    public SupplierClient(Long supplierId, Long clientId) {
        this.supplierId = supplierId;
        this.clientId = clientId;
    }
}
