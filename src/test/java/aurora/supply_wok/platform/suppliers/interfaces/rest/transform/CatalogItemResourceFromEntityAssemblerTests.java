package aurora.supply_wok.platform.suppliers.interfaces.rest.transform;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;
import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogItemResourceFromEntityAssemblerTests {

    @Test
    void toResourceFromEntity_whenEntityProvided_mapsAllFieldsCorrectly() {
        // Arrange
        var item = new CatalogItem(
                2L,
                "Ajonjoli Blanco",
                "Semillas",
                BigDecimal.valueOf(14.50),
                ECatalogUnit.KG,
                "Entrega en bolsa sellada"
        );
        item.setId(101L);

        // Act
        var resource = CatalogItemResourceFromEntityAssembler.toResourceFromEntity(item);

        // Assert
        assertThat(resource.id()).isEqualTo(101L);
        assertThat(resource.name()).isEqualTo("Ajonjoli Blanco");
        assertThat(resource.category()).isEqualTo("Semillas");
        assertThat(resource.price()).isEqualTo(new BigDecimal("14.50"));
        assertThat(resource.unit()).isEqualTo("KG");
        assertThat(resource.deliveryConditions()).isEqualTo("Entrega en bolsa sellada");
    }
}
