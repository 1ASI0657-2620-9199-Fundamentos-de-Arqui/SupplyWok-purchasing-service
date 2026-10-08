package aurora.supply_wok.platform.suppliers.interfaces.rest.transform;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SupplierResourceFromEntityAssemblerTests {

    @Test
    void toResourceFromEntity_whenEntityProvided_mapsAllFieldsCorrectly() {
        // Arrange
        var uuid = UUID.randomUUID();
        var supplier = new Supplier(
                uuid,
                "Insumos del Chef",
                "Juan Perez",
                "juan@insumos.com",
                "+51987654321",
                "Verduras",
                "2026-01-15",
                "24h",
                "2h"
        );
        supplier.setId(22L);

        // Act
        var resource = SupplierResourceFromEntityAssembler.toResourceFromEntity(supplier);

        // Assert
        assertThat(resource.id()).isEqualTo(22L);
        assertThat(resource.uuid()).isEqualTo(uuid);
        assertThat(resource.name()).isEqualTo("Insumos del Chef");
        assertThat(resource.contactName()).isEqualTo("Juan Perez");
        assertThat(resource.email()).isEqualTo("juan@insumos.com");
        assertThat(resource.phone()).isEqualTo("+51987654321");
        assertThat(resource.category()).isEqualTo("Verduras");
        assertThat(resource.linkedDate()).isEqualTo("2026-01-15");
        assertThat(resource.sla()).isEqualTo("24h");
        assertThat(resource.responseTime()).isEqualTo("2h");
    }
}
