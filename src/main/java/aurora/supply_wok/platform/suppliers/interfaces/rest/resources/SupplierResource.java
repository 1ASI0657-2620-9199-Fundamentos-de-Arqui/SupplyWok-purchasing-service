package aurora.supply_wok.platform.suppliers.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
        name = "SupplierResponse",
        description = "Supplier details response payload"
)
public record SupplierResource(
        @Schema(description = "Supplier unique identifier", example = "1")
        Long id,

        @Schema(description = "Supplier stable UUID")
        UUID uuid,

        @Schema(description = "Supplier display name", example = "Golden Wok Produce")
        String name,

        @Schema(description = "Supplier contact name", example = "Marta Ruiz")
        String contactName,

        @Schema(description = "Supplier contact email", example = "marta@goldenwok.com")
        String email,

        @Schema(description = "Supplier contact phone", example = "+51 999 888 777")
        String phone,

        @Schema(description = "Supplier category", example = "Vegetables")
        String category,

        @Schema(description = "Supplier linked date", example = "2026-06-18")
        String linkedDate,

        @Schema(description = "Supplier SLA label", example = "24h")
        String sla,

        @Schema(description = "Supplier response time label", example = "2h")
        String responseTime
) {
}
