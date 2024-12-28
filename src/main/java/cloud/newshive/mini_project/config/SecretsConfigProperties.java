package cloud.newshive.mini_project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="secret")
public record SecretsConfigProperties(String newsApiKey, String mailersendApiKey, String domain) {
    
}
