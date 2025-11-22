package org.acme.filter;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter; // <-- ESTE É O IMPORT CHAVE
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.Priority;
import org.acme.service.IdempotencyService;

/**
 * Filtro que garante que o header 'Idempotency-Key' esteja presente em requisições POST
 * nos endpoints marcados com @Idempotent.
 * NOTA: A checagem do cache é feita no FilmeResource.
 */
@Provider
@Idempotent
@Priority(50)
public class Idempotencyfilter implements ContainerRequestFilter {

    @Inject
    IdempotencyService idempotencyService;

    @Override
    public void filter(ContainerRequestContext ctx) {

        // Aplica SOMENTE para POST
        if (!ctx.getMethod().equalsIgnoreCase("POST")) {
            return;
        }

        String key = ctx.getHeaderString("Idempotency-Key");

        // 1. Checa se a chave está presente
        if (key == null || key.isBlank()) {
            ctx.abortWith(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Header 'Idempotency-Key' é obrigatório para esta operação.")
                    .build());
            return;
        }

        // Não precisa checar se a chave existe no cache aqui, pois o Resource já faz.
        // O filtro apenas garante que a chave foi fornecida.
    }
}