package de.hotiovip.chatAppBackend.security;

import de.hotiovip.chatAppBackend.entity.User;
import de.hotiovip.chatAppBackend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private  UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix

            try {
                Optional<Long> id = jwtUtil.getTokenSubject(token);
                if (id.isPresent()) {
                    Optional<User> user = userService.getUserById(id.get());
                    if (user.isPresent()) {
                        // Create an Authentication object (no roles)
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user.get().getUsername(), null, new ArrayList<>());

                        // Set the SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                    else {
                        System.out.println("Could not retrieve user from token's id value");
                    }
                }
                else {
                    System.out.println("Could no retrieve token's subject (user id)");
                }
            } catch (Exception e) {
                // If token is invalid, log the error and proceed without setting authentication
                System.out.println("Invalid token: " + e.getMessage());
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}