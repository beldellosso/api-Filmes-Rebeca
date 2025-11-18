package org.acme.resource; // Pacote corrigido para minúsculo

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.validation.Valid;
import org.acme.Filme; // Importa o modelo de dados correto (assumindo org.acme.model)
import org.acme.filter.ApiKeyProtected;
import org.acme.filter.Idempotent;
import org.acme.filter.RateLimited;
import org.acme.service.FilmeService;
import org.acme.service.IdempotencyService;

import java.net.URI;
import java.util.List;

@Path("/filmes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RateLimited
@ApiKeyProtected
public class FilmeResource {

    @Inject
    FilmeService service;

    @Inject
    IdempotencyService idempotency;

    @GET
    public List<Filme> listar() {
        return service.listar();
    }

    // Método para busca dinâmica por parâmetros (QueryParam)
    @GET
    @Path("/buscar")
    public List<Filme> buscar(
            @QueryParam("titulo") String titulo,
            @QueryParam("genero") Filme.Genero genero,
            @QueryParam("ano") Integer ano) {

        return service.buscarCustom(titulo, genero, ano);
    }

    @GET
    @Path("/{id}")
    public Response buscar(@PathParam("id") Long id) {
        Filme filme = service.buscar(id);
        if (filme != null) {
            return Response.ok(filme).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
        }
    }

    @POST
    @Idempotent // A anotação garante a checagem do cabeçalho no filtro
    public Response criar(@Valid Filme f, @HeaderParam("Idempotency-Key") String key) {

        // 1. VERIFICAÇÃO DE IDEMPOTÊNCIA: Tenta carregar a resposta salva
        Response cachedResponse = idempotency.load(key);
        if (cachedResponse != null) {
            // Se existir, retorna a resposta salva (201 Created)
            return Response.fromResponse(cachedResponse).build();
        }

        Filme novo;
        Response resposta;

        try {
            // 2. Execução da lógica: Cria o filme
            novo = service.criar(f);

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
            // Lança exceção para ser mapeada para 500
            throw new WebApplicationException("Erro ao criar filme: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }

        return resposta;
    }

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