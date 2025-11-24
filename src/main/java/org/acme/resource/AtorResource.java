package org.acme.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.Ator;

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
    public Ator criar(Ator ator) {
        ator.persist();
        return ator;
    }
}
