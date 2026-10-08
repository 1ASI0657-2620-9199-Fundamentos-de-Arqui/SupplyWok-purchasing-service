package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities;

import aurora.supply_wok.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * JPA persistence entity for catalog items.
 */
@Entity
@Table(name = "catalog_items")
@Getter
@Setter
@NoArgsConstructor
public class CatalogItemPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private SupplierPersistenceEntity supplier;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ECatalogUnit unit;

    @Column(name = "delivery_conditions", nullable = false, length = 250)
    private String deliveryConditions;
}
