package org.acme.v2;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.acme.Ator;
import org.acme.Diretor;

import java.util.List;

@Entity
public class Filme extends PanacheEntity {

    @NotBlank(message = "O título do filme é obrigatório.")
    @Size(min = 2, max = 100, message = "O título deve ter entre 2 e 100 caracteres.")
    public String titulo;

    @NotNull(message = "O ano de lançamento é obrigatório.")
    @Min(1888)
    @Max(2100)
    public Integer anoLancamento;

    @NotBlank
    public String sinopse;

    @ManyToOne
    @JoinColumn(name = "diretor_id")
    public Diretor diretor;

    @ManyToMany
    @JoinTable(
            name = "filme_ator",
            joinColumns = @JoinColumn(name = "filme_id"),
            inverseJoinColumns = @JoinColumn(name = "ator_id")
    )
    public List<Ator> atores;

    @Enumerated(EnumType.STRING)
    public org.acme.Filme.Genero genero;

    public enum Genero {
        ACAO,
        COMEDIA,
        DRAMA,
        FICCAO_CIENTIFICA,
        TERROR,
        ROMANCE
    }
}
