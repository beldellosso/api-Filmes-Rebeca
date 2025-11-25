package org.acme.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.validation.Valid;
import org.acme.Filme; // Sua entidade Filme
import org.acme.filter.ApiKeyProtected;
import org.acme.filter.Idempotent;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.net.URI;
import java.util.List;

@Path("/filmes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApiKeyProtected
public class FilmeResource {

    @GET
    public Response listar() {
        // Usa o método estático Panache para listar todos
        return Response.ok(Filme.listAll()).build();
    }

    @GET
    @Path("/buscar")
    public Response buscar(
            @QueryParam("titulo") String titulo,
            @QueryParam("genero") Filme.Genero genero,
            @QueryParam("ano") Integer anoLancamento // O nome do parâmetro deve ser consistente com o campo da entidade
    ) {
        // CORREÇÃO: Usando o método find() do Panache para uma busca flexível
        // Esta é uma busca simples que pode ser complexa dependendo do seu requisito.
        // A busca é feita por uma combinação de campos
        List<Filme> filmes = Filme
                .find("titulo = ?1 or genero = ?2 or anoLancamento = ?3", titulo, genero, anoLancamento)
                .list();

        return Response.ok(filmes).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        // CORREÇÃO: Usando findById() do Panache e checando a existência
        Filme filme = Filme.findById(id);

        if (filme != null) {
            return Response.ok(filme).build(); // 200 OK
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
        }
    }

    @POST
    @Idempotent // Mantendo a anotação para o filtro
    @Parameter(
            name = "X-API-Key",
            in = ParameterIn.HEADER,
            description = "Chave da API para autenticação"
    )
    public Response criar(@Valid Filme f) {
        // CORREÇÃO: Chamando o método persist() diretamente na entidade Panache
        f.persist(); // Salva a nova entidade no banco de dados

        // Construção da URI (para o cabeçalho Location) usando o ID gerado
        URI location = UriBuilder.fromResource(FilmeResource.class)
                .path("/{id}")
                .resolveTemplate("id", f.id)
                .build();

        return Response
                .created(location)
                .entity(f)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Idempotent
    public Response atualizar(@PathParam("id") Long id, @Valid Filme f) {
        // CORREÇÃO: Buscando a entidade existente para atualização
        Filme filmeAtualizado = Filme.findById(id);

        if (filmeAtualizado != null) {
            // Atualiza os campos do objeto gerenciado com os dados do request (f)
            filmeAtualizado.titulo = f.titulo;
            filmeAtualizado.genero = f.genero;
            filmeAtualizado.anoLancamento = f.anoLancamento; // Campo corrigido
            filmeAtualizado.diretor = f.diretor;
            filmeAtualizado.elenco = f.elenco;

            // Em uma transação, as mudanças são salvas automaticamente (Panache)

            return Response.ok(filmeAtualizado).build(); // 200 OK
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
        }
    }

    @DELETE
    @Path("/{id}")
    @Idempotent
    public Response deletar(@PathParam("id") Long id) {
        // CORREÇÃO: Usando deleteById() do Panache
        boolean deletado = Filme.deleteById(id);

        if (deletado) {
            return Response.status(Response.Status.NO_CONTENT).build(); // 204 No Content
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
        }
    }
}