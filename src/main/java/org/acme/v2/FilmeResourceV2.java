package org.acme.v2;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.Filme;

import java.util.List;


@Path("/v2/filmes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class FilmeResourceV2 {


    @GET
    public Response listarV2(
            @QueryParam("genero") Filme.Genero genero
    ) {
        if (genero == null ) {
            return Response.ok(Filme.listAll()).build();
        }
        List<Filme> filmesFiltrados = Filme.find("genero = ?1", genero).list();

        return Response.ok(filmesFiltrados).build();
    }

}