package cloud.newshive.mini_project.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import cloud.newshive.mini_project.model.Article;
import cloud.newshive.mini_project.service.ArticleService;
import cloud.newshive.mini_project.service.BookmarkService;
import jakarta.servlet.http.HttpSession;


@Controller
public class ArticleController {
    
    @Autowired
    ArticleService articleService;

    @Autowired
    BookmarkService bookmarkService;

    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @GetMapping(path={"/", "/top"})
    public String getTopArticles(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        List<Article> articles = articleService.readTopHeadlinesList();

        if (email != null && isAuthenticated) {
            model.addAttribute("email", email);
            model.addAttribute("isAuthenticated", isAuthenticated);
            bookmarkService.markBookmarkedArticles(email, articles);
        }
        model.addAttribute("articles", articles);
        
        return "top";
    }

    @GetMapping("/search")
    public String searchArticles(@RequestParam("q") String query, @RequestParam(defaultValue="1") int page, HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");
        String encodedQuery = query.replace(" ", "+");

        List<Article> articles = new ArrayList<>();

        try {
            ResponseEntity<String> responseBody = articleService.searchArticles(encodedQuery, page);
            articles = articleService.storeSearchedArticles(responseBody.getBody());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        model.addAttribute("articles", articles);
        model.addAttribute("query", query);
        model.addAttribute("page", page);

        if (email != null && isAuthenticated) {
            model.addAttribute("email", email);
            model.addAttribute("isAuthenticated", isAuthenticated);
            bookmarkService.markBookmarkedArticles(email, articles);
        }

        return "search";
    }

    @GetMapping("/category/{category}")
    public String getCategoryArticles(@PathVariable String category, HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        List<Article> articles = new ArrayList<>();

        try {
            ResponseEntity<String> responseBody = articleService.getCategoryNews();
            articles = articleService.storeCategoryArticles(responseBody.getBody(), category);
            model.addAttribute("articles", articles);
            model.addAttribute("category", category);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        if (email != null && isAuthenticated) {
            model.addAttribute("email", email);
            model.addAttribute("isAuthenticated", isAuthenticated);
            bookmarkService.markBookmarkedArticles(email, articles);
        }

        return "category";
    }

    @GetMapping("/bookmarks")
    public String getBookmarkedArticles(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        List<Article> articles = bookmarkService.getBookmarkedArticles(email);

        if (email != null && isAuthenticated) {
            model.addAttribute("email", email);
            model.addAttribute("isAuthenticated", isAuthenticated);
            bookmarkService.markBookmarkedArticles(email, articles);
            model.addAttribute("articles", articles);
            return "bookmarks";
        } else {
            return "error";
        }
    }

    @GetMapping("/about")
    public String getAbout(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        if (email != null && isAuthenticated) {
            model.addAttribute("email", email);
            model.addAttribute("isAuthenticated", isAuthenticated);
        }

        return "about";
    }
}
