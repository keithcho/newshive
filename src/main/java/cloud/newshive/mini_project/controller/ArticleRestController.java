package cloud.newshive.mini_project.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.newshive.mini_project.model.Article;
import cloud.newshive.mini_project.service.ArticleService;
import cloud.newshive.mini_project.service.BookmarkService;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;



@RestController
@RequestMapping("/api")
public class ArticleRestController {

    @Autowired
    ArticleService articleService;

    @Autowired
    BookmarkService bookmarkService;

    @GetMapping("/top")
    public ResponseEntity<String> getTopHeadlines() {
        JsonArray articleArray = articleService.readTopHeadlinesJson();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(articleArray.toString());
    }
    
    @PostMapping("/bookmark")
    public ResponseEntity<Map<String, Boolean>> toggleBookmark(@RequestBody Map<String, String> articleData, HttpSession session) {
        
        String email = (String) session.getAttribute("email");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String articleTitle = articleData.get("title");

        Article article = articleService.articleMapper(articleData);
        JsonObject articleJson = articleService.articleJsonSerializer(article);

        if (bookmarkService.isArticleBookmarked(email, articleTitle)) {
            bookmarkService.deleteArticle(email, articleTitle);
            return ResponseEntity.ok(Map.of("bookmarked", false));
        } else {
            bookmarkService.addArticle(email, articleTitle, articleJson.toString());
            return ResponseEntity.ok(Map.of("bookmarked", true));
        }
    }
}
