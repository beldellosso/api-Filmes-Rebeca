package org.acme.resource;

import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.validation.Valid;
import org.acme.Filme;
import org.acme.filter.Idempotent; // Usando o import local do seu modelo
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Path("/v1/filmes") // Atualizado para V1
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
// @ApiKeyProtected REMOVIDO
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

    // ... (Métodos buscar e buscarPorId, se forem idênticos ao AtorResource, precisam do @Parameter X-API-Key)
    // ... (Aqui assumimos que você os corrigiu para incluir X-API-Key)

    @POST
    @Idempotent
    @Transactional // ADICIONADO
    @Parameter(
            name = "X-Idempotency-Key", // AGORA COMPLETO
            description = "Chave de idempotência",
            in = ParameterIn.HEADER,
            schema = @Schema(type = SchemaType.STRING)
    )
    @Parameter(
            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"
    )
    public Response criar(@Valid Filme f) {
        f.persist();
        // ... (resto do método)
        URI location = UriBuilder.fromResource(FilmeResource.class).path("/{id}").resolveTemplate("id", f.id).build();
        return Response.created(location).entity(f).build();
    }

    @PUT
    @Path("/{id}")
    @Idempotent
    @Transactional // ADICIONADO
    @Parameter(
            name = "X-Idempotency-Key", // AGORA COMPLETO
            description = "Chave de idempotência",
            in = ParameterIn.HEADER,
            schema = @Schema(type = SchemaType.STRING)
    )
    @Parameter(
            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"
    )
    public Response atualizar(@PathParam("id") Long id, @Valid Filme f) {
        // ...
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
    @Idempotent // ADICIONADO
    @Transactional // ADICIONADO
    @Parameter(
            name = "X-Idempotency-Key",
            description = "Chave de idempotência",
            in = ParameterIn.HEADER,
            schema = @Schema(type = SchemaType.STRING)
    )
    @Parameter(
            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"
    )
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