package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
public class Filme extends PanacheEntity {

    @NotBlank(message = "O título do filme é obrigatório.")
    @Size(min = 2, max = 100, message = "O título deve ter entre 2 e 100 caracteres.")
    public String titulo;

    @NotNull(message = "O ano de lançamento é obrigatório.")
    @Min(value = 1888, message = "O ano de lançamento deve ser maior que 1888.")
    @Max(value = 2100, message = "O ano de lançamento não pode ser maior que 2100.")
    public Integer anoLancamento;

    @NotBlank(message = "A sinopse é obrigatória.")
    public String sinopse;

    // Relacionamento Muitos para Um: muitos filmes podem ter o mesmo diretor
    @ManyToOne
    @JoinColumn(name = "diretor_id")
    public Diretor diretor;

    // Relacionamento Muitos para Muitos: um filme pode ter muitos atores
    @ManyToMany
    @JoinTable(
            name = "filme_ator",
            joinColumns = @JoinColumn(name = "filme_id"),
            inverseJoinColumns = @JoinColumn(name = "ator_id")
    )
    public List<Ator> atores;

    @Enumerated(EnumType.STRING)
    public Genero genero;

    public enum Genero {
        ACAO,
        COMEDIA,
        DRAMA,
        FICCAO_CIENTIFICA,
        TERROR,
        ROMANCE
    }
}
