package org.texas.computerecommerce.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.texas.computerecommerce.Entity.User;
import org.texas.computerecommerce.Exception.ForbiddenOperationException;

/**
 * Utility class for checking ownership of resources.
 * Used by controllers to prevent users from accessing other users' data.
 */
public class SecurityUtils {

    /**
     * Get the currently authenticated user from the security context.
     * @return The authenticated User
     * @throws ForbiddenOperationException if no user is authenticated
     */
    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new ForbiddenOperationException("Not authenticated");
        }
        return (User) auth.getPrincipal();
    }

    /**
     * Check if the current user is an admin.
     */
    public static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Ensure the current user is either the resource owner or an admin.
     * Throws ForbiddenOperationException if neither is true.
     *
     * @param ownerUserId The userId that owns the resource.
     *                    Pass null to require ADMIN only.
     */
    public static void requireSelfOrAdmin(Long ownerUserId) {
        User current = getCurrentUser();

        // Admin can access anything
        if (isAdmin()) return;

        // If null, admin-only endpoint
        if (ownerUserId == null) {
            throw new ForbiddenOperationException("Admin access required");
        }

        // Otherwise, must be the owner
        if (!current.getUserId().equals(ownerUserId)) {
            throw new ForbiddenOperationException("You can only access your own data");
        }
    }
}