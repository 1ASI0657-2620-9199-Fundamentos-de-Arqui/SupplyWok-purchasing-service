package aurora.supply_wok.platform.purchasing.domain.model.entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Purchase order line item entity.
 */
@Getter
public class PurchaseOrderItem {

    @Setter
    private Long id;

    @Setter
    private Long inventoryItemId;

    @Setter
    private String productName;

    @Setter
    private BigDecimal quantity;

    @Setter
    private BigDecimal unitPrice;

    @Setter
    private String unitType;

    public PurchaseOrderItem() {
        this.inventoryItemId = null;
        this.productName = "";
        this.quantity = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.unitPrice = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.unitType = "";
    }

    public PurchaseOrderItem(Long inventoryItemId, String productName, BigDecimal quantity, BigDecimal unitPrice, String unitType) {
        this();
        this.inventoryItemId = inventoryItemId;
        this.productName = productName;
        this.quantity = normalize(quantity);
        this.unitPrice = normalize(unitPrice);
        this.unitType = unitType;
    }

    private BigDecimal normalize(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }
}
