package com.evo.security;

import com.evo.UserAuthentication;
import com.evo.UserAuthority;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFilter.class);
    private final AuthorityService authorityService;

    public CustomAuthenticationFilter(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (!(securityContext.getAuthentication() instanceof JwtAuthenticationToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) securityContext.getAuthentication();
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Jwt token = authentication.getToken();
        Boolean isClient = Boolean.FALSE;

        Set<SimpleGrantedAuthority> grantedPermissions = new HashSet<>();
        Optional<UserAuthority> optionalUserAuthority = enrichAuthority(token);
        Long isRoot = optionalUserAuthority.map(UserAuthority::getIsRoot).orElse(0L);

        optionalUserAuthority.ifPresent(userAuthority ->
                userAuthority.getGrantedPermissions().forEach(permission ->
                        grantedPermissions.add(new SimpleGrantedAuthority(permission))));

        String username = StringUtils.hasText(token.getClaimAsString("email"))
                ? token.getClaimAsString("email")
                : token.getClaimAsString("sub");


        User principal = new User(username, "", grantedPermissions);
        AbstractAuthenticationToken auth =
                new UserAuthentication(principal, token, grantedPermissions, isRoot, false);

        SecurityContextHolder.getContext().setAuthentication(auth);
        logSecurityContextDetails();
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        return !(authentication instanceof JwtAuthenticationToken);
    }

    private void logSecurityContextDetails() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication Details:");
        logger.info("Principal: {}", authentication.getPrincipal());
        logger.info("Credentials: {}", authentication.getCredentials());

        logger.info("Authorities:");
        authentication.getAuthorities().forEach(authority ->
                logger.info(" - {}", authority.getAuthority())
        );

        logger.info("Details: {}", authentication.getDetails());
        logger.info("Name: {}", authentication.getName());
        logger.info("Authenticated: {}", authentication.isAuthenticated());
    }

    private Optional<UserAuthority> enrichAuthority(Jwt token) {
        String username = StringUtils.hasText(token.getClaimAsString("email"))
                ? token.getClaimAsString("preferred_username")
                : token.getClaimAsString("sub");

        if (!StringUtils.hasText(username)) {
            log.error("JWT token does not contain 'email' or 'sub' claim");
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(authorityService.getUserAuthority(username));
        } catch (Exception e) {
            log.error("Failed to fetch user authority for user '{}'", username, e);
            return Optional.empty();
        }
    }
}
