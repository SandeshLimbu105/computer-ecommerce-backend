package org.texas.computerecommerce.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.texas.computerecommerce.Entity.User;
import org.texas.computerecommerce.Repository.UserRepository;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Jwtutil jwtutil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("incoming request: {}", request.getRequestURI());
        final String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ FIX (bug #11): trim the token
        String token = requestTokenHeader.split("Bearer")[1].trim();

        // ✅ FIX (bug #11): wrap token parsing in try/catch so invalid/expired
        // tokens don't throw unhandled exceptions (which caused 500 errors).
        // Instead, we continue the chain unauthenticated, and Spring Security
        // returns the proper 401/403 downstream.
        String email = null;
        try {
            email = jwtutil.getUserEmailFromToken(token);
        } catch (Exception ex) {
            log.warn("Rejected request with invalid/expired JWT: {}", ex.getMessage());
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                User user = userRepository.findByEmail(email).orElseThrow();
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ex) {
                // e.g. the user tied to this token's email was deleted after token was issued
                log.warn("Valid token but no matching user found for {}: {}", email, ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}