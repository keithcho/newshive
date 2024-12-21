package cloud.newshive.mini_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import cloud.newshive.mini_project.config.SecretsConfigProperties;

@SpringBootApplication
@EnableConfigurationProperties(SecretsConfigProperties.class)
public class MiniProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniProjectApplication.class, args);
	}

}
