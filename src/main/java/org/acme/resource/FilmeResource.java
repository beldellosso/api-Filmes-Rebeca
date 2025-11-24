package org.acme.resource; // Pacote corrigido para minúsculo

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.validation.Valid;
import org.acme.Filme; // Importa o modelo de dados correto (assumindo org.acme.model)
import org.acme.filter.ApiKeyProtected;
import org.acme.filter.Idempotent;
import org.acme.service.FilmeService;
import org.acme.service.IdempotencyService;

import java.net.URI;
import java.util.List;

/**
 * Recurso JAX-RS (REST) para a entidade Filme.
 *
 * Aplica filtros de segurança:
 * - @RateLimited: Aplica limite de requisições.
 * - @ApiKeyProtected: Requer uma X-API-Key válida.
 */
@Path("/filmes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApiKeyProtected
public class FilmeResource {

    @Inject
    FilmeService service;

    @Inject
    IdempotencyService idempotency;

    /**
     * Lista todos os filmes.
     * GET /filmes
     */
    @GET
    public List<Filme> listar() {
        return service.listar();
    }

    /**
     * Busca filmes dinamicamente por parâmetros de consulta.
     * GET /filmes/buscar?titulo=...&genero=...&ano=...
     */
    @GET
    @Path("/buscar")
    public List<Filme> buscar(
            @QueryParam("titulo") String titulo,
            @QueryParam("genero") Filme.Genero genero,
            @QueryParam("ano") Integer ano
    ) {
        return service.buscar(titulo, genero, ano);
    }

    /**
     * Busca um filme por ID.
     * GET /filmes/{id}
     */
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Filme filme = service.buscarPorId(id);
        if (filme != null) {
            return Response.ok(filme).build(); // 200 OK
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
        }
    }

    /**
     * Cria um novo filme, implementando a lógica de idempotência.
     * POST /filmes
     * Requer o header "Idempotency-Key".
     */
    @POST
    @Idempotent // Marca para o filtro garantir o header, e para a lógica de cache aqui.
    public Response criar(@HeaderParam("Idempotency-Key") String key, @Valid Filme f) {
        Response resposta;

        // 1. Checa o cache de idempotência
        Response savedResponse = idempotency.load(key);
        if (savedResponse != null) {
            // Se existir, retorna a resposta cacheada (status 201 ou 500 anterior)
            return savedResponse;
        }

        try {
            // 2. Processa a requisição (se não estiver em cache)
            Filme novo = service.criar(f);

            // Cria a resposta de SUCESSO (201 Created)
            resposta = Response
                    .created(URI.create("/filmes/" + novo.id))
                    .entity(novo)
                    .build();

            // 3. Salva a resposta de SUCESSO no cache
            idempotency.save(key, resposta);

        } catch (Exception e) {
            // 4. Em caso de FALHA (Ex: erro de validação/banco), remove a chave para permitir nova tentativa
            if (key != null && !key.isBlank()) {
                idempotency.remove(key);
            }
            // Lança exceção para ser mapeada para 500 pela GlobalExceptionHandler
            throw new WebApplicationException("Erro ao criar filme: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }

        return resposta;
    }

    /**
     * Atualiza um filme existente por ID.
     * PUT /filmes/{id}
     */
    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, @Valid Filme f) {
        Filme filmeAtualizado = service.atualizar(id, f);
        if (filmeAtualizado != null) {
            return Response.ok(filmeAtualizado).build(); // 200 OK
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
        }
    }

    /**
     * Deleta um filme por ID.
     * DELETE /filmes/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") Long id) {
        if (service.deletar(id)) {
            return Response.status(Response.Status.NO_CONTENT).build(); // 204 No Content
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
        }
    }
}