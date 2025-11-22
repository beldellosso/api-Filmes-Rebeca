package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço responsável por gerenciar chaves de Idempotência e armazenar respostas em cache.
 * IMPORTANTE: Para produção, use um cache distribuído (Redis/Infinispan) e serialize a resposta.
 * Aqui, usamos um ConcurrentHashMap para simular o cache em memória.
 */
@ApplicationScoped
public class IdempotencyService {

    // Simulação de cache em memória: String (Idempotency-Key) -> Response
    private final ConcurrentHashMap<String, Response> cache = new ConcurrentHashMap<>();

    /**
     * Tenta carregar uma resposta cacheada associada à chave de idempotência.
     *
     * @param key A chave de idempotência do header.
     * @return A Response cacheada, ou null se não for encontrada.
     */
    public Response load(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        // Retorna a resposta se a chave existir no cache
        return cache.get(key);
    }

    /**
     * Salva a resposta de sucesso no cache, associando-a à chave.
     *
     * @param key A chave de idempotência.
     * @param response A Response HTTP final (normalmente 201 Created).
     */
    public void save(String key, Response response) {
        if (key != null && !key.isBlank() && response != null) {
            // Clona a Response para garantir que o objeto em cache não seja alterado
            // e preserva os metadados como headers.
            Response cachedResponse = Response.fromResponse(response).build();
            cache.put(key, cachedResponse);
        }
    }

    /**
     * Remove a chave do cache (usado em caso de falha para permitir uma nova tentativa).
     *
     * @param key A chave de idempotência.
     */
    public void remove(String key) {
        if (key != null && !key.isBlank()) {
            cache.remove(key);
        }
    }

    /**
     * Verifica se a chave existe no cache. Usado pelo filtro.
     *
     * @param key A chave de idempotência.
     * @return true se a chave existe e possui uma resposta, false caso contrário.
     */
    public boolean exists(String key) {
        return key != null && !key.isBlank() && cache.containsKey(key);
    }
}