package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

/**
 * Entidade para Diretor, utilizando Panache.
 * Deve ter o pacote correto (org.acme) e um construtor padrão.
 */
@Entity
public class Diretor extends PanacheEntity {

    @NotBlank
    public String nome;

    // Construtor padrão necessário para Panache/JPA
    public Diretor() {
    }

    // Construtor opcional para facilitar a criação de objetos
    public Diretor(String nome) {
        this.nome = nome;
    }
}