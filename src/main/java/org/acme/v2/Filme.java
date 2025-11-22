package org.acme.v2;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entidade Filme. Contém a enumeração Genero, referenciada no FilmeResource.
 */
@Entity
public class Filme extends PanacheEntity {

    @NotBlank(message = "O título é obrigatório")
    public String titulo;

    @NotNull(message = "O gênero é obrigatório")
    public org.acme.Filme.Genero genero;

    @Min(value = 1888, message = "O ano deve ser válido (ex: 1999)")
    public int ano;

    // Enumeração interna para o gênero do filme
    public enum Genero {
        ACAO, DRAMA, COMEDIA, TERROR, FICCAO_CIENTIFICA, DOCUMENTARIO
    }

    // Outros campos como diretor, atores, etc. seriam adicionados aqui
}
