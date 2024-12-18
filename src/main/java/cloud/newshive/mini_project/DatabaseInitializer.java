package cloud.newshive.mini_project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cloud.newshive.mini_project.service.ArticleService;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    
    @Autowired
    ArticleService articleService;

    @Override
    public void run(String... args) {

        try {
            ResponseEntity<String> response = articleService.getTopHeadlines();
            articleService.addTopHeadlines(response.getBody());
        } catch (Exception e) {

        }

    }
}
