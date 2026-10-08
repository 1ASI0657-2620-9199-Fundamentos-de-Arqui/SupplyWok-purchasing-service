package aurora.supply_wok.platform.suppliers.interfaces.rest.transform;

import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.UpdateCatalogItemResource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateCatalogItemCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenResourceIsValid_mapsAllFieldsCorrectly() {
        // Arrange
        Long supplierId = 3L;
        Long catalogItemId = 18L;
        var resource = new UpdateCatalogItemResource(
                "Salsa de Ostras",
                "Salsas Especiales",
                BigDecimal.valueOf(22.0),
                "LTR",
                "Entrega en 48h"
        );

        // Act
        var command = UpdateCatalogItemCommandFromResourceAssembler.toCommandFromResource(supplierId, catalogItemId, resource);

        // Assert
        assertThat(command.supplierId()).isEqualTo(3L);
        assertThat(command.catalogItemId()).isEqualTo(18L);
        assertThat(command.name()).isEqualTo("Salsa de Ostras");
        assertThat(command.category()).isEqualTo("Salsas Especiales");
        assertThat(command.price()).isEqualTo(BigDecimal.valueOf(22.0));
        assertThat(command.unit()).isEqualTo(ECatalogUnit.LTR);
        assertThat(command.deliveryConditions()).isEqualTo("Entrega en 48h");
    }

    @Test
    void toCommandFromResource_whenUnitIsInvalid_throwsIllegalArgumentException() {
        // Arrange
        Long supplierId = 3L;
        Long catalogItemId = 18L;
        var resource = new UpdateCatalogItemResource(
                "Salsa de Ostras",
                "Salsas Especiales",
                BigDecimal.valueOf(22.0),
                "INVALID_UNIT",
                "Entrega en 48h"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                UpdateCatalogItemCommandFromResourceAssembler.toCommandFromResource(supplierId, catalogItemId, resource));
    }
}
