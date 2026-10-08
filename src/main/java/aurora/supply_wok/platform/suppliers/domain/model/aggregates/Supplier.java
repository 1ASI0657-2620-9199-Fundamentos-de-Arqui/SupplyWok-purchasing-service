package aurora.supply_wok.platform.suppliers.domain.model.aggregates;

import aurora.supply_wok.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import aurora.supply_wok.platform.suppliers.domain.model.events.SupplierCreatedEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Supplier aggregate root.
 */
@Getter
public class Supplier extends AbstractDomainAggregateRoot<Supplier> {

    @Setter
    private Long id;

    @Setter
    private UUID uuid;

    @Setter
    private String name;

    @Setter
    private String contactName;

    @Setter
    private String email;

    @Setter
    private String phone;

    @Setter
    private String category;

    @Setter
    private String linkedDate;

    @Setter
    private String sla;

    @Setter
    private String responseTime;

    public Supplier() {
        this.uuid = null;
        this.name = "";
        this.contactName = "";
        this.email = "";
        this.phone = "";
        this.category = "";
        this.linkedDate = "";
        this.sla = "";
        this.responseTime = "";
    }

    public Supplier(UUID uuid, String name, String contactName, String email, String phone, String category,
                    String linkedDate, String sla, String responseTime) {
        this.name = name;
        this.uuid = uuid;
        this.contactName = contactName;
        this.email = email;
        this.phone = phone;
        this.category = category;
        this.linkedDate = linkedDate;
        this.sla = sla;
        this.responseTime = responseTime;
    }

    public void onCreated() {
        registerDomainEvent(SupplierCreatedEvent.from(this));
    }

    public void updateFromProfile(String name, String contactName, String email, String phone, String category,
                                  String sla, String responseTime) {
        this.name = name;
        this.contactName = contactName;
        this.email = email;
        this.phone = phone;
        this.category = category;
        this.sla = sla;
        this.responseTime = responseTime;
    }
}
