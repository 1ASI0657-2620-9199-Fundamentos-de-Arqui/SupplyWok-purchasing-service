package aurora.supply_wok.platform.shared.infrastructure.security;

/**
 * Exception thrown when a requested resource is not found or not accessible within the current tenant scope.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
