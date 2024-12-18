package cloud.newshive.mini_project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("news.api")
public record NewsConfigProperties(String apiKey) {
    
}
