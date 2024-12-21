package cloud.newshive.mini_project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cloud.newshive.mini_project.service.ArticleService;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    
    @Autowired
    ArticleService articleService;

    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Override
    public void run(String... args) {

        // Ping Redis server
        try {
            String pingResponse = articleService.pingRedis();
            if (pingResponse.equals("PONG")) {
                logger.info("Successfully connected to Redis");
            } else {
                logger.warn("Redis PING response: " + pingResponse);
            }
        } catch (Exception e) {
            logger.error("Error connecting to Redis: " + e.getMessage());
        }

        // Initialise Redis database with current top headlines
        try {
            ResponseEntity<String> response = articleService.getTopHeadlines();
            articleService.addTopHeadlines(response.getBody());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }
}
