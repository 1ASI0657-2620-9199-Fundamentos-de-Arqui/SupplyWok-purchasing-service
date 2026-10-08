package aurora.supply_wok.platform.suppliers.interfaces.rest;

import aurora.supply_wok.platform.suppliers.application.queryservices.ClientQueryService;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllClientsBySupplierIdQuery;
import aurora.supply_wok.platform.suppliers.interfaces.rest.resources.ClientResource;
import aurora.supply_wok.platform.suppliers.interfaces.rest.transform.ClientResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that exposes supplier client listing endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/suppliers/{supplierId}/clients", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Supplier Clients", description = "Supplier client read endpoints")
public class SupplierClientsController {

    private final ClientQueryService clientQueryService;

    public SupplierClientsController(ClientQueryService clientQueryService) {
        this.clientQueryService = clientQueryService;
    }

    @GetMapping
    @Operation(
            summary = "Get clients by supplier ID",
            description = "Retrieves all clients linked to the specified supplier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Clients retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ClientResource.class))
            ),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<List<ClientResource>> getAllClientsBySupplierId(
            @PathVariable
            @Parameter(description = "Supplier unique identifier", example = "1", required = true)
            Long supplierId
    ) {
        var query = new GetAllClientsBySupplierIdQuery(supplierId);
        var clients = clientQueryService.handle(query);

        if (clients.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var clientResources = clients.get().stream()
                .map(ClientResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(clientResources);
    }
}
