package org.acme.resource;

import jakarta.transaction.Transactional;
import org.acme.filter.Idempotent;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.Ator;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.util.List;

@Path("/atores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AtorResource {

    @GET
    public List<Ator> listar() {
        return Ator.listAll();
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
    public Ator criar(Ator ator) {
        ator.persist();
        return ator;
    }
}
