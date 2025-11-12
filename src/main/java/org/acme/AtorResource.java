package org.acme;

import org.acme.Ator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/atores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AtorResource {

    @GET
    public List<Ator> listar() {
        return Ator.listAll();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Ator ator = Ator.findById(id);
        if (ator == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(ator).build();
    }

    @POST
    @Transactional
    public Response criar(@Valid Ator ator) {
        ator.persist();
        return Response.status(Response.Status.CREATED).entity(ator).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Long id, @Valid Ator dados) {
        Ator ator = Ator.findById(id);
        if (ator == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        ator.nome = dados.nome;
        ator.persist();
        return Response.ok(ator).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Long id) {
        boolean deletado = Ator.deleteById(id);
        if (!deletado)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    }

    @GET
    @Path("/search")
    public List<Ator> buscarPorNome(@QueryParam("nome") String nome) {
        if (nome == null || nome.isBlank()) {
            return Ator.listAll();
        }
        return Ator.list("LOWER(nome) LIKE LOWER(?1)", "%" + nome + "%");
    }
}
