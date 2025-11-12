package org.acme;

import org.acme.Diretor;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/diretores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DiretorResource {

    @GET
    public List<Diretor> listar() {
        return Diretor.listAll();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Diretor diretor = Diretor.findById(id);
        if (diretor == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(diretor).build();
    }

    @POST
    @Transactional
    public Response criar(@Valid Diretor diretor) {
        diretor.persist();
        return Response.status(Response.Status.CREATED).entity(diretor).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Long id, @Valid Diretor dados) {
        Diretor diretor = Diretor.findById(id);
        if (diretor == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        diretor.nome = dados.nome;
        diretor.persist();
        return Response.ok(diretor).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Long id) {
        boolean deletado = Diretor.deleteById(id);
        if (!deletado)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    }

    @GET
    @Path("/search")
    public List<Diretor> buscarPorNome(@QueryParam("nome") String nome) {
        if (nome == null || nome.isBlank()) {
            return Diretor.listAll();
        }
        return Diretor.list("LOWER(nome) LIKE LOWER(?1)", "%" + nome + "%");
    }
}
