package org.acme.filter;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import org.acme.service.ApiKeyService;

@Provider
@ApiKeyProtected
public class ApiKeyFilter implements ContainerRequestFilter {

    @Inject
    ApiKeyService apiKeyService;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String apiKey = ctx.getHeaderString("X-API-Key");

        if (apiKey == null || !apiKeyService.isValidKey(apiKey)) {
            ctx.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("API Key inválida ou ausente")
                            .build()
            );
        }
    }
}
