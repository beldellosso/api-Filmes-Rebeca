package org.acme;

import org.acme.Filme;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;

@Path("/filmes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FilmeResource {

    @GET
    public List<Filme> listarTodos() {
        return Filme.listAll();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Filme filme = Filme.findById(id);
        if (filme != null) {
            return Response.ok(filme).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/buscar")
    public List<Filme> buscar(
            @QueryParam("titulo") String titulo,
            @QueryParam("genero") Filme.Genero genero,
            @QueryParam("ano") Integer ano) {

        if (titulo != null) {
            return Filme.list("titulo like ?1", "%" + titulo + "%");
        }
        if (genero != null) {
            return Filme.list("genero", genero);
        }
        if (ano != null) {
            return Filme.list("anoLancamento", ano);
        }

        return Filme.listAll();
    }

    @POST
    @Transactional
    public Response adicionar(@Valid Filme filme) {
        filme.persist();
        return Response.created(URI.create("/filmes/" + filme.id)).entity(filme).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Long id, @Valid Filme dados) {
        Filme filme = Filme.findById(id);
        if (filme != null) {
            filme.titulo = dados.titulo;
            filme.diretor = dados.diretor;
            filme.atores = dados.atores;
            filme.genero = dados.genero;
            filme.anoLancamento = dados.anoLancamento;
            filme.sinopse = dados.sinopse;
            return Response.ok(filme).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Long id) {
        if (Filme.deleteById(id)) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}