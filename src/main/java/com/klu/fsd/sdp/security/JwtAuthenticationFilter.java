package com.klu.fsd.sdp.security;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.klu.fsd.sdp.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        String path = request.getRequestURI();
        
        // Skip authentication for public endpoints
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            final String jwtToken = requestTokenHeader.substring(7);
            try {
                final String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                final String role = jwtTokenUtil.getRoleFromToken(jwtToken);
                
                // Verify the user exists
                userRepository.findByEmail(username).ifPresent(user -> {
                    // If token is valid
                    if (jwtTokenUtil.validateToken(jwtToken, username) && user.isActive()) {
                        // Store the authentication info in request attribute
                        request.setAttribute("authenticated", true);
                        request.setAttribute("userRole", user.getRole().name());
                        request.setAttribute("userId", user.getId());
                        request.setAttribute("userEmail", user.getEmail());
                    }
                });
            } catch (Exception e) {
                logger.warning("Error processing JWT token: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
} 