package aurora.supply_wok.platform.purchasing.interfaces.rest;

import aurora.supply_wok.platform.purchasing.application.commandservices.PurchaseOrderCommandService;
import aurora.supply_wok.platform.purchasing.application.queryservices.PurchaseOrderQueryService;
import aurora.supply_wok.platform.purchasing.domain.model.aggregates.PurchaseOrder;
import aurora.supply_wok.platform.purchasing.domain.model.commands.DeletePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetAllPurchaseOrdersQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrderByIdAndRestaurantIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrderByIdAndSupplierIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrderByIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrdersByRestaurantIdQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrdersBySupplierIdQuery;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.CreatePurchaseOrderResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.PurchaseOrderResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.UpdatePurchaseOrderResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.UpdatePurchaseOrderStatusResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.transform.CreatePurchaseOrderCommandFromResourceAssembler;
import aurora.supply_wok.platform.purchasing.interfaces.rest.transform.PurchaseOrderResourceFromEntityAssembler;
import aurora.supply_wok.platform.purchasing.interfaces.rest.transform.UpdatePurchaseOrderCommandFromResourceAssembler;
import aurora.supply_wok.platform.purchasing.interfaces.rest.transform.UpdatePurchaseOrderStatusCommandFromResourceAssembler;
import aurora.supply_wok.platform.shared.infrastructure.security.CurrentUserProvider;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * REST controller that exposes purchase order endpoints with tenant isolation and role authorization.
 */
