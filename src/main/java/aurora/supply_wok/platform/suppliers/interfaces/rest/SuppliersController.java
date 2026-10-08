package aurora.supply_wok.platform.suppliers.interfaces.rest;

import aurora.supply_wok.platform.suppliers.application.commandservices.SupplierCommandService;
import aurora.supply_wok.platform.suppliers.application.queryservices.SupplierQueryService;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllSuppliersQuery;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.CreateSupplierResource;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.SupplierResource;
import aurora.supply_wok.platform.suppliers.interfaces.rest.transform.CreateSupplierCommandFromResourceAssembler;
import aurora.supply_wok.platform.suppliers.interfaces.rest.transform.SupplierResourceFromEntityAssembler;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that exposes supplier listing endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Suppliers", description = "Supplier management endpoints")
public class SuppliersController {

    private final SupplierCommandService supplierCommandService;
    private final SupplierQueryService supplierQueryService;

    public SuppliersController(SupplierCommandService supplierCommandService, SupplierQueryService supplierQueryService) {
        this.supplierCommandService = supplierCommandService;
        this.supplierQueryService = supplierQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new supplier", description = "Creates a new supplier.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Supplier created successfully",
                    content = @Content(schema = @Schema(implementation = SupplierResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<SupplierResource> createSupplier(@Valid @RequestBody CreateSupplierResource resource) {
        try {
            var command = CreateSupplierCommandFromResourceAssembler.toCommandFromResource(resource);
            var supplier = supplierCommandService.handle(command);
            return new ResponseEntity<>(SupplierResourceFromEntityAssembler.toResourceFromEntity(supplier), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all suppliers", description = "Retrieves all registered suppliers.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Suppliers retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SupplierResource.class)))
            )
    })
    public ResponseEntity<List<SupplierResource>> getAllSuppliers() {
        var suppliers = supplierQueryService.handle(new GetAllSuppliersQuery());
        var resources = suppliers.stream()
                .map(SupplierResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
