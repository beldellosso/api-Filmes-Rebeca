package org.acme;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

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
    public Filme buscarPorId(@PathParam("id") Long id) {
        return Filme.findById(id);
    }

    @GET
    @Path("/buscar")
    public List<Filme> buscar(@QueryParam("titulo") String titulo,
                              @QueryParam("genero") String genero,
                              @QueryParam("ano") Integer ano) {
        return Filme.list("titulo like ?1 or genero = ?2 or anoLancamento = ?3",
                "%" + titulo + "%", genero, ano);
    }

    @POST
    @Transactional
    public Filme adicionar(Filme filme) {
        filme.persist();
        return filme;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Filme atualizar(@PathParam("id") Long id, Filme dados) {
        Filme filme = Filme.findById(id);
        if (filme == null) {
            throw new NotFoundException("Filme não encontrado");
        }
        filme.titulo = dados.titulo;
        filme.diretor = dados.diretor;
        filme.genero = dados.genero;
        filme.anoLancamento = dados.anoLancamento;
        filme.disponivel = dados.disponivel;
        return filme;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void deletar(@PathParam("id") Long id) {
        Filme.deleteById(id);
    }
}
