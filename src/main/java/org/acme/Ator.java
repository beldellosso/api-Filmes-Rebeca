package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

/**
 * Entidade para Ator, utilizando Panache (Entity pattern)
 * Estendendo PanacheEntity garante acesso aos métodos persist() e listAll().
 */
@Entity
public class Ator extends PanacheEntity {

    @NotBlank
    public String nome;

    // Construtores são boas práticas
    public Ator() {
    }

    public Ator(String nome) {
        this.nome = nome;
    }
}