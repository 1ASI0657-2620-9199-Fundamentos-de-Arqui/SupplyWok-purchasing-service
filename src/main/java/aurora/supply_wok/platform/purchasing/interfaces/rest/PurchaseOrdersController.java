package aurora.supply_wok.platform.purchasing.interfaces.rest;

import aurora.supply_wok.platform.purchasing.application.commandservices.PurchaseOrderCommandService;
import aurora.supply_wok.platform.purchasing.application.queryservices.PurchaseOrderQueryService;
import aurora.supply_wok.platform.purchasing.domain.model.commands.DeletePurchaseOrderCommand;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetAllPurchaseOrdersQuery;
import aurora.supply_wok.platform.purchasing.domain.model.queries.GetPurchaseOrderByIdQuery;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.CreatePurchaseOrderResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.PurchaseOrderResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.UpdatePurchaseOrderResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.resources.UpdatePurchaseOrderStatusResource;
import aurora.supply_wok.platform.purchasing.interfaces.rest.transform.CreatePurchaseOrderCommandFromResourceAssembler;
import aurora.supply_wok.platform.purchasing.interfaces.rest.transform.PurchaseOrderResourceFromEntityAssembler;
import aurora.supply_wok.platform.purchasing.interfaces.rest.transform.UpdatePurchaseOrderCommandFromResourceAssembler;
import aurora.supply_wok.platform.purchasing.interfaces.rest.transform.UpdatePurchaseOrderStatusCommandFromResourceAssembler;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that exposes purchase order endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/purchase-orders", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Purchase Orders", description = "Purchase order management endpoints")
public class PurchaseOrdersController {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final PurchaseOrderQueryService purchaseOrderQueryService;

    public PurchaseOrdersController(PurchaseOrderCommandService purchaseOrderCommandService,
                                    PurchaseOrderQueryService purchaseOrderQueryService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.purchaseOrderQueryService = purchaseOrderQueryService;
    }

    @PostMapping
    @Operation(summary = "Create purchase order", description = "Creates a new purchase order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Purchase order created successfully",
                    content = @Content(schema = @Schema(implementation = PurchaseOrderResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<PurchaseOrderResource> createPurchaseOrder(@Valid @RequestBody CreatePurchaseOrderResource resource) {
        try {
            var command = CreatePurchaseOrderCommandFromResourceAssembler.toCommandFromResource(resource);
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
    @Operation(summary = "Get all purchase orders", description = "Retrieves all registered purchase orders.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase orders retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PurchaseOrderResource.class))))
    })
    public ResponseEntity<List<PurchaseOrderResource>> getAllPurchaseOrders() {
        var purchaseOrders = purchaseOrderQueryService.handle(new GetAllPurchaseOrdersQuery())
                .stream()
                .map(PurchaseOrderResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(purchaseOrders);
    }

    @GetMapping("/{purchaseOrderId}")
    @Operation(summary = "Get purchase order by id", description = "Retrieves a purchase order by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase order found",
                    content = @Content(schema = @Schema(implementation = PurchaseOrderResource.class))),
            @ApiResponse(responseCode = "404", description = "Purchase order not found")
    })
    public ResponseEntity<PurchaseOrderResource> getPurchaseOrderById(@PathVariable Long purchaseOrderId) {
        var purchaseOrder = purchaseOrderQueryService.handle(new GetPurchaseOrderByIdQuery(purchaseOrderId));
        if (purchaseOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PurchaseOrderResourceFromEntityAssembler.toResourceFromEntity(purchaseOrder.get()));
    }

    @PutMapping("/{purchaseOrderId}")
    @Operation(summary = "Update purchase order", description = "Updates an existing purchase order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase order updated successfully",
                    content = @Content(schema = @Schema(implementation = PurchaseOrderResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Purchase order or supplier not found")
    })
    public ResponseEntity<PurchaseOrderResource> updatePurchaseOrder(@PathVariable Long purchaseOrderId,
                                                                     @Valid @RequestBody UpdatePurchaseOrderResource resource) {
        try {
            var command = UpdatePurchaseOrderCommandFromResourceAssembler.toCommandFromResource(purchaseOrderId, resource);
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
    @Operation(summary = "Update purchase order status", description = "Updates only the status of an existing purchase order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase order status updated successfully",
                    content = @Content(schema = @Schema(implementation = PurchaseOrderResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found")
    })
    public ResponseEntity<PurchaseOrderResource> updatePurchaseOrderStatus(@PathVariable Long purchaseOrderId,
                                                                           @Valid @RequestBody UpdatePurchaseOrderStatusResource resource) {
        try {
            var command = UpdatePurchaseOrderStatusCommandFromResourceAssembler.toCommandFromResource(purchaseOrderId, resource);
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
    @Operation(summary = "Delete purchase order", description = "Deletes an existing purchase order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Purchase order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found")
    })
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable Long purchaseOrderId) {
        var deleted = purchaseOrderCommandService.handle(new DeletePurchaseOrderCommand(purchaseOrderId));
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
