package aurora.supply_wok.platform.purchasing.acceptance;

import aurora.supply_wok.platform.purchasing.application.internal.commandservices.PurchaseOrderCommandServiceImpl;
import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderItemCommand;
import aurora.supply_wok.platform.purchasing.domain.repositories.PurchaseOrderRepository;
import aurora.supply_wok.platform.suppliers.interfaces.acl.SuppliersContextFacade;
import aurora.supply_wok.platform.suppliers.interfaces.acl.resources.SupplierIdentityAclResource;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PurchaseOrderCreationSteps {

    private PurchaseOrderRepository purchaseOrderRepository;
    private SuppliersContextFacade suppliersContextFacade;
    private PurchaseOrderCommandServiceImpl purchaseOrderCommandService;

    private Long currentRestaurantId;
    private String currentRestaurantName;
    private Optional<PurchaseOrder> createdOrder;
    private Exception thrownException;

    @Before
    public void setUp() {
        purchaseOrderRepository = mock(PurchaseOrderRepository.class);
        suppliersContextFacade = mock(SuppliersContextFacade.class);
        purchaseOrderCommandService = new PurchaseOrderCommandServiceImpl(purchaseOrderRepository, suppliersContextFacade);
        createdOrder = Optional.empty();
        thrownException = null;

        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> {
            PurchaseOrder po = invocation.getArgument(0);
            if (po.getId() == null) {
                po.setId(100L);
            }
            return po;
        });
    }

    @Given("an active supplier with id {long} and name {string}")
    public void anActiveSupplierWithIdAndName(long supplierId, String supplierName) {
        when(suppliersContextFacade.getSupplierIdentityById(supplierId))
                .thenReturn(Optional.of(new SupplierIdentityAclResource(supplierId, supplierName)));
    }

    @Given("no supplier exists with id {long}")
    public void noSupplierExistsWithId(long supplierId) {
        when(suppliersContextFacade.getSupplierIdentityById(supplierId))
                .thenReturn(Optional.empty());
    }

    @And("a restaurant with id {long} and name {string}")
    public void aRestaurantWithIdAndName(long restaurantId, String restaurantName) {
        this.currentRestaurantId = restaurantId;
        this.currentRestaurantName = restaurantName;
    }

    @When("the restaurant submits a purchase order with code {string} for supplier {long} with items:")
    public void theRestaurantSubmitsAPurchaseOrderWithCodeForSupplierWithItems(
            String code, long supplierId, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        List<CreatePurchaseOrderItemCommand> items = new ArrayList<>();
        long itemIdCounter = 1L;

        for (Map<String, String> row : rows) {
            String productName = row.get("productName");
            BigDecimal quantity = new BigDecimal(row.get("quantity"));
            BigDecimal unitPrice = new BigDecimal(row.get("unitPrice"));
            String unitType = row.get("unitType");
            items.add(new CreatePurchaseOrderItemCommand(
                    itemIdCounter++,
                    itemIdCounter,
                    productName,
                    quantity,
                    unitPrice,
                    unitType
            ));
        }

        var command = new CreatePurchaseOrderCommand(
                code,
                supplierId,
                null,
                currentRestaurantName,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                "Medium",
                "Pending",
                items,
                currentRestaurantId
        );

        try {
            createdOrder = purchaseOrderCommandService.handle(command);
        } catch (Exception ex) {
            thrownException = ex;
        }
    }

    @Then("the purchase order is created successfully with status {string}")
    public void thePurchaseOrderIsCreatedSuccessfullyWithStatus(String expectedStatus) {
        assertThat(thrownException).isNull();
        assertThat(createdOrder).isPresent();
        assertThat(createdOrder.get().getStatus().name()).isEqualToIgnoringCase(expectedStatus);
    }

    @And("the purchase order belongs to restaurant {long} and supplier {long}")
    public void thePurchaseOrderBelongsToRestaurantAndSupplier(long restaurantId, long supplierId) {
        assertThat(createdOrder).isPresent();
        assertThat(createdOrder.get().getRestaurantId()).isEqualTo(restaurantId);
        assertThat(createdOrder.get().getSupplierId()).isEqualTo(supplierId);
    }

    @Then("the purchase order creation is rejected because supplier is not found")
    public void thePurchaseOrderCreationIsRejectedBecauseSupplierIsNotFound() {
        assertThat(createdOrder).isEmpty();
    }

    @When("the restaurant attempts to create a purchase order with initial status {string}")
    public void theRestaurantAttemptsToCreateAPurchaseOrderWithInitialStatus(String initialStatus) {
        var item = new CreatePurchaseOrderItemCommand(
                1L, 1L, "Soy Sauce", BigDecimal.TEN, BigDecimal.valueOf(5.0), "L");
        var command = new CreatePurchaseOrderCommand(
                "PO-INVALID-STATUS",
                10L,
                null,
                currentRestaurantName,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                "Medium",
                initialStatus,
                List.of(item),
                currentRestaurantId
        );

        try {
            createdOrder = purchaseOrderCommandService.handle(command);
        } catch (Exception ex) {
            thrownException = ex;
        }
    }

    @Then("the purchase order creation is rejected with an error {string}")
    public void thePurchaseOrderCreationIsRejectedWithAnError(String expectedMessage) {
        assertThat(thrownException).isNotNull();
        assertThat(thrownException.getMessage()).isEqualTo(expectedMessage);
    }
}
