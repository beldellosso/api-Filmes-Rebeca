package org.acme.resource;

import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.acme.Filme;
import org.acme.filter.ApiKeyProtected;
import org.acme.filter.Idempotent;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Path("/filmes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApiKeyProtected
public class FilmeResource {

    @GET
    @Parameter(
            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"
    )
    @RateLimit(value = 5, window= 60, windowUnit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "fallbackListarFilme")
    public Response listar() {
        return Response.ok(Filme.listAll()).build();
    }

    @GET
    @Path("/buscar")
    public Response buscar(
            @QueryParam("titulo") String titulo,
            @QueryParam("genero") Filme.Genero genero,
            @QueryParam("ano")
            @Min(value = 1888, message = "O ano deve ser posterior a 1888.")
            @Max(value = 2100, message = "O ano de lançamento é irreal.")
            Integer anoLancamento
    ) {
        List<Filme> filmes = Filme
                .find("titulo = ?1 or genero = ?2 or anoLancamento = ?3",
                        titulo, genero, anoLancamento)
                .list();

        return Response.ok(filmes).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Filme filme = Filme.findById(id);
        if (filme != null) {
            return Response.ok(filme).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Idempotent
    @Parameter(
            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"
    )
    public Response criar(@Valid Filme f) {
        f.persist();

        URI location = UriBuilder.fromResource(FilmeResource.class)
                .path("/{id}")
                .resolveTemplate("id", f.id)
                .build();

        return Response
                .created(location)
                .entity(f)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Idempotent
    public Response atualizar(@PathParam("id") Long id, @Valid Filme f) {
        Filme filmeAtualizado = Filme.findById(id);

        if (filmeAtualizado != null) {
            filmeAtualizado.titulo = f.titulo;
            filmeAtualizado.genero = f.genero;
            filmeAtualizado.anoLancamento = f.anoLancamento;
            filmeAtualizado.diretor = f.diretor;
            filmeAtualizado.elenco = f.elenco;

            return Response.ok(filmeAtualizado).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Idempotent
    public Response deletar(@PathParam("id") Long id) {
        boolean deletado = Filme.deleteById(id);

        if (deletado) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    public Response fallbackListarFilme() {
        return Response.status(Response.Status.TOO_MANY_REQUESTS)
                .entity(Map.of("erro", "Taxa de requisições excedida. Tente novamente mais tarde."))
                .header("X-RateLimit-Limit", 5)
                .header("X-RateLimit-Remaining", 0)
                .build();
    }
}
