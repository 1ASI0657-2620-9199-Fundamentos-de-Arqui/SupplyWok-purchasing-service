package aurora.supply_wok.platform.purchasing.application.internal.queryservices;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetAllPurchaseOrdersQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrderByIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrdersBySupplierIdQuery;
import aurora.supply_wok.platform.purchasing.domain.repositories.PurchaseOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderQueryServiceImplTests {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    private PurchaseOrderQueryServiceImpl purchaseOrderQueryService;

    @Test
    void handle_whenGetAllQuery_returnsAllOrders() {
        // Arrange
        var orders = List.of(new PurchaseOrder(), new PurchaseOrder());
        when(purchaseOrderRepository.findAll()).thenReturn(orders);

        // Act
        var result = purchaseOrderQueryService.handle(new GetAllPurchaseOrdersQuery());

        // Assert
        assertThat(result).hasSize(2);
        verify(purchaseOrderRepository).findAll();
    }

    @Test
    void handle_whenGetBySupplierIdQuery_returnsMatchingOrders() {
        // Arrange
        Long supplierId = 3L;
        var orders = List.of(new PurchaseOrder());
        when(purchaseOrderRepository.findAllBySupplierId(supplierId)).thenReturn(orders);

        // Act
        var result = purchaseOrderQueryService.handle(new GetPurchaseOrdersBySupplierIdQuery(supplierId));

        // Assert
        assertThat(result).hasSize(1);
        verify(purchaseOrderRepository).findAllBySupplierId(supplierId);
    }

    @Test
    void handle_whenGetByIdQuery_returnsMatchingOrder() {
        // Arrange
        Long orderId = 15L;
        var order = new PurchaseOrder();
        order.setId(orderId);
        when(purchaseOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        var result = purchaseOrderQueryService.handle(new GetPurchaseOrderByIdQuery(orderId));

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(orderId);
        verify(purchaseOrderRepository).findById(orderId);
    }
}
