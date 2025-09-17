package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Filme extends PanacheEntity {
    public String titulo;
    public String diretor;
    public String genero;
    public int anoLancamento;
    public boolean disponivel;
}
