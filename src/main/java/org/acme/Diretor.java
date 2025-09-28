package com.rebeca.filmes.api.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
public class Diretor extends PanacheEntity {

    @NotBlank(message = "O nome do diretor é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    public String nome;

    @NotBlank(message = "O país de origem é obrigatório.")
    public String paisOrigem;

    // Relacionamento Um para Muitos: um diretor pode ter muitos filmes
    @OneToMany(mappedBy = "diretor")
    public List<Filme> filmes;
}