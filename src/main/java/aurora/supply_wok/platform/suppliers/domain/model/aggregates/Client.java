package aurora.supply_wok.platform.suppliers.domain.model.aggregates;

import aurora.supply_wok.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Client aggregate root.
 */
@Getter
public class Client extends AbstractDomainAggregateRoot<Client> {

    @Setter
    private Long id;

    @Setter
    private String name;

    @Setter
    private String district;

    @Setter
    private String status;

    public Client() {
        this.name = "";
        this.district = "";
        this.status = "";
    }

    public Client(String name, String district, String status) {
        this.name = name;
        this.district = district;
        this.status = status;
    }
}
