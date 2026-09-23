package com.festibuy.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.festibuy.dto.ApiResponse;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IpRateLimitingInterceptor implements HandlerInterceptor {

    @Value("${rate-limit.read.limit:60}")
    private int readLimit;

    @Value("${rate-limit.read.period-seconds:60}")
    private int readPeriodSeconds;

    @Value("${rate-limit.write.limit:15}")
    private int writeLimit;

    @Value("${rate-limit.write.period-seconds:60}")
    private int writePeriodSeconds;

    private RateLimiterRegistry readRegistry;
    private RateLimiterRegistry writeRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        RateLimiterConfig readConfig = RateLimiterConfig.custom()
                .limitForPeriod(readLimit)
                .limitRefreshPeriod(Duration.ofSeconds(readPeriodSeconds))
                .timeoutDuration(Duration.ZERO)
                .build();
        this.readRegistry = RateLimiterRegistry.of(readConfig);

        RateLimiterConfig writeConfig = RateLimiterConfig.custom()
                .limitForPeriod(writeLimit)
                .limitRefreshPeriod(Duration.ofSeconds(writePeriodSeconds))
                .timeoutDuration(Duration.ZERO)
                .build();
        this.writeRegistry = RateLimiterRegistry.of(writeConfig);
        
        log.info("Initialized IP Rate Limiting: Read = {} req / {}s, Write = {} req / {}s", 
                readLimit, readPeriodSeconds, writeLimit, writePeriodSeconds);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        String clientIp = getClientIp(request);
        boolean isWrite = "POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method);

        RateLimiter rateLimiter = isWrite
                ? writeRegistry.rateLimiter(clientIp)
                : readRegistry.rateLimiter(clientIp);

        if (!rateLimiter.acquirePermission()) {
            log.warn("Rate limit exceeded for IP: {} on [{}] {}", clientIp, method, request.getRequestURI());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ApiResponse<Void> apiResponse = ApiResponse.error("Too many requests from your IP. Rate limit exceeded. Please try again later.", HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            return false;
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // First IP in X-Forwarded-For is the real client IP
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }

        return request.getRemoteAddr();
    }
}
