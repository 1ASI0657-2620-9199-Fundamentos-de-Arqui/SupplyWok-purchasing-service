package aurora.supply_wok.platform.suppliers.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "CreateSupplierRequest",
        description = "Request payload for creating a new supplier",
        example = """
                {"name":"Golden Wok Produce","contactName":"Marta Ruiz","email":"marta@goldenwok.com","phone":"+51 999 888 777","category":"Vegetables","linkedDate":"2026-07-03","sla":"24h","responseTime":"2h"}
                """
)
public record CreateSupplierResource(
        @NotBlank
        @Size(max = 100)
        @Schema(description = "Supplier display name", example = "Golden Wok Produce", minLength = 1, maxLength = 100)
        String name,

        @NotBlank
        @Size(max = 100)
        @Schema(description = "Supplier contact name", example = "Marta Ruiz", minLength = 1, maxLength = 100)
        String contactName,

        @NotBlank
        @Email
        @Size(max = 120)
        @Schema(description = "Supplier contact email", example = "marta@goldenwok.com", minLength = 1, maxLength = 120)
        String email,

        @NotBlank
        @Size(max = 40)
        @Schema(description = "Supplier contact phone", example = "+51 999 888 777", minLength = 1, maxLength = 40)
        String phone,

        @NotBlank
        @Size(max = 80)
        @Schema(description = "Supplier category", example = "Vegetables", minLength = 1, maxLength = 80)
        String category,

        @Schema(description = "Supplier linked date. Defaults to today when omitted.", example = "2026-07-03")
        String linkedDate,

        @NotBlank
        @Size(max = 40)
        @Schema(description = "Supplier SLA label", example = "24h", minLength = 1, maxLength = 40)
        String sla,

        @NotBlank
        @Size(max = 40)
        @Schema(description = "Supplier response time label", example = "2h", minLength = 1, maxLength = 40)
        String responseTime
) {
}
