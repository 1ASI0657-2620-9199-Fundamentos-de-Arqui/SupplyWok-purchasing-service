package aurora.supply_wok.platform.purchasing.domain.model.valueobjects;

/**
 * Supported purchase order statuses.
 */
public enum EPurchaseOrderStatus {
    Pending,
    Confirmed,
    InTransit,
    Delivered,
    Delayed
}
