package org.acme.filter;

import jakarta.ws.rs.NameBinding;
// A linha 'import jakarta.inject.Qualifier;' DEVE ser removida!
import jakarta.enterprise.util.Nonbinding;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Anotação de Binding (JAX-RS) para aplicar o RateLimitFilter.
 * IMPORTANTE: NÃO PODE ter a anotação @Qualifier.
 * Isso resolve a ambiguidade na injeção do RateLimitService.
 */
@NameBinding // Mantido: liga o filtro ao recurso JAX-RS
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RateLimited {

    @Nonbinding
    String dummy() default "";
}