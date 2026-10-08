package aurora.supply_wok.platform.suppliers.interfaces.acl.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Minimal supplier identity data exposed to other bounded contexts.
 */
@Schema(
        name = "SupplierIdentityAclResource",
        description = "Minimal supplier identity payload exposed through the Suppliers ACL"
)
public record SupplierIdentityAclResource(
        @Schema(description = "Supplier unique identifier", example = "201")
        Long id,

        @Schema(description = "Supplier display name", example = "Golden Wok Produce")
        String name
) {
}
