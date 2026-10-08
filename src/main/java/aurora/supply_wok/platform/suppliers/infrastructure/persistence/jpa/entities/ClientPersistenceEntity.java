package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities;

import aurora.supply_wok.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity for clients.
 */
@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class ClientPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 80)
    private String district;

    @Column(nullable = false, length = 20)
    private String status;
}
