package aurora.supply_wok.platform.purchasing.interfaces.rest.transform;

import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.UpdatePurchaseOrderStatusResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdatePurchaseOrderStatusCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenResourceProvided_mapsCommandFields() {
        // Arrange
        Long orderId = 88L;
        var resource = new UpdatePurchaseOrderStatusResource("Delivered");

        // Act
        var command = UpdatePurchaseOrderStatusCommandFromResourceAssembler.toCommandFromResource(orderId, resource);

        // Assert
        assertThat(command.id()).isEqualTo(88L);
        assertThat(command.status()).isEqualTo("Delivered");
    }
}
