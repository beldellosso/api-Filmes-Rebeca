package org.acme.resource;

import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.Ator;
import org.acme.filter.Idempotent;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;


@Path("/v1/atores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class AtorResource {

    @GET
    @RateLimit(value = 5, window=60, windowUnit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "fallbackListaAtor")
    @Parameter(

            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"

    )

    public Response listar() {
        return Response.ok(Ator.listAll()).build();
    }




    @GET
    @Path("/{id}")
    @Parameter(
            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"

    )

    public Response buscarPorId(@PathParam("id") Long id) {
        Ator ator = Ator.findById(id);
        if (ator != null) {
            return Response.ok(ator).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }



    @POST
    @Transactional
    @Idempotent
    @Parameter(
            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"
    )

    @Parameter(
            name = "X-Idempotency-Key",
            description = "Chave de idempotência",
            in = ParameterIn.HEADER,
            schema = @Schema(type = SchemaType.STRING)
    )

    public Ator criar(Ator ator) {
        ator.persist();
        return ator;
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

    public Response atualizar(@PathParam("id") Long id, Ator a) {
        Ator atorAtualizado = Ator.findById(id);
        if (atorAtualizado != null) {
            atorAtualizado.nome = a.nome;
            return Response.ok(atorAtualizado).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


    @DELETE
    @Path("/{id}")
    @Transactional
    @Parameter(
            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"
    )

    public Response deletar(@PathParam("id") Long id) {
        boolean deletado = Ator.deleteById(id);
        if (deletado) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    public Response fallbackListaAtor() {
        return Response.status(Response.Status.TOO_MANY_REQUESTS)
                .entity(Map.of("erro", "Taxa de requisições excedida. Tente novamente mais tarde."))
                .build();

    }

}