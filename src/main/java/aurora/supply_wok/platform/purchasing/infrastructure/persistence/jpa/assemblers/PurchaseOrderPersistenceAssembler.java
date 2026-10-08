package aurora.supply_wok.platform.purchasing.infrastructure.persistence.jpa.assemblers;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.entities.PurchaseOrderItem;
import aurora.supply_wok.platform.purchasing.infrastructure.persistence.jpa.entities.PurchaseOrderItemPersistenceEntity;
import aurora.supply_wok.platform.purchasing.infrastructure.persistence.jpa.entities.PurchaseOrderPersistenceEntity;

import java.util.ArrayList;

/**
 * Static assembler between purchase order domain and persistence representations.
 */
public final class PurchaseOrderPersistenceAssembler {

    private PurchaseOrderPersistenceAssembler() {
    }

    public static PurchaseOrder toDomainFromPersistence(PurchaseOrderPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }

        var order = new PurchaseOrder();
        order.setId(entity.getId());
        order.setCode(entity.getCode());
        order.setSupplierId(entity.getSupplierId());
        order.setSupplierName(entity.getSupplierName());
        order.setRestaurantName(entity.getRestaurantName());
        order.setOrderDate(entity.getOrderDate());
        order.setEstimatedDate(entity.getEstimatedDate());
        order.setPriority(entity.getPriority());
        order.setStatus(entity.getStatus());

        var items = new ArrayList<PurchaseOrderItem>();
        for (var itemEntity : entity.getItems()) {
            var item = new PurchaseOrderItem();
            item.setId(itemEntity.getId());
            item.setInventoryItemId(itemEntity.getInventoryItemId());
            item.setProductName(itemEntity.getProductName());
            item.setQuantity(itemEntity.getQuantity());
            item.setUnitPrice(itemEntity.getUnitPrice());
            item.setUnitType(itemEntity.getUnitType());
            items.add(item);
        }
        order.setItems(items);
        return order;
    }

    public static PurchaseOrderPersistenceEntity toPersistenceFromDomain(PurchaseOrder order) {
        if (order == null) {
            return null;
        }

        var entity = new PurchaseOrderPersistenceEntity();
        if (order.getId() != null) {
            entity.setId(order.getId());
        }
        entity.setCode(order.getCode());
        entity.setSupplierId(order.getSupplierId());
        entity.setSupplierName(order.getSupplierName());
        entity.setRestaurantName(order.getRestaurantName());
        entity.setOrderDate(order.getOrderDate());
        entity.setEstimatedDate(order.getEstimatedDate());
        entity.setPriority(order.getPriority());
        entity.setStatus(order.getStatus());

        var itemEntities = new ArrayList<PurchaseOrderItemPersistenceEntity>();
        for (var item : order.getItems()) {
            var itemEntity = new PurchaseOrderItemPersistenceEntity();
            if (item.getId() != null) {
                itemEntity.setId(item.getId());
            }
            itemEntity.setPurchaseOrder(entity);
            itemEntity.setInventoryItemId(item.getInventoryItemId());
            itemEntity.setProductName(item.getProductName());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setUnitPrice(item.getUnitPrice());
            itemEntity.setUnitType(item.getUnitType());
            itemEntities.add(itemEntity);
        }
        entity.setItems(itemEntities);
        return entity;
    }
}
