package cloud.newshive.mini_project.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import cloud.newshive.mini_project.model.Article;

@Repository
public class BookmarkRepo {

    @Autowired
    @Qualifier("hashTemplate")
    RedisTemplate<String, String> template;

    public Map<Object, Object> getBookmarks(String email) {
        return template.opsForHash().entries(email);
    }

    public void markBookmarkedArticles(String email, List<Article> articles) {
        Map<Object, Object> bookmarkedArticles = getBookmarks(email); 
        articles.forEach(article -> 
            article.setBookmarked(bookmarkedArticles.containsKey(article.getTitle()))
        );
    }

    public boolean isArticleBookmarked(String email, String title) {
        return template.opsForHash().hasKey(email, title);
    }

    public void deleteArticle(String email, String title) {
        template.opsForHash().delete(email, title);
    }

    public void addArticle(String email, String title, String articleData) {
        template.opsForHash().put(email, title, articleData);
    }
}
