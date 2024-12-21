package cloud.newshive.mini_project.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import cloud.newshive.mini_project.model.Article;
import cloud.newshive.mini_project.service.ArticleService;


@Controller
public class ArticleController {
    
    @Autowired
    ArticleService articleService;

    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @GetMapping(path={"/", "/top"})
    public String getTopArticles(Model model) {

        List<Article> articles = articleService.readTopHeadlinesList();

        model.addAttribute("articles", articles);

        return "top";
    }
    


}
