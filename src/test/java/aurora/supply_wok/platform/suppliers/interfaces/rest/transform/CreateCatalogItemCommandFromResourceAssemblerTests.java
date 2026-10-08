package aurora.supply_wok.platform.suppliers.interfaces.rest.transform;

import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.CreateCatalogItemResource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateCatalogItemCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenResourceIsValid_mapsAllFieldsCorrectly() {
        // Arrange
        Long supplierId = 7L;
        var resource = new CreateCatalogItemResource(
                "Salsa de Tamarindo",
                "Salsas",
                BigDecimal.valueOf(12.0),
                "LTR",
                "Entrega inmediata"
        );

        // Act
        var command = CreateCatalogItemCommandFromResourceAssembler.toCommandFromResource(supplierId, resource);

        // Assert
        assertThat(command.supplierId()).isEqualTo(7L);
        assertThat(command.name()).isEqualTo("Salsa de Tamarindo");
        assertThat(command.category()).isEqualTo("Salsas");
        assertThat(command.price()).isEqualTo(BigDecimal.valueOf(12.0));
        assertThat(command.unit()).isEqualTo(ECatalogUnit.LTR);
        assertThat(command.deliveryConditions()).isEqualTo("Entrega inmediata");
    }

    @Test
    void toCommandFromResource_whenUnitIsInvalid_throwsIllegalArgumentException() {
        // Arrange
        Long supplierId = 7L;
        var resource = new CreateCatalogItemResource(
                "Salsa de Tamarindo",
                "Salsas",
                BigDecimal.valueOf(12.0),
                "INVALID_UNIT",
                "Entrega inmediata"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                CreateCatalogItemCommandFromResourceAssembler.toCommandFromResource(supplierId, resource));
    }
}
