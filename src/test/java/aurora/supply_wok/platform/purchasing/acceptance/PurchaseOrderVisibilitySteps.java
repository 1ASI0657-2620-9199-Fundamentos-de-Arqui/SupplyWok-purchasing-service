package aurora.supply_wok.platform.purchasing.acceptance;

import aurora.supply_wok.platform.purchasing.application.internal.queryservices.PurchaseOrderQueryServiceImpl;
import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.entities.PurchaseOrderItem;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrderByIdAndRestaurantIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrdersByRestaurantIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrdersBySupplierIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderPriority;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderStatus;
import aurora.supply_wok.platform.purchasing.domain.repositories.PurchaseOrderRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PurchaseOrderVisibilitySteps {

    private PurchaseOrderRepository purchaseOrderRepository;
    private PurchaseOrderQueryServiceImpl purchaseOrderQueryService;

    private List<PurchaseOrder> registeredOrders;
    private List<PurchaseOrder> queryResults;
    private Optional<PurchaseOrder> singleQueryResult;
    private long idSequence = 1000L;

    @Before
    public void setUp() {
        purchaseOrderRepository = mock(PurchaseOrderRepository.class);
        purchaseOrderQueryService = new PurchaseOrderQueryServiceImpl(purchaseOrderRepository);
        registeredOrders = new ArrayList<>();
        queryResults = new ArrayList<>();
        singleQueryResult = Optional.empty();

        when(purchaseOrderRepository.findAllBySupplierId(anyLong())).thenAnswer(inv -> {
            Long supplierId = inv.getArgument(0);
            return registeredOrders.stream()
                    .filter(order -> supplierId.equals(order.getSupplierId()))
                    .toList();
        });

        when(purchaseOrderRepository.findAllByRestaurantId(anyLong())).thenAnswer(inv -> {
            Long restaurantId = inv.getArgument(0);
            return registeredOrders.stream()
                    .filter(order -> restaurantId.equals(order.getRestaurantId()))
                    .toList();
        });

        when(purchaseOrderRepository.findByIdAndRestaurantId(anyLong(), anyLong())).thenAnswer(inv -> {
            Long orderId = inv.getArgument(0);
            Long restaurantId = inv.getArgument(1);
            return registeredOrders.stream()
                    .filter(order -> orderId.equals(order.getId()) && restaurantId.equals(order.getRestaurantId()))
                    .findFirst();
        });
    }

    @Given("a purchase order {string} for supplier {long} and restaurant {long}")
    public void aPurchaseOrderForSupplierAndRestaurant(String code, long supplierId, long restaurantId) {
        var item = new PurchaseOrderItem(1L, "Item", BigDecimal.TEN, BigDecimal.ONE, "KG");
        var order = new PurchaseOrder(
                code,
                supplierId,
                "Supplier " + supplierId,
                "Restaurant " + restaurantId,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Medium,
                EPurchaseOrderStatus.Pending,
                List.of(item),
                restaurantId
        );
        order.setId(idSequence++);
        registeredOrders.add(order);
    }

    @Given("a purchase order {string} for restaurant {long} and supplier {long}")
    public void aPurchaseOrderForRestaurantAndSupplier(String code, long restaurantId, long supplierId) {
        aPurchaseOrderForSupplierAndRestaurant(code, supplierId, restaurantId);
    }

    @Given("a purchase order {string} with id {long} belonging to restaurant {long}")
    public void aPurchaseOrderWithIdBelongingToRestaurant(String code, long orderId, long restaurantId) {
        var item = new PurchaseOrderItem(1L, "Item", BigDecimal.TEN, BigDecimal.ONE, "KG");
        var order = new PurchaseOrder(
                code,
                10L,
                "Supplier 10",
                "Restaurant " + restaurantId,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                EPurchaseOrderPriority.Medium,
                EPurchaseOrderStatus.Pending,
                List.of(item),
                restaurantId
        );
        order.setId(orderId);
        registeredOrders.add(order);
    }

    @When("supplier {long} queries all its purchase orders")
    public void supplierQueriesAllItsPurchaseOrders(long supplierId) {
        queryResults = purchaseOrderQueryService.handle(new GetPurchaseOrdersBySupplierIdQuery(supplierId));
    }

    @When("restaurant {long} queries all its purchase orders")
    public void restaurantQueriesAllItsPurchaseOrders(long restaurantId) {
        queryResults = purchaseOrderQueryService.handle(new GetPurchaseOrdersByRestaurantIdQuery(restaurantId));
    }

    @When("restaurant {long} queries the purchase order with id {long}")
    public void restaurantQueriesThePurchaseOrderWithId(long restaurantId, long orderId) {
        singleQueryResult = purchaseOrderQueryService.handle(
                new GetPurchaseOrderByIdAndRestaurantIdQuery(orderId, restaurantId));
    }

    @Then("the query results contain only purchase order {string}")
    public void theQueryResultsContainOnlyPurchaseOrder(String expectedCode) {
        assertThat(queryResults).extracting(PurchaseOrder::getCode).containsExactly(expectedCode);
    }

    @And("the query results do not contain purchase order {string}")
    public void theQueryResultsDoNotContainPurchaseOrder(String excludedCode) {
        assertThat(queryResults).extracting(PurchaseOrder::getCode).doesNotContain(excludedCode);
    }

    @Then("no purchase order is found")
    public void noPurchaseOrderIsFound() {
        assertThat(singleQueryResult).isEmpty();
    }
}
