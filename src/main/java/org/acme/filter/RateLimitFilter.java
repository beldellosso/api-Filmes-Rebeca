package org.acme.filter;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.Priority;
import org.acme.service.RateLimitService;
import org.acme.service.RateLimitService.RateLimitResponse;
// Não precisamos mais desta importação aqui, pois a variável foi removida.
// import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Filtro que aplica a lógica de Rate Limiting (Limite de Requisições)
 * para endpoints marcados com a anotação @RateLimited.
 *
 * Utiliza o RateLimitService (Sliding Window Log) para checar e atualizar
 * os contadores, e define os headers de resposta padrão de Rate Limiting.
 */
@Provider
@RateLimited
@Priority(2000) // Prioridade alta para garantir que roda DEPOIS do filtro de autenticação.
public class RateLimitFilter implements ContainerRequestFilter {

    // O RateLimitService com a lógica Sliding Window Log
    @Inject
    RateLimitService rateLimitService;

    // REMOVIDO: A injeção de @ConfigProperty duplicada que estava causando o erro.
    // Agora o valor é obtido via rateLimitService.getMaxRequests().

    @Override
    public void filter(ContainerRequestContext ctx) {

        // Simula a obtenção do identificador do cliente.
        // O X-API-Key é preferido, mas se não existir, usa um IP simulado ou padrão.
        String apiKey = ctx.getHeaderString("X-API-Key");
        if (apiKey == null || apiKey.isBlank()) {
            // Se a chave não existir, usa um IP simulado ou um valor padrão.
            apiKey = ctx.getHeaderString("X-Simulated-Client-IP");
            if (apiKey == null || apiKey.isBlank()) {
                apiKey = "default-unauthenticated-client";
            }
        }

        // 1. Chama o serviço para verificar o limite
        RateLimitResponse response = rateLimitService.allowRequest(apiKey);

        // O valor do limite é obtido do serviço, que é a fonte da verdade.
        int maxRequests = rateLimitService.getMaxRequests();

        // 2. Define os headers padrão (mesmo que a requisição seja permitida)
        ctx.getHeaders().add("X-RateLimit-Limit", String.valueOf(maxRequests));
        ctx.getHeaders().add("X-RateLimit-Remaining", String.valueOf(response.getRemaining()));

        // 3. Verifica se a requisição é permitida
        if (!response.isAllowed()) {

            // 4. Se for bloqueada (Rate Limit Excedido), define o Retry-After e aborta
            ctx.getHeaders().add("Retry-After", String.valueOf(response.getWaitTimeSeconds()));

            ctx.abortWith(Response.status(Response.Status.TOO_MANY_REQUESTS) // 429
                    .entity("Limite de requisições excedido. Tente novamente após " +
                            response.getWaitTimeSeconds() + " segundos.")
                    .build());
        }

        // Se permitido, a execução continua para o próximo filtro ou o Resource.
    }
}