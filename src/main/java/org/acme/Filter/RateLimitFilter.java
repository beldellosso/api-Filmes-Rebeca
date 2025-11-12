package org.acme.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class RateLimitFilter implements ContainerRequestFilter {

    private static final int LIMIT = 10; // Ex: 10 requests/minuto
    private static final Map<String, Counter> clientRequests = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String clientIp = requestContext.getHeaderString("x-api-key");
        if (clientIp == null) clientIp = requestContext.getHeaders().getFirst("X-Forwarded-For");
        if (clientIp == null) clientIp = "unknown";

        Counter counter = clientRequests.computeIfAbsent(clientIp, k -> new Counter());
        if (System.currentTimeMillis() - counter.timestamp > 60000) {
            counter.count = 0;
            counter.timestamp = System.currentTimeMillis();
        }

        if (++counter.count > LIMIT) {
            requestContext.abortWith(Response.status(429)
                    .entity("{\"error\": \"Too many requests\"}").build());
        }
    }

    static class Counter {
        int count = 0;
        long timestamp = System.currentTimeMillis();
    }
}
