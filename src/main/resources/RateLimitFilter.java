package org.acme.filter;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.acme.service.RateLimitingService;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;

/**
 * Filter responsible for enforcing Rate Limiting (Limite de Taxa) on endpoints
 * annotated with @RateLimited. It uses the API Key for user identification.
 */
@Provider
@RateLimited
@Priority(50)
public class RateLimitingFilter implements ContainerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String RATE_LIMIT_HEADER = "X-RateLimit-Limit";
    private static final String RATE_REMAINING_HEADER = "X-RateLimit-Remaining";

    @Inject
    RateLimitingService rateLimitingService;

    // Injetando as configurações diretamente para uso nos headers de resposta
    @Inject
    @ConfigProperty(name = "api.ratelimit.limit", defaultValue = "5")
    int requestLimit;

    @Inject
    @ConfigProperty(name = "api.ratelimit.window.seconds", defaultValue = "10")
    long windowSeconds;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // A API Key é o identificador do cliente que o RateLimitingService usa.
        String apiKey = requestContext.getHeaderString(API_KEY_HEADER);

        // Se a chave não for fornecida (deve ter sido tratada pelo ApiKeyFilter com status 401),
        // usamos um identificador genérico.
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = "anonymous_client";
        }

        // 1. Tenta permitir a requisição
        if (!rateLimitingService.allowRequest(apiKey)) {
            // 2. Limite excedido: Aborta com status 429
            requestContext.abortWith(
                    Response.status(429) // HTTP 429 Too Many Requests
                            .header("Retry-After", windowSeconds)
                            .entity("Limite de requisições excedido. Tente novamente em " + windowSeconds + " segundos.")
                            .build()
            );
        }

        // 3. Adiciona cabeçalhos informativos (mesmo que a requisição seja permitida)
        int remaining = rateLimitingService.remainingRequests(apiKey);

        // CORRIGIDO: Headers devem ser String, mas o valor é um Integer/Long. Convertemos para String.
        requestContext.getHeaders().add(RATE_LIMIT_HEADER, String.valueOf(requestLimit));
        requestContext.getHeaders().add(RATE_REMAINING_HEADER, String.valueOf(remaining));
    }
}