package org.acme.resource;

import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.transaction.Transactional;
import org.acme.filter.Idempotent;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.Diretor;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Path("/v1/diretores") // Atualizado para V1
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DiretorResource {

    @GET
    @RateLimit(value = 5, window=60, windowUnit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "fallbackListaDiretor")
    @Parameter(
            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"
    )
    public Response listar() {
        return Response.ok(Diretor.listAll()).build();
    }

    @GET
    @Path("/{id}")
    @Parameter(
            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"
    )
    public Response buscarPorId(@PathParam("id") Long id) {
        Diretor diretor = Diretor.findById(id);
        return diretor != null ? Response.ok(diretor).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Idempotent
    @Transactional
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
    public Diretor criar(Diretor d) {
        d.persist();
        return d;
    }

    @PUT
    @Idempotent
    @Path("/{id}")
    @Transactional
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
    public Response atualizar(@PathParam("id") Long id, Diretor d) {
        Diretor diretorAtualizado = Diretor.findById(id);
        if (diretorAtualizado != null) {
            diretorAtualizado.nome = d.nome;
            return Response.ok(diretorAtualizado).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Idempotent // ADICIONADO: Idempotência para DELETE
    @Path("/{id}")
    @Transactional
    @Parameter(
            name = "X-Idempotency-Key", // ADICIONADO
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
        boolean deletado = Diretor.deleteById(id);
        return deletado ? Response.status(Response.Status.NO_CONTENT).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    public Response fallbackListaDiretor() {
        return Response.status(Response.Status.TOO_MANY_REQUESTS)
                .entity(Map.of("erro", "Taxa de requisições excedida. Tente novamente mais tarde."))
                .build();
    }
}