package org.acme.v2;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/v2/filmes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FilmeResourceV2 {

    @GET
    public List<String> listar() {
        // Apenas exemplo de formato diferente
        return List.of("Versão 2 - Listagem simplificada de filmes");
    }
}
