package org.acme.v2;

import org.acme.Filme;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/v2/filmes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FilmeResourceV2 {

    @GET
    public List<Filme> listar() {
        return Filme.listAll();
    }

    @POST
    @Transactional
    public Filme criar(Filme filme) {
        filme.persist();
        return filme;
    }
}
