package com.toptal.soccermanager.configuration.authentication;

import com.toptal.soccermanager.utils.UserInfo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JwtTokenFilter extends OncePerRequestFilter {
    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        /*
       try {
            if (checkJwtToken(request)) {
                String jwt = request.getHeader(HEADER).replace(PREFIX, "");
                UserInfo user = jwtUtils.getUserFromJwtToken(jwt);
                if (user != null) {
                    setUpAuthentication(user);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException
                 | SignatureException | IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
        }
         */
        // Get authorization header and validate
        if (!checkJwtToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        // Get jwt token and validate
        String jwt = request.getHeader(HEADER).replace(PREFIX, "");
        if (!jwtUtils.validateToken(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }
        // Get user and set it on spring security context
        UserInfo user = jwtUtils.getUserFromJwtToken(jwt);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null,
                user == null ? List.of() : user.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    private boolean checkJwtToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(HEADER);
        if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX)) {
            return false;
        }
        return true;
    }
}
