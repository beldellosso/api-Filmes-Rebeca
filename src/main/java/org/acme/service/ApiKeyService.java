package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Set;

/**
 * Service to validate the API Key against the configured keys in application.properties.
 * This separates the configuration logic from the filter logic, supporting multiple keys.
 */
@ApplicationScoped
public class ApiKeyService {

    // Injeta um conjunto de chaves de API esperadas do arquivo application.properties.
    // O Quarkus/MicroProfile injeta uma lista (ou Set) se a propriedade for uma string separada por vírgulas.
    @Inject
    @ConfigProperty(name = "api.security.keys", defaultValue = "DEFAULT_SECRET")
    Set<String> validApiKeys;

    /**
     * Checks if the provided API Key matches any of the expected keys.
     * @param apiKey The key provided in the request header (X-API-Key).
     * @return true if the key is valid and present in the configured set, false otherwise.
     */
    public boolean isValidKey(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return false;
        }
        // Verifica se a chave fornecida está no conjunto de chaves válidas configuradas.
        return validApiKeys.contains(apiKey);
    }
}