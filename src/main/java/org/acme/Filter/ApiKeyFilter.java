package org.acme.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class ApiKeyFilter implements ContainerRequestFilter {

    private static final String API_KEY = "12345-SECRET"; // pode mover para application.properties

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
            String key = requestContext.getHeaderString("x-api-key");
            if (key == null || !key.equals(API_KEY)) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Invalid or missing API Key\"}").build());
            }
        }
    }
}
