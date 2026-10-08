package aurora.supply_wok.platform.suppliers.interfaces.rest;

import aurora.supply_wok.platform.suppliers.application.commandservices.CatalogItemCommandService;
import aurora.supply_wok.platform.suppliers.application.queryservices.CatalogItemQueryService;
import aurora.supply_wok.platform.suppliers.domain.model.commands.DeleteCatalogItemCommand;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllCatalogItemsBySupplierIdQuery;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetCatalogItemByIdQuery;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.CatalogItemResource;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.CreateCatalogItemResource;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.UpdateCatalogItemResource;
import aurora.supply_wok.platform.suppliers.interfaces.rest.transform.CatalogItemResourceFromEntityAssembler;
import aurora.supply_wok.platform.suppliers.interfaces.rest.transform.CreateCatalogItemCommandFromResourceAssembler;
import aurora.supply_wok.platform.suppliers.interfaces.rest.transform.UpdateCatalogItemCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * REST controller that exposes supplier catalog endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/suppliers/{supplierId}/catalog-items", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Supplier Catalog Items", description = "Supplier catalog management endpoints")
public class SupplierCatalogItemsController {

    private final CatalogItemCommandService catalogItemCommandService;
    private final CatalogItemQueryService catalogItemQueryService;

    public SupplierCatalogItemsController(CatalogItemCommandService catalogItemCommandService, CatalogItemQueryService catalogItemQueryService) {
        this.catalogItemCommandService = catalogItemCommandService;
        this.catalogItemQueryService = catalogItemQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a catalog item", description = "Creates a new catalog item under the specified supplier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Catalog item created successfully", content = @Content(schema = @Schema(implementation = CatalogItemResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<CatalogItemResource> createCatalogItem(
            @PathVariable
            @Parameter(description = "Supplier unique identifier", example = "1", required = true)
            Long supplierId,
            @Valid @RequestBody CreateCatalogItemResource resource
    ) {
        try {
            var command = CreateCatalogItemCommandFromResourceAssembler.toCommandFromResource(supplierId, resource);
            var catalogItemId = catalogItemCommandService.handle(command);

            if (catalogItemId.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var catalogItem = catalogItemQueryService.handle(new GetCatalogItemByIdQuery(supplierId, catalogItemId.get()));
            if (catalogItem.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            var catalogItemResource = CatalogItemResourceFromEntityAssembler.toResourceFromEntity(catalogItem.get());
            return new ResponseEntity<>(catalogItemResource, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get catalog items by supplier ID", description = "Retrieves all catalog items linked to the specified supplier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catalog items retrieved successfully", content = @Content(schema = @Schema(implementation = CatalogItemResource.class))),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<List<CatalogItemResource>> getAllCatalogItemsBySupplierId(
            @PathVariable
            @Parameter(description = "Supplier unique identifier", example = "1", required = true)
            Long supplierId
    ) {
        var catalogItems = catalogItemQueryService.handle(new GetAllCatalogItemsBySupplierIdQuery(supplierId));
        if (catalogItems.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var catalogItemResources = catalogItems.get().stream()
                .map(CatalogItemResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(catalogItemResources);
    }

    @GetMapping("/{catalogItemId}")
    @Operation(summary = "Get catalog item by ID", description = "Retrieves a specific catalog item owned by the specified supplier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catalog item found", content = @Content(schema = @Schema(implementation = CatalogItemResource.class))),
            @ApiResponse(responseCode = "404", description = "Supplier or catalog item not found")
    })
    public ResponseEntity<CatalogItemResource> getCatalogItemById(
            @PathVariable
            @Parameter(description = "Supplier unique identifier", example = "1", required = true)
            Long supplierId,
            @PathVariable
            @Parameter(description = "Catalog item unique identifier", example = "1", required = true)
            Long catalogItemId
    ) {
        var catalogItem = catalogItemQueryService.handle(new GetCatalogItemByIdQuery(supplierId, catalogItemId));
        if (catalogItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var catalogItemResource = CatalogItemResourceFromEntityAssembler.toResourceFromEntity(catalogItem.get());
        return ResponseEntity.ok(catalogItemResource);
    }

    @PutMapping("/{catalogItemId}")
    @Operation(summary = "Update a catalog item", description = "Updates a catalog item owned by the specified supplier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catalog item updated successfully", content = @Content(schema = @Schema(implementation = CatalogItemResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Supplier or catalog item not found")
    })
    public ResponseEntity<CatalogItemResource> updateCatalogItem(
            @PathVariable
            @Parameter(description = "Supplier unique identifier", example = "1", required = true)
            Long supplierId,
            @PathVariable
            @Parameter(description = "Catalog item unique identifier", example = "1", required = true)
            Long catalogItemId,
            @Valid @RequestBody UpdateCatalogItemResource resource
    ) {
        try {
            var command = UpdateCatalogItemCommandFromResourceAssembler.toCommandFromResource(supplierId, catalogItemId, resource);
            var catalogItem = catalogItemCommandService.handle(command);

            if (catalogItem.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var catalogItemResource = CatalogItemResourceFromEntityAssembler.toResourceFromEntity(catalogItem.get());
            return ResponseEntity.ok(catalogItemResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{catalogItemId}")
    @Operation(summary = "Delete a catalog item", description = "Deletes a catalog item owned by the specified supplier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Catalog item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier or catalog item not found")
    })
    public ResponseEntity<Void> deleteCatalogItem(
            @PathVariable
            @Parameter(description = "Supplier unique identifier", example = "1", required = true)
            Long supplierId,
            @PathVariable
            @Parameter(description = "Catalog item unique identifier", example = "1", required = true)
            Long catalogItemId
    ) {
        var deleted = catalogItemCommandService.handle(new DeleteCatalogItemCommand(supplierId, catalogItemId));
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
