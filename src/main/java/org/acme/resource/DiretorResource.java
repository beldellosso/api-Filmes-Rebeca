package org.acme.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.Diretor;

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
    public Diretor criar(Diretor d) {
        d.persist();
        return d;
    }
}