package aurora.supply_wok.platform.suppliers.acceptance;

import aurora.supply_wok.platform.suppliers.application.internal.commandservices.CatalogItemCommandServiceImpl;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;
import aurora.supply_wok.platform.suppliers.domain.model.commands.CreateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.commands.UpdateCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.valueobjects.ECatalogUnit;
import aurora.supply_wok.platform.suppliers.domain.repositories.CatalogItemRepository;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SupplierCatalogSteps {

    private CatalogItemRepository catalogItemRepository;
    private SupplierRepository supplierRepository;
    private CatalogItemCommandServiceImpl catalogItemCommandService;

    private Optional<Long> createdItemId;
    private Optional<CatalogItem> updatedItem;

    @Before
    public void setUp() {
        catalogItemRepository = Mockito.mock(CatalogItemRepository.class);
        supplierRepository = Mockito.mock(SupplierRepository.class);
        catalogItemCommandService = new CatalogItemCommandServiceImpl(catalogItemRepository, supplierRepository);
        createdItemId = Optional.empty();
        updatedItem = Optional.empty();
    }

    @Given("a registered supplier with id {long} in purchasing")
    public void aRegisteredSupplierInPurchasing(Long supplierId) {
        when(supplierRepository.existsById(supplierId)).thenReturn(true);
    }

    @Given("no registered supplier exists with id {long} in purchasing")
    public void noRegisteredSupplierExistsInPurchasing(Long supplierId) {
        when(supplierRepository.existsById(supplierId)).thenReturn(false);
    }

    @When("supplier {long} adds catalog item {string} priced at {double} per {string}")
    public void supplierAddsCatalogItem(Long supplierId, String name, Double price, String unit) {
        when(catalogItemRepository.save(any(CatalogItem.class))).thenAnswer(invocation -> {
            CatalogItem item = invocation.getArgument(0);
            item.setId(42L);
            return item;
        });
        var command = new CreateCatalogItemCommand(supplierId, name, "Salsas", BigDecimal.valueOf(price), ECatalogUnit.valueOf(unit), "Entrega 24h");
        createdItemId = catalogItemCommandService.handle(command);
    }

    @When("supplier {long} attempts to add catalog item {string} priced at {double} per {string}")
    public void supplierAttemptsToAddCatalogItem(Long supplierId, String name, Double price, String unit) {
        var command = new CreateCatalogItemCommand(supplierId, name, "Aceites", BigDecimal.valueOf(price), ECatalogUnit.valueOf(unit), "Estándar");
        createdItemId = catalogItemCommandService.handle(command);
    }

    @Then("the catalog item is created with an assigned id")
    public void theCatalogItemIsCreatedWithAnAssignedId() {
        assertThat(createdItemId).contains(42L);
    }

    @Then("the catalog item operation returns an empty result")
    public void theCatalogItemOperationReturnsAnEmptyResult() {
        assertThat(createdItemId).isEmpty();
        assertThat(updatedItem).isEmpty();
    }

    @Given("supplier {long} owns catalog item {long} named {string} priced at {double}")
    public void supplierOwnsCatalogItem(Long supplierId, Long itemId, String name, Double price) {
        var existing = new CatalogItem(supplierId, name, "Salsas", BigDecimal.valueOf(price), ECatalogUnit.LTR, "Entrega inmediata");
        existing.setId(itemId);
        when(catalogItemRepository.findByIdAndSupplierId(itemId, supplierId)).thenReturn(Optional.of(existing));
        when(catalogItemRepository.save(any(CatalogItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Given("catalog item {long} does not belong to supplier {long}")
    public void catalogItemDoesNotBelongToSupplier(Long itemId, Long supplierId) {
        when(catalogItemRepository.findByIdAndSupplierId(itemId, supplierId)).thenReturn(Optional.empty());
    }

    @When("supplier {long} updates catalog item {long} to name {string} and price {double}")
    public void supplierUpdatesCatalogItem(Long supplierId, Long itemId, String newName, Double newPrice) {
        var command = new UpdateCatalogItemCommand(supplierId, itemId, newName, "Salsas Premium", BigDecimal.valueOf(newPrice), ECatalogUnit.LTR, "Entrega 24h");
        updatedItem = catalogItemCommandService.handle(command);
    }

    @When("supplier {long} attempts to update catalog item {long} to name {string} and price {double}")
    public void supplierAttemptsToUpdateCatalogItem(Long supplierId, Long itemId, String newName, Double newPrice) {
        var command = new UpdateCatalogItemCommand(supplierId, itemId, newName, "Salsas", BigDecimal.valueOf(newPrice), ECatalogUnit.LTR, "Entrega 24h");
        updatedItem = catalogItemCommandService.handle(command);
    }

    @Then("catalog item {long} is updated with name {string} and price {double}")
    public void catalogItemIsUpdated(Long itemId, String expectedName, Double expectedPrice) {
        assertThat(updatedItem).isPresent();
        assertThat(updatedItem.get().getId()).isEqualTo(itemId);
        assertThat(updatedItem.get().getName()).isEqualTo(expectedName);
        assertThat(updatedItem.get().getPrice()).isEqualByComparingTo(BigDecimal.valueOf(expectedPrice));
    }
}
