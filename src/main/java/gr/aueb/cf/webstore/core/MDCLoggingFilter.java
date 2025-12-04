package gr.aueb.cf.webstore.core;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


@Component
public class MDCLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException,
            IOException {

        try {
            String user = "anonymous";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) user = authentication.getName();

            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp != null && !clientIp.isEmpty()) {
                clientIp = clientIp.split(",")[0].trim();
            } else {
                clientIp = request.getRemoteAddr();
            }

            if ("0:0:0:0:0:0:0:1".equals(clientIp)) clientIp = "127.0.0.1";

            MDC.put("user", user);
            MDC.put("ip", clientIp);

            filterChain.doFilter(request, response);

        } finally {

            MDC.clear();
        }
    }
}
