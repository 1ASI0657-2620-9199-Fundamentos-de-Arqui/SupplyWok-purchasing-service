package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities;

import aurora.supply_wok.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity for supplier-client links.
 */
@Entity
@Table(
        name = "supplier_clients",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_supplier_clients_supplier_id_client_id",
                columnNames = {"supplier_id", "client_id"}
        ),
        indexes = {
                @Index(name = "idx_supplier_clients_supplier_id", columnList = "supplier_id"),
                @Index(name = "idx_supplier_clients_client_id", columnList = "client_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SupplierClientPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private SupplierPersistenceEntity supplier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientPersistenceEntity client;
}
