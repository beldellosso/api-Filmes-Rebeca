package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to manage Rate Limiting (limite de taxa de requisições) em memória.
 * Utiliza o algoritmo Fixed Window Counter (Janela Fixa).
 *
 * NOTA: Em produção, seria necessário usar uma solução distribuída (e.g., Redis)
 * para sincronizar o estado em múltiplas instâncias da API.
 */
@ApplicationScoped
public class RateLimitService { // Nome da classe corrigido

    // Cache para armazenar o estado de limite de taxa por API Key (ou identificador de cliente)
    private final Map<String, RequestState> rateLimitCache = new ConcurrentHashMap<>();

    // Injeta configurações do application.properties
    @Inject
    @ConfigProperty(name = "api.ratelimit.limit", defaultValue = "20")
    int requestLimit;

    // A janela de tempo é configurada em segundos.
    @Inject
    @ConfigProperty(name = "api.ratelimit.window.seconds", defaultValue = "60")
    long windowSeconds;


    /**
     * Tenta processar uma requisição para a chave de API fornecida.
     * @param clientIdentifier O identificador do cliente (e.g., chave de API ou IP).
     * @return true se a requisição for permitida, false se o limite for excedido.
     */
    public boolean allowRequest(String clientIdentifier) {
        RequestState state = rateLimitCache.computeIfAbsent(clientIdentifier, k -> new RequestState());
        Instant now = Instant.now();

        // Define o início da janela de tempo atual
        Instant windowStart = now.minusSeconds(windowSeconds);

        // Se a última requisição foi feita fora da janela atual, reinicia a contagem
        if (state.lastRequestTime.isBefore(windowStart)) {
            state.requestCount = 0;
            state.lastRequestTime = now;
        }

        // Incrementa e verifica se excedeu o limite
        if (state.requestCount < requestLimit) {
            state.requestCount++;
            return true;
        } else {
            // Limite excedido
            return false;
        }
    }

    /**
     * Retorna o número total de requisições permitidas na janela.
     * @return O limite de requisições.
     */
    public int getLimit() {
        return requestLimit;
    }

    /**
     * Retorna o número de requisições restantes na janela atual.
     * @param clientIdentifier O identificador do cliente.
     * @return O número de requisições restantes.
     */
    public int remainingRequests(String clientIdentifier) {
        RequestState state = rateLimitCache.get(clientIdentifier);
        if (state == null) {
            return requestLimit; // Se não houver estado, assume o limite total
        }

        Instant now = Instant.now();
        Instant windowStart = now.minusSeconds(windowSeconds);

        // Se a janela resetou, retorna o limite total
        if (state.lastRequestTime.isBefore(windowStart)) {
            return requestLimit;
        }

        return requestLimit - state.requestCount;
    }

    /**
     * Classe interna para armazenar o estado da requisição por chave de API.
     */
    private static class RequestState {
        int requestCount;
        Instant lastRequestTime;

        public RequestState() {
            this.requestCount = 0;
            // Inicializa com um tempo antigo para garantir que a primeira requisição seja contada.
            this.lastRequestTime = Instant.EPOCH;
        }
    }
}