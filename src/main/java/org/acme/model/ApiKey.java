package org.acme.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

@Entity
public class ApiKey extends PanacheEntity {

    @NotBlank
    public String key; // ex: UUID

    public String owner; // opcional, dono / nome do usuário

    public boolean active = true;

    public Instant createdAt = Instant.now();
}
