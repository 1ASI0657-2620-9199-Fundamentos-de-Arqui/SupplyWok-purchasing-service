package aurora.supply_wok.platform.purchasing.interfaces.rest.transform;

import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.CreatePurchaseOrderResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.PurchaseOrderItemResource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreatePurchaseOrderCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenClientSideLineItemIdsProvided_ignoresItemIds() {
        // Arrange
        var resource = new CreatePurchaseOrderResource(
                "PO-TEST-1",
                1L,
                "Golden Wok Produce",
                "Gran Dragon Chifa",
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                "High",
                "Pending",
                List.of(new PurchaseOrderItemResource(
                        1783513738209L,
                        null,
                        "arroz",
                        BigDecimal.valueOf(50),
                        BigDecimal.valueOf(2.5),
                        "Kg"
                ))
        );

        // Act
        var command = CreatePurchaseOrderCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertThat(command.items()).hasSize(1);
        assertThat(command.items().getFirst().id()).isNull();
    }

    @Test
    void toCommandFromResource_whenValidResource_mapsAllFieldsCorrectly() {
        // Arrange
        var today = LocalDate.now();
        var estimated = today.plusDays(3);
        var resource = new CreatePurchaseOrderResource(
                "PO-999",
                5L,
                "Supplier Five",
                "Wok Asia",
                today,
                estimated,
                "High",
                "Pending",
                List.of(new PurchaseOrderItemResource(
                        null,
                        20L,
                        "Kion",
                        BigDecimal.valueOf(10),
                        BigDecimal.valueOf(4.5),
                        "KG"
                ))
        );

        // Act
        var command = CreatePurchaseOrderCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertThat(command.code()).isEqualTo("PO-999");
        assertThat(command.supplierId()).isEqualTo(5L);
        assertThat(command.supplierName()).isEqualTo("Supplier Five");
        assertThat(command.restaurantName()).isEqualTo("Wok Asia");
        assertThat(command.orderDate()).isEqualTo(today);
        assertThat(command.estimatedDate()).isEqualTo(estimated);
        assertThat(command.priority()).isEqualTo("High");
        assertThat(command.status()).isEqualTo("Pending");
        assertThat(command.items().getFirst().inventoryItemId()).isEqualTo(20L);
        assertThat(command.items().getFirst().productName()).isEqualTo("Kion");
    }
}
