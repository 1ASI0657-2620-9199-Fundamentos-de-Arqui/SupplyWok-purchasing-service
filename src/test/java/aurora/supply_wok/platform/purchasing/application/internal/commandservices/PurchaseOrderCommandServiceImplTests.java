package aurora.supply_wok.platform.purchasing.application.internal.commandservices;

import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.CreatePurchaseOrderItemCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.DeletePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.UpdatePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.commands.UpdatePurchaseOrderStatusCommand;
import aurora.supply_wok.platform.purchasing.domain.model.entities.PurchaseOrderItem;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderPriority;
import aurora.supply_wok.platform.purchasing.domain.model.valueobjects.EPurchaseOrderStatus;
import aurora.supply_wok.platform.purchasing.domain.repositories.PurchaseOrderRepository;
import aurora.supply_wok.platform.suppliers.interfaces.acl.SuppliersContextFacade;
import aurora.supply_wok.platform.suppliers.interfaces.acl.resources.SupplierIdentityAclResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderCommandServiceImplTests {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private SuppliersContextFacade suppliersContextFacade;

    @InjectMocks
    private PurchaseOrderCommandServiceImpl purchaseOrderCommandService;

    @Test
    void handleCreate_whenSupplierNotFound_returnsEmptyAndNeverSaves() {
        // Arrange
        var command = new CreatePurchaseOrderCommand(
                "PO-001",
                10L,
                "Supplier A",
                "Wok 1",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "Medium",
                "Pending",
                List.of(new CreatePurchaseOrderItemCommand(null, 1L, "Arroz", BigDecimal.TEN, BigDecimal.ONE, "KG"))
        );
        when(suppliersContextFacade.getSupplierIdentityById(10L)).thenReturn(Optional.empty());

        // Act
        var result = purchaseOrderCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void handleCreate_whenStatusIsNotPending_throwsIllegalArgumentException() {
        // Arrange
        var command = new CreatePurchaseOrderCommand(
                "PO-001",
                10L,
                "Supplier A",
                "Wok 1",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "Medium",
                "Confirmed",
                List.of(new CreatePurchaseOrderItemCommand(null, 1L, "Arroz", BigDecimal.TEN, BigDecimal.ONE, "KG"))
        );
        when(suppliersContextFacade.getSupplierIdentityById(10L))
                .thenReturn(Optional.of(new SupplierIdentityAclResource(10L, "Supplier A")));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> purchaseOrderCommandService.handle(command));
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void handleCreate_whenEstimatedDateBeforeOrderDate_throwsIllegalArgumentException() {
        // Arrange
        var command = new CreatePurchaseOrderCommand(
                "PO-001",
                10L,
                "Supplier A",
                "Wok 1",
                LocalDate.now(),
                LocalDate.now().minusDays(1),
                "Medium",
                "Pending",
                List.of(new CreatePurchaseOrderItemCommand(null, 1L, "Arroz", BigDecimal.TEN, BigDecimal.ONE, "KG"))
        );
        when(suppliersContextFacade.getSupplierIdentityById(10L))
                .thenReturn(Optional.of(new SupplierIdentityAclResource(10L, "Supplier A")));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> purchaseOrderCommandService.handle(command));
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void handleCreate_whenItemsListIsEmpty_throwsIllegalArgumentException() {
        // Arrange
        var command = new CreatePurchaseOrderCommand(
                "PO-001",
                10L,
                "Supplier A",
                "Wok 1",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "Medium",
                "Pending",
                Collections.emptyList()
        );
        when(suppliersContextFacade.getSupplierIdentityById(10L))
                .thenReturn(Optional.of(new SupplierIdentityAclResource(10L, "Supplier A")));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> purchaseOrderCommandService.handle(command));
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void handleCreate_whenCodeAlreadyExists_throwsIllegalArgumentException() {
        // Arrange
        var command = new CreatePurchaseOrderCommand(
                "PO-DUPLICATE",
                10L,
                "Supplier A",
                "Wok 1",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "Medium",
                "Pending",
                List.of(new CreatePurchaseOrderItemCommand(null, 1L, "Arroz", BigDecimal.TEN, BigDecimal.ONE, "KG"))
        );
        when(suppliersContextFacade.getSupplierIdentityById(10L))
                .thenReturn(Optional.of(new SupplierIdentityAclResource(10L, "Supplier A")));
        when(purchaseOrderRepository.existsByCode("PO-DUPLICATE")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> purchaseOrderCommandService.handle(command));
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void handleCreate_whenValidCommand_savesOrderAndLinksSupplierClient() {
        // Arrange
        var command = new CreatePurchaseOrderCommand(
                "PO-001",
                10L,
                "Supplier A",
                "Wok 1",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "Medium",
                "Pending",
                List.of(new CreatePurchaseOrderItemCommand(null, 1L, "Arroz", BigDecimal.TEN, BigDecimal.ONE, "KG"))
        );
        when(suppliersContextFacade.getSupplierIdentityById(10L))
                .thenReturn(Optional.of(new SupplierIdentityAclResource(10L, "Supplier A")));
        when(purchaseOrderRepository.existsByCode("PO-001")).thenReturn(false);
        when(purchaseOrderRepository.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> {
                    PurchaseOrder saved = invocation.getArgument(0);
                    saved.setId(100L);
                    return saved;
                });

        // Act
        var result = purchaseOrderCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(100L);
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        verify(suppliersContextFacade).ensureSupplierClientLinked(10L, "Wok 1");
    }

    @Test
    void handleUpdate_whenOrderNotFound_returnsEmpty() {
        // Arrange
        var command = new UpdatePurchaseOrderCommand(
                99L,
                "PO-99",
                10L,
                "Supplier A",
                "Wok 1",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "Medium",
                "Pending",
                List.of(new CreatePurchaseOrderItemCommand(1L, 1L, "Arroz", BigDecimal.TEN, BigDecimal.ONE, "KG"))
        );
        when(purchaseOrderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        var result = purchaseOrderCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void handleUpdate_whenSupplierNotFound_returnsEmpty() {
        // Arrange
        var existingOrder = new PurchaseOrder();
        existingOrder.setId(99L);
        existingOrder.setStatus(EPurchaseOrderStatus.Pending);

        var command = new UpdatePurchaseOrderCommand(
                99L,
                "PO-99",
                10L,
                "Supplier A",
                "Wok 1",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "Medium",
                "Pending",
                List.of(new CreatePurchaseOrderItemCommand(1L, 1L, "Arroz", BigDecimal.TEN, BigDecimal.ONE, "KG"))
        );
        when(purchaseOrderRepository.findById(99L)).thenReturn(Optional.of(existingOrder));
        when(suppliersContextFacade.getSupplierIdentityById(10L)).thenReturn(Optional.empty());

        // Act
        var result = purchaseOrderCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void handleUpdate_whenInvalidStatusTransition_throwsIllegalArgumentException() {
        // Arrange
        var existingOrder = new PurchaseOrder();
        existingOrder.setId(99L);
        existingOrder.setStatus(EPurchaseOrderStatus.Delivered);

        var command = new UpdatePurchaseOrderCommand(
                99L,
                "PO-99",
                10L,
                "Supplier A",
                "Wok 1",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "Medium",
                "Pending",
                List.of(new CreatePurchaseOrderItemCommand(1L, 1L, "Arroz", BigDecimal.TEN, BigDecimal.ONE, "KG"))
        );
        when(purchaseOrderRepository.findById(99L)).thenReturn(Optional.of(existingOrder));
        when(suppliersContextFacade.getSupplierIdentityById(10L))
                .thenReturn(Optional.of(new SupplierIdentityAclResource(10L, "Supplier A")));
        when(purchaseOrderRepository.existsByCodeAndIdNot("PO-99", 99L)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> purchaseOrderCommandService.handle(command));
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void handleUpdate_whenValidCommand_savesUpdatedOrder() {
        // Arrange
        var existingOrder = new PurchaseOrder();
        existingOrder.setId(50L);
        existingOrder.setStatus(EPurchaseOrderStatus.Pending);

        var command = new UpdatePurchaseOrderCommand(
                50L,
                "PO-50",
                10L,
                "Supplier A",
                "Wok Updated",
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                "High",
                "Confirmed",
                List.of(new CreatePurchaseOrderItemCommand(1L, 1L, "Arroz", BigDecimal.TEN, BigDecimal.ONE, "KG"))
        );
        when(purchaseOrderRepository.findById(50L)).thenReturn(Optional.of(existingOrder));
        when(suppliersContextFacade.getSupplierIdentityById(10L))
                .thenReturn(Optional.of(new SupplierIdentityAclResource(10L, "Supplier A")));
        when(purchaseOrderRepository.existsByCodeAndIdNot("PO-50", 50L)).thenReturn(false);
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        var result = purchaseOrderCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(EPurchaseOrderStatus.Confirmed);
        assertThat(result.get().getRestaurantName()).isEqualTo("Wok Updated");
        verify(purchaseOrderRepository).save(existingOrder);
        verify(suppliersContextFacade).ensureSupplierClientLinked(10L, "Wok Updated");
    }

    @Test
    void handleUpdateStatus_whenOrderNotFound_returnsEmpty() {
        // Arrange
        var command = new UpdatePurchaseOrderStatusCommand(1L, "Confirmed");
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        var result = purchaseOrderCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void handleUpdateStatus_whenValidStatus_savesOrderWithNewStatus() {
        // Arrange
        var existingOrder = new PurchaseOrder();
        existingOrder.setId(1L);
        existingOrder.setStatus(EPurchaseOrderStatus.Pending);

        var command = new UpdatePurchaseOrderStatusCommand(1L, "Confirmed");
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        var result = purchaseOrderCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(EPurchaseOrderStatus.Confirmed);
        verify(purchaseOrderRepository).save(existingOrder);
    }

    @Test
    void handleDelete_whenOrderNotFound_returnsFalse() {
        // Arrange
        var command = new DeletePurchaseOrderCommand(1L);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean deleted = purchaseOrderCommandService.handle(command);

        // Assert
        assertThat(deleted).isFalse();
        verify(purchaseOrderRepository, never()).delete(any());
    }

    @Test
    void handleDelete_whenOrderExists_deletesOrderAndReturnsTrue() {
        // Arrange
        var existingOrder = new PurchaseOrder();
        existingOrder.setId(1L);

        var command = new DeletePurchaseOrderCommand(1L);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        // Act
        boolean deleted = purchaseOrderCommandService.handle(command);

        // Assert
        assertThat(deleted).isTrue();
        verify(purchaseOrderRepository).delete(existingOrder);
    }
}
