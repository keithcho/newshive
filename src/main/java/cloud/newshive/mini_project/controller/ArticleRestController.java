package cloud.newshive.mini_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.newshive.mini_project.service.ArticleService;
import jakarta.json.JsonArray;


@RestController
@RequestMapping("/api")
public class ArticleRestController {

    @Autowired
    ArticleService articleService;

    @GetMapping("/top")
    public ResponseEntity<String> getTopHeadlines() {
        JsonArray articleArray = articleService.readTopHeadlinesJson();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(articleArray.toString());
    }
    
    
}
