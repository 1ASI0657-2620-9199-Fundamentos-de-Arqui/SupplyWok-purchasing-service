package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities;

import aurora.supply_wok.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * JPA persistence entity for suppliers.
 */
@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
public class SupplierPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false, length = 100)
    private UUID uuid;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "contact_name", nullable = false, length = 100)
    private String contactName;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 40)
    private String phone;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(name = "linked_date", nullable = false, length = 20)
    private String linkedDate;

    @Column(nullable = false, length = 40)
    private String sla;

    @Column(name = "response_time", nullable = false, length = 40)
    private String responseTime;
}
