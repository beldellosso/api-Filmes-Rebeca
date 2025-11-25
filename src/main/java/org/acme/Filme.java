package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Entidade Filme com validação completa via Bean Validation.
 * Inclui enum de gênero e relacionamentos com Diretor e Ator.
 */
@Entity
public class Filme extends PanacheEntity {

    @NotBlank(message = "O título é obrigatório.")
    public String titulo;

    @NotNull(message = "O ano de lançamento é obrigatório.")
    @Min(value = 1888, message = "O ano deve ser posterior a 1888.")
    @Max(value = 2100, message = "O ano de lançamento é irreal.")
    public Integer anoLancamento;

    @NotNull(message = "O gênero é obrigatório.")
    @Enumerated(EnumType.STRING)
    public Genero genero;

    @ManyToOne
    @NotNull(message = "O diretor é obrigatório.")
    public Diretor diretor;

    @ManyToMany
    public List<Ator> elenco;


    public enum Genero {
        ACAO,
        COMEDIA,
        DRAMA,
        FICCAO_CIENTIFICA,
        TERROR,
        ROMANCE,
        DOCUMENTARIO,
        ANIMACAO,
        FANTASIA
    }
}
