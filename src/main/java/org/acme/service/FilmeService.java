package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional; // Importação chave para métodos de escrita
import org.acme.Filme;
import org.acme.Filme.Genero; // Importação explícita do Genero
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço responsável por encapsular a lógica de persistência e busca para a entidade Filme.
 * Utiliza o Panache (Quarkus) para simplificar as operações de banco de dados.
 */
@ApplicationScoped
public class FilmeService {

    public List<Filme> listar() {
        return Filme.listAll();
    }

    public Filme buscarPorId(Long id) {
        return Filme.findById(id);
    }

    @Transactional // Garante que a operação de escrita rode em um contexto transacional
    public Filme criar(Filme filme) {
        filme.persist();
        return filme;
    }

    @Transactional // Garante que a operação de escrita rode em um contexto transacional
    public Filme atualizar(Long id, Filme dados) {
        Filme entity = Filme.findById(id);
        if (entity == null) {
            return null;
        }

        entity.titulo = dados.titulo;
        entity.genero = dados.genero;
        entity.anoLancamento = dados.anoLancamento;
        entity.diretor = dados.diretor;
        entity.elenco = dados.elenco;

        // Panache se encarrega de persistir as mudanças no fim da transação
        return entity;
    }

    @Transactional // Garante que a operação de escrita rode em um contexto transacional
    public boolean deletar(Long id) {
        return Filme.deleteById(id);
    }

    /**
     * Implementação do método de busca dinâmica usando Panache.find().
     * Filtra a lista de filmes com base nos parâmetros não nulos.
     * * @param titulo Filtra por parte do título (case-insensitive)
     * @param genero Filtra pelo gênero do filme (exato)
     * @param anoLancamento Filtra pelo ano de lançamento (exato)
     * @return Lista de filmes que correspondem aos critérios
     */
    public List<Filme> buscar(String titulo, Genero genero, Integer anoLancamento) {

        StringBuilder query = new StringBuilder("1=1"); // Começa com uma condição sempre verdadeira
        Map<String, Object> params = new HashMap<>();

        if (titulo != null && !titulo.isBlank()) {
            // Usa LIKE para busca parcial e UPPER para case-insensitive no título
            query.append(" AND UPPER(titulo) LIKE CONCAT('%', UPPER(:titulo), '%')");
            params.put("titulo", titulo);
        }
        if (genero != null) {
            query.append(" AND genero = :genero");
            // Panache lida perfeitamente com a tipagem de ENUM
            params.put("genero", genero);
        }
        if (anoLancamento != null) {
            query.append(" AND anoLancamento = :anoLancamento");
            params.put("anoLancamento", anoLancamento);
        }

        // Executa a query usando Panache.find()
        return Filme.find(query.toString(), params).list();
    }
}