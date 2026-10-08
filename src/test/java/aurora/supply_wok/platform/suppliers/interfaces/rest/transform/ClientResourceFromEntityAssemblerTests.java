package aurora.supply_wok.platform.suppliers.interfaces.rest.transform;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Client;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientResourceFromEntityAssemblerTests {

    @Test
    void toResourceFromEntity_whenEntityProvided_mapsAllFieldsCorrectly() {
        // Arrange
        var client = new Client("Chifa Palacio", "San Isidro", "active");
        client.setId(55L);

        // Act
        var resource = ClientResourceFromEntityAssembler.toResourceFromEntity(client);

        // Assert
        assertThat(resource.id()).isEqualTo(55L);
        assertThat(resource.name()).isEqualTo("Chifa Palacio");
        assertThat(resource.district()).isEqualTo("San Isidro");
        assertThat(resource.status()).isEqualTo("active");
    }
}
