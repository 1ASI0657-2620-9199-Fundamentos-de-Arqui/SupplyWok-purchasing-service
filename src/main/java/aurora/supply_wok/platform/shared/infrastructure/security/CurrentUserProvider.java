package aurora.supply_wok.platform.shared.infrastructure.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Provides access to the authenticated user from the current security context.
 */
@Component
public class CurrentUserProvider {

    /**
     * Resolves the current authenticated user.
     *
     * @return the AuthenticatedUser
     * @throws AuthenticationCredentialsNotFoundException if no authenticated user is present
     */
    public AuthenticatedUser get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("No authenticated user in security context");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUser user) {
            return user;
        }

        Long userId = null;
        String email = authentication.getName();
        if (email != null) {
            try {
                userId = Long.parseLong(email);
            } catch (NumberFormatException ignored) {
            }
        }

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.startsWith("ROLE_") ? auth.substring(5) : auth)
                .toList();

        return new AuthenticatedUser(userId, email, roles);
    }

    /**
     * Returns the current authenticated user's ID.
     *
     * @return the user ID (restaurant ID for RESTAURANT accounts)
     */
    public Long userId() {
        return get().userId();
    }

    /**
     * Returns the current authenticated user's email.
     *
     * @return the email address
     */
    public String email() {
        return get().email();
    }
}
