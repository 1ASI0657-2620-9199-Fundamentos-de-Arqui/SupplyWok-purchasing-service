package aurora.supply_wok.platform.purchasing.acceptance;

import aurora.supply_wok.platform.purchasing.application.internal.commandservices.PurchaseOrderCommandServiceImpl;
import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderItemCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.UpdatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.UpdatePurchaseOrderStatusCommand;
import aurora.supply_wok.platform.purchasing.domain.model.entities.PurchaseOrderItem;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderPriority;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderStatus;
import aurora.supply_wok.platform.purchasing.domain.repositories.PurchaseOrderRepository;
import aurora.supply_wok.platform.suppliers.interfaces.acl.SuppliersContextFacade;
import aurora.supply_wok.platform.suppliers.interfaces.acl.resources.SupplierIdentityAclResource;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PurchaseOrderLifecycleSteps {

    private PurchaseOrderRepository purchaseOrderRepository;
    private SuppliersContextFacade suppliersContextFacade;
    private PurchaseOrderCommandServiceImpl purchaseOrderCommandService;

    private PurchaseOrder currentOrder;
    private Optional<PurchaseOrder> updatedOrderResult;
    private Exception thrownException;

    @Before
    public void setUp() {
        purchaseOrderRepository = mock(PurchaseOrderRepository.class);
        suppliersContextFacade = mock(SuppliersContextFacade.class);
        purchaseOrderCommandService = new PurchaseOrderCommandServiceImpl(purchaseOrderRepository, suppliersContextFacade);
        currentOrder = null;
        updatedOrderResult = Optional.empty();
        thrownException = null;

        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Given("an existing purchase order with code {string} in {string} status for restaurant {long} and supplier {long}")
    public void anExistingPurchaseOrderWithCodeInStatusForRestaurantAndSupplier(
            String code, String statusName, long restaurantId, long supplierId) {
        var status = EPurchaseOrderStatus.valueOf(statusName);
        var item = new PurchaseOrderItem(1L, "Wok Sauce", BigDecimal.valueOf(10), BigDecimal.valueOf(5), "L");
        currentOrder = new PurchaseOrder(
                code,
                supplierId,
                "Asian Food Distribution",
                "Dragon Wok",
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Medium,
                status,
                List.of(item),
                restaurantId
        );
        currentOrder.setId(200L);

        when(purchaseOrderRepository.findById(200L)).thenReturn(Optional.of(currentOrder));
        when(purchaseOrderRepository.findByIdAndRestaurantId(200L, restaurantId)).thenReturn(Optional.of(currentOrder));
        when(suppliersContextFacade.getSupplierIdentityById(supplierId))
                .thenReturn(Optional.of(new SupplierIdentityAclResource(supplierId, "Asian Food Distribution")));
    }

    @Given("an existing purchase order with code {string} in {string} status")
    public void anExistingPurchaseOrderWithCodeInStatus(String code, String statusName) {
        var status = EPurchaseOrderStatus.valueOf(statusName);
        var item = new PurchaseOrderItem(1L, "Wok Sauce", BigDecimal.valueOf(10), BigDecimal.valueOf(5), "L");
        currentOrder = new PurchaseOrder(
                code,
                10L,
                "Asian Food Distribution",
                "Dragon Wok",
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Medium,
                status,
                List.of(item),
                101L
        );
        currentOrder.setId(300L);

        when(purchaseOrderRepository.findById(300L)).thenReturn(Optional.of(currentOrder));
        when(purchaseOrderRepository.findByIdAndRestaurantId(300L, 101L)).thenReturn(Optional.of(currentOrder));
        when(suppliersContextFacade.getSupplierIdentityById(10L))
                .thenReturn(Optional.of(new SupplierIdentityAclResource(10L, "Asian Food Distribution")));
    }

    @When("the restaurant modifies the purchase order details with updated restaurant name {string}")
    public void theRestaurantModifiesThePurchaseOrderDetailsWithUpdatedRestaurantName(String newRestaurantName) {
        var item = new CreatePurchaseOrderItemCommand(1L, 1L, "Wok Sauce", BigDecimal.valueOf(10), BigDecimal.valueOf(5), "L");
        var command = new UpdatePurchaseOrderCommand(
                currentOrder.getId(),
                currentOrder.getCode(),
                currentOrder.getSupplierId(),
                currentOrder.getSupplierName(),
                newRestaurantName,
                currentOrder.getOrderDate(),
                currentOrder.getEstimatedDate(),
                currentOrder.getPriority().name(),
                currentOrder.getStatus().name(),
                List.of(item),
                currentOrder.getRestaurantId()
        );

        try {
            updatedOrderResult = purchaseOrderCommandService.handle(command);
        } catch (Exception ex) {
            thrownException = ex;
        }
    }

    @Then("the purchase order is updated successfully with restaurant name {string}")
    public void thePurchaseOrderIsUpdatedSuccessfullyWithRestaurantName(String expectedName) {
        assertThat(thrownException).isNull();
        assertThat(updatedOrderResult).isPresent();
        assertThat(updatedOrderResult.get().getRestaurantName()).isEqualTo(expectedName);
    }

    @When("the restaurant attempts to modify the purchase order details")
    public void theRestaurantAttemptsToModifyThePurchaseOrderDetails() {
        var item = new CreatePurchaseOrderItemCommand(1L, 1L, "Wok Sauce", BigDecimal.valueOf(10), BigDecimal.valueOf(5), "L");
        var command = new UpdatePurchaseOrderCommand(
                currentOrder.getId(),
                currentOrder.getCode(),
                currentOrder.getSupplierId(),
                currentOrder.getSupplierName(),
                "Modified Name",
                currentOrder.getOrderDate(),
                currentOrder.getEstimatedDate(),
                currentOrder.getPriority().name(),
                currentOrder.getStatus().name(),
                List.of(item),
                currentOrder.getRestaurantId()
        );

        try {
            updatedOrderResult = purchaseOrderCommandService.handle(command);
        } catch (Exception ex) {
            thrownException = ex;
        }
    }

    @Then("the modification is rejected with an error {string}")
    public void theModificationIsRejectedWithAnError(String expectedMessage) {
        assertThat(thrownException).isNotNull();
        assertThat(thrownException.getMessage()).isEqualTo(expectedMessage);
    }

    @When("the status is updated to {string}")
    public void theStatusIsUpdatedTo(String nextStatus) {
        var command = new UpdatePurchaseOrderStatusCommand(currentOrder.getId(), nextStatus);
        try {
            updatedOrderResult = purchaseOrderCommandService.handle(command);
            if (updatedOrderResult.isPresent()) {
                currentOrder = updatedOrderResult.get();
            }
        } catch (Exception ex) {
            thrownException = ex;
        }
    }

    @Then("the purchase order status is updated to {string}")
    public void thePurchaseOrderStatusIsUpdatedTo(String expectedStatus) {
        assertThat(thrownException).isNull();
        assertThat(currentOrder.getStatus().name()).isEqualToIgnoringCase(expectedStatus);
    }

    @When("an invalid status transition to {string} is attempted")
    public void anInvalidStatusTransitionToIsAttempted(String targetStatus) {
        var command = new UpdatePurchaseOrderStatusCommand(currentOrder.getId(), targetStatus);
        try {
            updatedOrderResult = purchaseOrderCommandService.handle(command);
        } catch (Exception ex) {
            thrownException = ex;
        }
    }

    @Then("the status update is rejected with an invalid transition error")
    public void theStatusUpdateIsRejectedWithAnInvalidTransitionError() {
        assertThat(thrownException).isNotNull();
        assertThat(thrownException).isInstanceOf(IllegalArgumentException.class);
        assertThat(thrownException.getMessage()).isEqualTo("Invalid purchase order status transition.");
    }
}
