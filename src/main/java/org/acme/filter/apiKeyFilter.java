package org.acme.filter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
@ApplicationScoped
public class apiKeyFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "quarkus.api-key.value")
    String apiKey;

    @ConfigProperty(name = "quarkus.api-key.header-name", defaultValue = "X-API-Key")
    String apiKeyHeader;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (isPublicRoute(requestContext.getUriInfo().getPath())) {
            return;
        }

        String providedKey = requestContext.getHeaderString(apiKeyHeader);

        if (providedKey == null || !providedKey.equals(apiKey)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("API key inválida ou ausente")
                            .build());
        }
    }

    private boolean isPublicRoute(String path) {
        // CORREÇÃO: Incluindo rotas do Swagger/OpenAPI para que a documentação funcione
        boolean basePublic = path.contains("/public/")
                || path.startsWith("/health")
                || path.startsWith("/metrics");

        boolean swaggerPublic = path.startsWith("openapi")
                || path.startsWith("q/openapi")
                || path.startsWith("swagger-ui")
                || path.startsWith("/swagger-ui");

        return basePublic || swaggerPublic;
    }
}