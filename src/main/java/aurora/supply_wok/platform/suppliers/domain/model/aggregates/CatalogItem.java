package aurora.supply_wok.platform.suppliers.domain.model.aggregates;

import aurora.supply_wok.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.UpdateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.events.CatalogItemCreatedEvent;
import aurora.supply_wok.platform.suppliers.domain.model.events.CatalogItemUpdatedEvent;
import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Catalog item aggregate root.
 */
@Getter
public class CatalogItem extends AbstractDomainAggregateRoot<CatalogItem> {

    @Setter
    private Long id;

    @Setter
    private Long supplierId;

    @Setter
    private String name;

    @Setter
    private String category;

    @Setter
    private BigDecimal price;

    @Setter
    private ECatalogUnit unit;

    @Setter
    private String deliveryConditions;

    public CatalogItem() {
        this.supplierId = 0L;
        this.name = "";
        this.category = "";
        this.price = BigDecimal.ZERO;
        this.unit = ECatalogUnit.KG;
        this.deliveryConditions = "";
    }

    public CatalogItem(Long supplierId, String name, String category, BigDecimal price, ECatalogUnit unit, String deliveryConditions) {
        this();
        assignSupplierId(supplierId);
        updateCore(name, category, price, unit, deliveryConditions);
    }

    public CatalogItem(CreateCatalogItemCommand command) {
        this(command.supplierId(), command.name(), command.category(), command.price(), command.unit(), command.deliveryConditions());
    }

    public CatalogItem updateInformation(UpdateCatalogItemCommand command) {
        updateCore(command.name(), command.category(), command.price(), command.unit(), command.deliveryConditions());
        registerDomainEvent(CatalogItemUpdatedEvent.from(this));
        return this;
    }

    public void onCreated() {
        registerDomainEvent(CatalogItemCreatedEvent.from(this));
    }

    private void assignSupplierId(Long supplierId) {
        if (supplierId == null || supplierId <= 0) {
            throw new IllegalArgumentException("Supplier id must be greater than zero.");
        }
        this.supplierId = supplierId;
    }

    private void updateCore(String name, String category, BigDecimal price, ECatalogUnit unit, String deliveryConditions) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Catalog item name cannot be empty.");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Catalog item category cannot be empty.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Catalog item price must be greater than zero.");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Catalog item unit is required.");
        }
        if (deliveryConditions == null || deliveryConditions.isBlank()) {
            throw new IllegalArgumentException("Delivery conditions cannot be empty.");
        }

        this.name = name;
        this.category = category;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.unit = unit;
        this.deliveryConditions = deliveryConditions;
    }
}
