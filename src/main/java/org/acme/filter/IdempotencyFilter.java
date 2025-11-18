package org.acme.filter; // PACOTE A CORRIGIR

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import org.acme.service.IdempotencyService;
import jakarta.annotation.Priority; // Adicionado para prioridade

@Provider
@Idempotent
@Priority(50) // Prioridade deve ser definida para controlar a ordem
public class IdempotencyFilter implements ContainerRequestFilter {

    @Inject
    IdempotencyService idempotencyService;

    @Override
    public void filter(ContainerRequestContext ctx) {

        // Aplica SOMENTE para POST
        if (!ctx.getMethod().equalsIgnoreCase("POST"))
            return;

        String key = ctx.getHeaderString("Idempotency-Key");

        if (key == null || key.isBlank()) {
            ctx.abortWith(Response.status(400)
                    .entity("Header 'Idempotency-Key' é obrigatório.")
                    .build());
            return;
        }

        if (idempotencyService.exists(key)) {
            // PROBLEMA: O FILTRO NÃO DEVE ABORTAR AQUI.
            // O recurso é quem deve verificar a existência e retornar a resposta CACHEADA.
            // O recurso (FilmeResource) contém a lógica:
            // 1. Load key
            // 2. Process request
            // 3. Save key (se sucesso)
            // A lógica de idempotência não é completa em apenas um Request Filter.

            // No seu código atual, o recurso já faz a checagem:
            // FilmeResource:
            // Response savedResponse = idempotency.load(key);
            // if (savedResponse != null) { return savedResponse; }

            // Portanto, o filtro precisa apenas garantir que a chave exista no header,
            // mas não deve abortar aqui, ou a lógica de cache no Resource ficará redundante
            // e ineficaz, e a resposta que o filtro aborta não terá o corpo correto do recurso criado.
        }
    }
}