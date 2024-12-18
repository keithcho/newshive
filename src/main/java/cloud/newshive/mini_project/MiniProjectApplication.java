package cloud.newshive.mini_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import cloud.newshive.mini_project.config.NewsConfigProperties;

@SpringBootApplication
@EnableConfigurationProperties(NewsConfigProperties.class)
public class MiniProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniProjectApplication.class, args);
	}

}