@RestController
@RequestMapping(value = "/api/v1/purchase-orders", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Purchase Orders", description = "Purchase order management endpoints")
public class PurchaseOrdersController {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final PurchaseOrderQueryService purchaseOrderQueryService;
    private final CurrentUserProvider currentUserProvider;
    private final SupplierRepository supplierRepository;

    public PurchaseOrdersController(PurchaseOrderCommandService purchaseOrderCommandService,
                                    PurchaseOrderQueryService purchaseOrderQueryService,
                                    CurrentUserProvider currentUserProvider,
                                    SupplierRepository supplierRepository) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.purchaseOrderQueryService = purchaseOrderQueryService;
        this.currentUserProvider = currentUserProvider;
        this.supplierRepository = supplierRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Create purchase order", description = "Creates a new purchase order for the calling restaurant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Purchase order created successfully",
                    content = @Content(schema = @Schema(implementation = PurchaseOrderResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<PurchaseOrderResource> createPurchaseOrder(@Valid @RequestBody CreatePurchaseOrderResource resource) {
        try {
            Long restaurantId = currentUserProvider.userId();
            var command = CreatePurchaseOrderCommandFromResourceAssembler.toCommandFromResource(resource, restaurantId);
            var purchaseOrder = purchaseOrderCommandService.handle(command);

            if (purchaseOrder.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var purchaseOrderResource = PurchaseOrderResourceFromEntityAssembler.toResourceFromEntity(purchaseOrder.get());
            return new ResponseEntity<>(purchaseOrderResource, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RESTAURANT', 'SUPPLIER', 'ADMIN')")
    @Operation(summary = "Get all purchase orders", description = "Retrieves purchase orders scoped to the caller.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase orders retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PurchaseOrderResource.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<PurchaseOrderResource>> getAllPurchaseOrders() {
        var user = currentUserProvider.get();
        List<PurchaseOrder> purchaseOrders;

        if (user.hasRole("RESTAURANT")) {
            purchaseOrders = purchaseOrderQueryService.handle(new GetPurchaseOrdersByRestaurantIdQuery(user.userId()));
        } else if (user.hasRole("SUPPLIER")) {
            var supplierOpt = supplierRepository.findByEmailIgnoreCase(user.email());
            if (supplierOpt.isEmpty()) {
                purchaseOrders = List.of();
            } else {
                purchaseOrders = purchaseOrderQueryService.handle(new GetPurchaseOrdersBySupplierIdQuery(supplierOpt.get().getId()));
            }
        } else {
            purchaseOrders = purchaseOrderQueryService.handle(new GetAllPurchaseOrdersQuery());
        }

        var resources = purchaseOrders.stream()
                .map(PurchaseOrderResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{purchaseOrderId}")
    @PreAuthorize("hasAnyRole('RESTAURANT', 'SUPPLIER', 'ADMIN')")
    @Operation(summary = "Get purchase order by id", description = "Retrieves a purchase order by ID if caller is owner restaurant or owning supplier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase order found",
                    content = @Content(schema = @Schema(implementation = PurchaseOrderResource.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found")
    })
    public ResponseEntity<PurchaseOrderResource> getPurchaseOrderById(@PathVariable Long purchaseOrderId) {
        var user = currentUserProvider.get();
        Optional<PurchaseOrder> purchaseOrder = Optional.empty();

        if (user.hasRole("ADMIN")) {
            purchaseOrder = purchaseOrderQueryService.handle(new GetPurchaseOrderByIdQuery(purchaseOrderId));
        } else {
            if (user.hasRole("RESTAURANT")) {
                purchaseOrder = purchaseOrderQueryService.handle(
                        new GetPurchaseOrderByIdAndRestaurantIdQuery(purchaseOrderId, user.userId()));
            }
            if (purchaseOrder.isEmpty() && user.hasRole("SUPPLIER")) {
                var supplierOpt = supplierRepository.findByEmailIgnoreCase(user.email());
                if (supplierOpt.isPresent()) {
                    purchaseOrder = purchaseOrderQueryService.handle(
                            new GetPurchaseOrderByIdAndSupplierIdQuery(purchaseOrderId, supplierOpt.get().getId()));
                }
            }
        }

        if (purchaseOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PurchaseOrderResourceFromEntityAssembler.toResourceFromEntity(purchaseOrder.get()));
    }

    @PutMapping("/{purchaseOrderId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Update purchase order", description = "Updates an existing purchase order for the owning restaurant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase order updated successfully",
                    content = @Content(schema = @Schema(implementation = PurchaseOrderResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Purchase order or supplier not found")
    })
    public ResponseEntity<PurchaseOrderResource> updatePurchaseOrder(@PathVariable Long purchaseOrderId,
                                                                     @Valid @RequestBody UpdatePurchaseOrderResource resource) {
        try {
            Long restaurantId = currentUserProvider.userId();
            var command = UpdatePurchaseOrderCommandFromResourceAssembler.toCommandFromResource(purchaseOrderId, resource, restaurantId);
            var purchaseOrder = purchaseOrderCommandService.handle(command);

            if (purchaseOrder.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(PurchaseOrderResourceFromEntityAssembler.toResourceFromEntity(purchaseOrder.get()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{purchaseOrderId}/status")
    @PreAuthorize("hasAnyRole('RESTAURANT', 'SUPPLIER', 'ADMIN')")
    @Operation(summary = "Update purchase order status", description = "Updates the status if caller is owner restaurant or owning supplier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase order status updated successfully",
                    content = @Content(schema = @Schema(implementation = PurchaseOrderResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found")
    })
    public ResponseEntity<PurchaseOrderResource> updatePurchaseOrderStatus(@PathVariable Long purchaseOrderId,
                                                                           @Valid @RequestBody UpdatePurchaseOrderStatusResource resource) {
        try {
            var user = currentUserProvider.get();
            Long restaurantId = null;
            Long supplierId = null;

            if (user.hasRole("ADMIN")) {
                // Admin has full access
            } else if (user.hasRole("RESTAURANT")) {
                restaurantId = user.userId();
            } else if (user.hasRole("SUPPLIER")) {
                var supplierOpt = supplierRepository.findByEmailIgnoreCase(user.email());
                if (supplierOpt.isEmpty()) {
                    return ResponseEntity.notFound().build();
                }
                supplierId = supplierOpt.get().getId();
            }

            var command = UpdatePurchaseOrderStatusCommandFromResourceAssembler.toCommandFromResource(
                    purchaseOrderId, resource, restaurantId, supplierId);
            var purchaseOrder = purchaseOrderCommandService.handle(command);

            if (purchaseOrder.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(PurchaseOrderResourceFromEntityAssembler.toResourceFromEntity(purchaseOrder.get()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{purchaseOrderId}")
    @PreAuthorize("hasRole('RESTAURANT')")
    @Operation(summary = "Delete purchase order", description = "Deletes an existing purchase order for the owning restaurant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Purchase order deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found")
    })
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable Long purchaseOrderId) {
        Long restaurantId = currentUserProvider.userId();
        var deleted = purchaseOrderCommandService.handle(new DeletePurchaseOrderCommand(purchaseOrderId, restaurantId));
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
