package aurora.supply_wok.platform.suppliers.domain.model.aggregates;

import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.UpdateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.events.CatalogItemCreatedEvent;
import aurora.supply_wok.platform.suppliers.domain.model.events.CatalogItemUpdatedEvent;
import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CatalogItemTests {

    @Test
    void constructor_whenSupplierIdIsZeroOrNegative_throwsIllegalArgumentException() {
        // Arrange
        Long invalidSupplierId = 0L;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new CatalogItem(
                invalidSupplierId,
                "Salsa Soya",
                "Condimentos",
                BigDecimal.valueOf(12.5),
                ECatalogUnit.LTR,
                "Entregas en 24h"
        ));
    }

    @Test
    void constructor_whenNameIsBlank_throwsIllegalArgumentException() {
        // Arrange
        String blankName = "   ";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new CatalogItem(
                1L,
                blankName,
                "Condimentos",
                BigDecimal.valueOf(12.5),
                ECatalogUnit.LTR,
                "Entregas en 24h"
        ));
    }

    @Test
    void constructor_whenCategoryIsBlank_throwsIllegalArgumentException() {
        // Arrange
        String blankCategory = "";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new CatalogItem(
                1L,
                "Salsa Soya",
                blankCategory,
                BigDecimal.valueOf(12.5),
                ECatalogUnit.LTR,
                "Entregas en 24h"
        ));
    }

    @Test
    void constructor_whenPriceIsZeroOrNegative_throwsIllegalArgumentException() {
        // Arrange
        BigDecimal invalidPrice = BigDecimal.ZERO;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new CatalogItem(
                1L,
                "Salsa Soya",
                "Condimentos",
                invalidPrice,
                ECatalogUnit.LTR,
                "Entregas en 24h"
        ));
    }

    @Test
    void constructor_whenUnitIsNull_throwsIllegalArgumentException() {
        // Arrange
        ECatalogUnit nullUnit = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new CatalogItem(
                1L,
                "Salsa Soya",
                "Condimentos",
                BigDecimal.valueOf(12.5),
                nullUnit,
                "Entregas en 24h"
        ));
    }

    @Test
    void constructor_whenDeliveryConditionsIsBlank_throwsIllegalArgumentException() {
        // Arrange
        String blankDeliveryConditions = " ";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new CatalogItem(
                1L,
                "Salsa Soya",
                "Condimentos",
                BigDecimal.valueOf(12.5),
                ECatalogUnit.LTR,
                blankDeliveryConditions
        ));
    }

    @Test
    void constructor_whenParametersAreValid_createsCatalogItemWithNormalizedPrice() {
        // Arrange
        var command = new CreateCatalogItemCommand(
                2L,
                "Fideos de Arroz",
                "Pastas",
                BigDecimal.valueOf(15.556),
                ECatalogUnit.KG,
                "Entrega matutina"
        );

        // Act
        var item = new CatalogItem(command);

        // Assert
        assertThat(item.getSupplierId()).isEqualTo(2L);
        assertThat(item.getName()).isEqualTo("Fideos de Arroz");
        assertThat(item.getPrice()).isEqualTo(new BigDecimal("15.56"));
    }

    @Test
    void updateInformation_whenCommandIsValid_updatesFieldsAndRegistersDomainEvent() {
        // Arrange
        var item = new CatalogItem(
                1L,
                "Arroz Chaufero",
                "Granos",
                BigDecimal.valueOf(30),
                ECatalogUnit.BOX,
                "Entrega diaria"
        );
        var updateCommand = new UpdateCatalogItemCommand(
                1L,
                item.getId(),
                "Arroz Superior",
                "Granos Premium",
                BigDecimal.valueOf(35),
                ECatalogUnit.BOX,
                "Entrega inmediata"
        );

        // Act
        item.updateInformation(updateCommand);

        // Assert
        assertThat(item.getName()).isEqualTo("Arroz Superior");
        assertThat(item.getCategory()).isEqualTo("Granos Premium");
        assertThat(item.getPrice()).isEqualTo(new BigDecimal("35.00"));
        assertThat(item.domainEvents()).hasSize(1);
        assertThat(item.domainEvents().iterator().next()).isInstanceOf(CatalogItemUpdatedEvent.class);
    }

    @Test
    void onCreated_whenInvoked_registersCatalogItemCreatedEvent() {
        // Arrange
        var item = new CatalogItem();

        // Act
        item.onCreated();

        // Assert
        assertThat(item.domainEvents()).hasSize(1);
        assertThat(item.domainEvents().iterator().next()).isInstanceOf(CatalogItemCreatedEvent.class);
    }
}
