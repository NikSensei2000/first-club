package com.firstclub.membership.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, 
                                  CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        logger.debug("Processing authentication for request: {} {}", request.getMethod(), requestURI);
        
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                logger.debug("JWT token found in request");
                
                if (tokenProvider.validateToken(jwt)) {
                    logger.debug("JWT token is valid, extracting user information");
                    
                    Long userId = tokenProvider.getUserIdFromToken(jwt);
                    logger.debug("Extracted userId from token: {}", userId);
                    
                    UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                    logger.debug("Loaded user details for userId: {}, username: {}", 
                               userId, userDetails.getUsername());
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Authentication set in security context for user: {}", userDetails.getUsername());
                } else {
                    logger.warn("Invalid JWT token in request to: {}", requestURI);
                }
            } else {
                logger.trace("No JWT token found in request to: {}", requestURI);
            }
        } catch (Exception ex) {
            logger.error("Failed to set user authentication in security context for request: {} - Error: {}", 
                        requestURI, ex.getMessage());
            logger.debug("Authentication error details", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
