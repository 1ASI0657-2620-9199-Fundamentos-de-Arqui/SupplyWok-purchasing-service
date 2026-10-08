package aurora.supply_wok.platform.shared.infrastructure.security;

import java.util.List;

/**
 * Represents the authenticated user extracted from the security token.
 *
 * @param userId the user id (acts as restaurant tenant id for RESTAURANT accounts)
 * @param email  the user email (used to resolve supplier identity)
 * @param roles  the user roles without or with ROLE_ prefix
 */
public record AuthenticatedUser(
        Long userId,
        String email,
        List<String> roles
) {
    public AuthenticatedUser {
        roles = roles == null ? List.of() : List.copyOf(roles);
    }

    /**
     * Checks if the user has the specified role.
     *
     * @param r the role name, e.g. "RESTAURANT" or "ROLE_RESTAURANT"
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String r) {
        if (r == null || roles == null) {
            return false;
        }
        String normalized = r.startsWith("ROLE_") ? r.substring(5) : r;
        return roles.stream().anyMatch(role -> {
            String roleNorm = role.startsWith("ROLE_") ? role.substring(5) : role;
            return roleNorm.equalsIgnoreCase(normalized);
        });
    }
}
