package aurora.supply_wok.platform.suppliers.interfaces.rest.transform;

import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.CreateSupplierResource;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CreateSupplierCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenLinkedDateProvided_mapsAllFieldsCorrectly() {
        // Arrange
        var resource = new CreateSupplierResource(
                "  Distribuidora Oriental  ",
                "  Li Wei  ",
                "  li@oriental.com  ",
                "  999888777  ",
                "  Abarrotes  ",
                "2026-02-15",
                "  24h  ",
                "  1h  "
        );

        // Act
        var command = CreateSupplierCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertThat(command.name()).isEqualTo("Distribuidora Oriental");
        assertThat(command.contactName()).isEqualTo("Li Wei");
        assertThat(command.email()).isEqualTo("li@oriental.com");
        assertThat(command.phone()).isEqualTo("999888777");
        assertThat(command.category()).isEqualTo("Abarrotes");
        assertThat(command.linkedDate()).isEqualTo("2026-02-15");
        assertThat(command.sla()).isEqualTo("24h");
        assertThat(command.responseTime()).isEqualTo("1h");
    }

    @Test
    void toCommandFromResource_whenLinkedDateIsBlank_defaultsToCurrentDate() {
        // Arrange
        var resource = new CreateSupplierResource(
                "Distribuidora Oriental",
                "Li Wei",
                "li@oriental.com",
                "999888777",
                "Abarrotes",
                "",
                "24h",
                "1h"
        );

        // Act
        var command = CreateSupplierCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertThat(command.linkedDate()).isEqualTo(LocalDate.now().toString());
    }
}
