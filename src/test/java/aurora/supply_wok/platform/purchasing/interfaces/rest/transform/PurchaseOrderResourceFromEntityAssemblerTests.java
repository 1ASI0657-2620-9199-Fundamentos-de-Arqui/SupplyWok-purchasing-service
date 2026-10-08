package aurora.supply_wok.platform.purchasing.interfaces.rest.transform;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.entities.PurchaseOrderItem;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderPriority;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseOrderResourceFromEntityAssemblerTests {

    @Test
    void toResourceFromEntity_whenEntityHasValues_mapsAllFieldsCorrectly() {
        // Arrange
        var today = LocalDate.of(2026, 4, 10);
        var estimated = LocalDate.of(2026, 4, 15);
        var item = new PurchaseOrderItem(12L, "Sillao", BigDecimal.valueOf(5), BigDecimal.valueOf(8.50), "L");
        item.setId(99L);

        var order = new PurchaseOrder(
                "PO-200",
                3L,
                "Supplier San",
                "Wok Express",
                today,
                estimated,
                EPurchaseOrderPriority.High,
                EPurchaseOrderStatus.Confirmed,
                List.of(item)
        );
        order.setId(10L);

        // Act
        var resource = PurchaseOrderResourceFromEntityAssembler.toResourceFromEntity(order);

        // Assert
        assertThat(resource.id()).isEqualTo(10L);
        assertThat(resource.code()).isEqualTo("PO-200");
        assertThat(resource.supplierId()).isEqualTo(3L);
        assertThat(resource.supplierName()).isEqualTo("Supplier San");
        assertThat(resource.restaurantName()).isEqualTo("Wok Express");
        assertThat(resource.orderDate()).isEqualTo("2026-04-10");
        assertThat(resource.estimatedDate()).isEqualTo("2026-04-15");
        assertThat(resource.priority()).isEqualTo("High");
        assertThat(resource.status()).isEqualTo("Confirmed");
        assertThat(resource.items()).hasSize(1);
        assertThat(resource.items().getFirst().id()).isEqualTo(99L);
        assertThat(resource.items().getFirst().productName()).isEqualTo("Sillao");
    }

    @Test
    void toResourceFromEntity_whenStatusIsInTransit_mapsStatusLabelWithSpace() {
        // Arrange
        var order = new PurchaseOrder();
        order.setStatus(EPurchaseOrderStatus.InTransit);

        // Act
        var resource = PurchaseOrderResourceFromEntityAssembler.toResourceFromEntity(order);

        // Assert
        assertThat(resource.status()).isEqualTo("In Transit");
    }
}
