package aurora.supply_wok.platform.purchasing.interfaces.rest.transform;

import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.PurchaseOrderItemResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.UpdatePurchaseOrderResource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UpdatePurchaseOrderCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenValidResourceProvided_mapsAllFieldsCorrectly() {
        // Arrange
        Long orderId = 123L;
        var today = LocalDate.of(2026, 5, 1);
        var deliveryDate = LocalDate.of(2026, 5, 5);
        var resource = new UpdatePurchaseOrderResource(
                "PO-UPDATE-1",
                4L,
                "Supplier Vega",
                "Chifa Golden",
                today,
                deliveryDate,
                "Low",
                "Confirmed",
                List.of(new PurchaseOrderItemResource(
                        10L,
                        2L,
                        "Pak Choi",
                        BigDecimal.valueOf(20),
                        BigDecimal.valueOf(3.5),
                        "KG"
                ))
        );

        // Act
        var command = UpdatePurchaseOrderCommandFromResourceAssembler.toCommandFromResource(orderId, resource);

        // Assert
        assertThat(command.id()).isEqualTo(123L);
        assertThat(command.code()).isEqualTo("PO-UPDATE-1");
        assertThat(command.supplierId()).isEqualTo(4L);
        assertThat(command.supplierName()).isEqualTo("Supplier Vega");
        assertThat(command.restaurantName()).isEqualTo("Chifa Golden");
        assertThat(command.orderDate()).isEqualTo(today);
        assertThat(command.estimatedDate()).isEqualTo(deliveryDate);
        assertThat(command.priority()).isEqualTo("Low");
        assertThat(command.status()).isEqualTo("Confirmed");
        assertThat(command.items()).hasSize(1);
        assertThat(command.items().getFirst().id()).isEqualTo(10L);
        assertThat(command.items().getFirst().productName()).isEqualTo("Pak Choi");
    }
}
