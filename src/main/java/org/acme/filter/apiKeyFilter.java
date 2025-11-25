package org.acme.filter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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

    @ConfigProperty(name = "quarkus.api-key.header-name", defaultValue
            = "X-API-Key")
    String apiKeyHeader;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Verificar se é uma rota pública (opcional)
        if (isPublicRoute(requestContext.getUriInfo().getPath())) {
            return;
        }

        // Obter o valor do cabeçalho da API key
        String providedKey =
                requestContext.getHeaderString(apiKeyHeader);

        // Verificar se a API key é válida
        if (providedKey == null || !providedKey.equals(apiKey)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("API key inválida ou ausente")
                            .build());
        }
    }

    private boolean isPublicRoute(String path) {
        // Defina aqui suas rotas públicas que não requerem autentica
        // ção
        return path.contains("/public/") || path.startsWith("/health")
                || path.startsWith("/metrics");
    }
}