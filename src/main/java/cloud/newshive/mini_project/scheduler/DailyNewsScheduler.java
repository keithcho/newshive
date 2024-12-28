package cloud.newshive.mini_project.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cloud.newshive.mini_project.service.ArticleService;

@Component
public class DailyNewsScheduler {

    @Autowired
    ArticleService articleService;

    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public DailyNewsScheduler() {
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshTopHeadlines() {
        try {
            ResponseEntity<String> response = articleService.getTopHeadlines();
            articleService.addTopHeadlines(response.getBody());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
