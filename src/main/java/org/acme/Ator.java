package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
public class Ator extends PanacheEntity {

    @NotBlank(message = "O nome do ator é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    public String nome;

    @NotBlank(message = "O país de origem é obrigatório.")
    public String paisOrigem;

    // Relacionamento Muitos para Muitos: um ator pode atuar em muitos filmes
    @ManyToMany(mappedBy = "atores")
    public List<Filme> filmes;
}
