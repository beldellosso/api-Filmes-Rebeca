package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Serviço responsável por implementar a lógica de limite de taxa
 * usando o algoritmo de janela deslizante (Sliding Window Log).
 *
 * NOTA: O uso de ConcurrentHashMap é adequado para uma única instância do Quarkus.
 * Em um ambiente distribuído (múltiplas instâncias), uma solução de cache distribuído
 * como Redis ou Infinispan seria necessária para sincronizar o estado.
 */
@ApplicationScoped
public class RateLimitService {

    // Mapa thread-safe: Chave da API -> Lista de Timestamps das requisições (em milissegundos)
    private final ConcurrentHashMap<String, List<Long>> requestMap = new ConcurrentHashMap<>();

    // O valor configurado é uma variável de instância (não estática)
    @ConfigProperty(name = "api.ratelimit.limit", defaultValue = "10")
    int maxRequests;

    // O valor configurado é uma variável de instância (não estática)
    @ConfigProperty(name = "api.ratelimit.window.seconds", defaultValue = "60")
    long windowSeconds;

    /**
     * Tenta permitir uma nova requisição para uma dada chave de API.
     * @param apiKey A chave de identificação da API.
     * @return Um objeto RateLimitResponse indicando se a requisição foi permitida e o status.
     */
    public RateLimitResponse allowRequest(String apiKey) {
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - TimeUnit.SECONDS.toMillis(windowSeconds);

        // Garante que existe uma lista para a chave, ou cria uma nova se for a primeira vez.
        // O uso de CopyOnWriteArrayList é para facilitar a filtragem/modificação
        // e garantir a segurança de threads, embora seja menos eficiente para listas grandes.
        List<Long> timestamps = requestMap.computeIfAbsent(apiKey, k -> new CopyOnWriteArrayList<>());

        // Sincroniza o acesso à lista para garantir atomicidade nas operações de limpeza e adição
        synchronized (timestamps) {
            // 1. Limpa os timestamps expirados (fora da janela deslizante)
            timestamps.removeIf(timestamp -> timestamp < windowStart);

            // 2. Checa o limite
            if (timestamps.size() < maxRequests) {
                // 3. Permite a requisição: Adiciona o timestamp atual e calcula o restante
                timestamps.add(currentTime);
                int remaining = maxRequests - timestamps.size();

                return new RateLimitResponse(true, remaining, 0);

            } else {
                // 4. Bloqueia a requisição: Calcula o tempo de espera (Retry-After)
                // O timestamp mais antigo (primeira requisição da janela) é o que precisa expirar.
                long oldestTimestamp = timestamps.get(0);
                long timeToWaitMillis = (oldestTimestamp + TimeUnit.SECONDS.toMillis(windowSeconds)) - currentTime;

                // Garante que o tempo de espera seja pelo menos 1 segundo
                long timeToWaitSeconds = Math.max(1, TimeUnit.MILLISECONDS.toSeconds(timeToWaitMillis));

                // A requisição não é permitida, 0 restante, tempo de espera calculado.
                return new RateLimitResponse(false, 0, timeToWaitSeconds);
            }
        }
    }

    /**
     * Getter público para a propriedade de configuração maxRequests.
     * Necessário para o RateLimitFilter que define o header X-RateLimit-Limit.
     */
    public int getMaxRequests() {
        return maxRequests;
    }

    /**
     * Getter público para a propriedade de configuração windowSeconds.
     * Necessário se você quiser expor a janela de tempo em algum lugar (e para consistência).
     */
    public long getWindowSeconds() {
        return windowSeconds;
    }


    /**
     * Classe interna estática para empacotar a resposta do limite de taxa.
     */
    public static class RateLimitResponse {
        private final boolean allowed;
        private final int remaining;
        private final long waitTimeSeconds; // Tempo até o próximo slot ser liberado

        public RateLimitResponse(boolean allowed, int remaining, long waitTimeSeconds) {
            this.allowed = allowed;
            this.remaining = remaining;
            this.waitTimeSeconds = waitTimeSeconds;
        }

        public boolean isAllowed() {
            return allowed;
        }

        public int getRemaining() {
            return remaining;
        }

        public long getWaitTimeSeconds() {
            return waitTimeSeconds;
        }
    }
}