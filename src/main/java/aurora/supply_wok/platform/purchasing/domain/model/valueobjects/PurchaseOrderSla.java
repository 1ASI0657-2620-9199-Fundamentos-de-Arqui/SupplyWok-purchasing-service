package aurora.supply_wok.platform.purchasing.domain.model.valueobjects;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Value object that summarizes supplier SLA based on purchase order outcomes.
 */
public class PurchaseOrderSla {

    private final Long supplierId;
    private final int deliveredOrders;
    private final int delayedOrders;
    private final int totalClosedOrders;
    private final BigDecimal complianceRate;

    public PurchaseOrderSla(Long supplierId, List<PurchaseOrder> purchaseOrders) {
        this.supplierId = supplierId;
        var supplierOrders = purchaseOrders.stream()
                .filter(order -> supplierId != null && supplierId.equals(order.getSupplierId()))
                .toList();
        this.deliveredOrders = (int) supplierOrders.stream()
                .filter(order -> order.getStatus() == EPurchaseOrderStatus.Delivered)
                .count();
        this.delayedOrders = (int) supplierOrders.stream()
                .filter(order -> order.getStatus() == EPurchaseOrderStatus.Delayed)
                .count();
        this.totalClosedOrders = deliveredOrders + delayedOrders;
        this.complianceRate = totalClosedOrders == 0
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(deliveredOrders)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalClosedOrders), 2, RoundingMode.HALF_UP);
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public int getDeliveredOrders() {
        return deliveredOrders;
    }

    public int getDelayedOrders() {
        return delayedOrders;
    }

    public int getTotalClosedOrders() {
        return totalClosedOrders;
    }

    public BigDecimal getComplianceRate() {
        return complianceRate;
    }
}
