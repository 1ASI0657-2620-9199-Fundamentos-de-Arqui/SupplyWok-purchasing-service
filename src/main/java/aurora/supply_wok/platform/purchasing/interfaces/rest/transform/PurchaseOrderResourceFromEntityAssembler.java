package aurora.supply_wok.platform.purchasing.interfaces.rest.transform;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.PurchaseOrderItemResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.PurchaseOrderResource;

/**
 * Assembler to convert a PurchaseOrder entity to a PurchaseOrderResource.
 */
public class PurchaseOrderResourceFromEntityAssembler {

    public static PurchaseOrderResource toResourceFromEntity(PurchaseOrder entity) {
        return new PurchaseOrderResource(
                entity.getId(),
                entity.getCode(),
                entity.getSupplierId(),
                entity.getSupplierName(),
                entity.getRestaurantName(),
                entity.getOrderDate() == null ? null : entity.getOrderDate().toString(),
                entity.getEstimatedDate() == null ? "" : entity.getEstimatedDate().toString(),
                entity.getPriority().name(),
                entity.getStatus() == null ? null : toStatusLabel(entity.getStatus().name()),
                entity.getItems().stream()
                        .map(item -> new PurchaseOrderItemResource(
                                item.getId(),
                                item.getInventoryItemId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getUnitType()
                        ))
                        .toList()
        );
    }

    private static String toStatusLabel(String statusName) {
        return "InTransit".equals(statusName) ? "In Transit" : statusName;
    }
}
