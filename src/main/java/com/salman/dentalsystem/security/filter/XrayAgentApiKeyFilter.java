package com.salman.dentalsystem.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class XrayAgentApiKeyFilter extends OncePerRequestFilter {

    @Value("${spring.xray.agent.api-key}")
    private String expectedApiKey;

    @Override
    @NullMarked
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/v1/xray/upload")) {
            String providedKey = request.getHeader("Xray-Agent-Api-Key");
            if (providedKey == null || !providedKey.equals(expectedApiKey)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Invalid or missing api key\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
