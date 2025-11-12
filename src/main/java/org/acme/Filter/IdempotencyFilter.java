package org.acme.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class IdempotencyFilter implements ContainerRequestFilter {

    private static final ConcurrentHashMap<String, Boolean> processedKeys = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if ("POST".equalsIgnoreCase(requestContext.getMethod())) {
            String key = requestContext.getHeaderString("Idempotency-Key");
            if (key == null || key.isBlank()) {
                requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Missing Idempotency-Key header\"}").build());
                return;
            }
            if (processedKeys.putIfAbsent(key, true) != null) {
                requestContext.abortWith(Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Duplicate Idempotency-Key\"}").build());
            }
        }
    }
}
