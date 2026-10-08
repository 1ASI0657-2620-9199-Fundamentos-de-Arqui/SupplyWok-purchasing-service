package aurora.supply_wok.platform.suppliers.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Client response resource.
 */
@Schema(
        name = "ClientResponse",
        description = "Client information response",
        example = """
                {"id": 1, "name": "Gran Dragon Chifa", "district": "San Isidro", "status": "active"}
                """
)
public record ClientResource(
        @Schema(description = "Client unique identifier", example = "1")
        Long id,

        @Schema(description = "Restaurant display name", example = "Gran Dragon Chifa")
        String name,

        @Schema(description = "Restaurant district", example = "San Isidro")
        String district,

        @Schema(description = "Supplier-side client status", example = "active")
        String status
) {
}
