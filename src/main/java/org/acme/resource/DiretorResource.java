package org.acme.resource;

import org.acme.filter.Idempotent;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.Diretor;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.util.List;

@Path("/diretores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DiretorResource {

    @GET
    public List<Diretor> listar() {
        return Diretor.listAll();
    }

    @POST
    @Idempotent
    @Parameter(
            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"
    )
    public Diretor criar(Diretor d) {
        d.persist();
        return d;
    }
}