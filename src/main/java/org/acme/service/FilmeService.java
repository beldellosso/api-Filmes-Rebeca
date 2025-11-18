package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.Filme; // Import corrigido para o pacote correto
import org.acme.Filme.Genero;
import java.util.List;

/**
 * Service class responsible for handling business logic related to Filme entity
 * using Panache internally.
 */
@ApplicationScoped
public class FilmeService {

    /**
     * Lists all films in the database.
     * @return A list of all Filme objects.
     */
    public List<Filme> listar() {
        return Filme.listAll();
    }

    /**
     * Retrieves a film by its ID.
     * @param id The ID of the film to retrieve.
     * @return The found Filme object, or null if not found.
     */
    public Filme buscar(Long id) {
        return Filme.findById(id);
    }

    /**
     * Executes the custom search logic based on title, genre, or year.
     * @param titulo Title filter (partial match).
     * @param genero Genre filter (exact match).
     * @param ano Year filter (exact match).
     * @return A list of matching Filme objects.
     */
    public List<Filme> buscarCustom(String titulo, Genero genero, Integer ano) {
        if (titulo != null && !titulo.isBlank()) {
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


    /**
     * Creates a new film in the database.
     * @param filme The Filme object to persist.
     * @return The persisted Filme object.
     */
    @Transactional // ESSENCIAL para persistir no banco de dados
    public Filme criar(Filme filme) {
        filme.persist();
        return filme;
    }

    /**
     * Updates an existing film.
     * @param id The ID of the film to update.
     * @param novosDados The Filme object containing the new data.
     * @return The updated Filme object, or null if not found.
     */
    @Transactional // ESSENCIAL para realizar o update
    public Filme atualizar(Long id, Filme novosDados) {
        Filme filme = Filme.findById(id);

        // CORRIGIDO: Garante que o filme existe antes de atualizar
        if (filme == null) {
            return null;
        }

        // Atualiza os campos do filme existente
        filme.titulo = novosDados.titulo;
        filme.anoLancamento = novosDados.anoLancamento;
        filme.sinopse = novosDados.sinopse;
        filme.genero = novosDados.genero;
        filme.diretor = novosDados.diretor;
        filme.atores = novosDados.atores;

        return filme; // Não precisa de 'persist()' explícito, pois é uma entidade gerenciada dentro da transação.
    }

    /**
     * Deletes a film by its ID.
     * @param id The ID of the film to delete.
     * @return true if deleted, false if not found. (Necessário para o Resource checar 404)
     */
    @Transactional // ESSENCIAL para realizar o delete
    public boolean deletar(Long id) {
        return Filme.deleteById(id);
    }
}
