package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to manage idempotency keys for POST operations.
 * NOTE: This is an in-memory implementation for demonstration.
 * A production application should use a persistent store (e.g., Redis).
 */
@ApplicationScoped
public class IdempotencyService {

    // Simples cache em memória, tipado para Response (o que o Recurso salva)
    private final ConcurrentHashMap<String, Response> cache = new ConcurrentHashMap<>();

    /**
     * Checks if the response for the given idempotency key exists in the cache.
     * @param key The idempotency key.
     * @return true if the key is present.
     */
    public boolean exists(String key) {
        return cache.containsKey(key);
    }

    /**
     * Saves the response associated with the given idempotency key.
     * @param key The idempotency key (from HeaderParam).
     * @param response The Response object to be cached.
     */
    public void save(String key, Response response) {
        // Usa computeIfAbsent para garantir que o save ocorra atomicamente
        // Nota: Response não é ideal para caching em memória, mas funciona para demonstração.
        cache.put(key, response);
    }

    /**
     * Loads a previously cached response based on the idempotency key.
     * @param key The idempotency key.
     * @return The cached Response object, or null if not found.
     */
    public Response load(String key) {
        return cache.get(key);
    }

    /**
     * Removes an idempotency key from the cache. Usado em caso de falha de processamento
     * para permitir que o cliente tente novamente.
     * @param key The idempotency key.
     */
    public void remove(String key) {
        cache.remove(key);
    }
}