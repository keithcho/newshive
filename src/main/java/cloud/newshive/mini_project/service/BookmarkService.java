package cloud.newshive.mini_project.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cloud.newshive.mini_project.model.Article;
import cloud.newshive.mini_project.repository.BookmarkRepo;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

@Service
public class BookmarkService {

    @Autowired
    BookmarkRepo bookmarkRepo;

    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());


    public List<Article> getBookmarkedArticles(String email) {
        Map<Object, Object> bookmarkMap = bookmarkRepo.getBookmarks(email);

        List<Object> bookmarkValues = new ArrayList<>(bookmarkMap.values());

        List<Article> articles = new ArrayList<>();

        for (Object data : bookmarkValues) {
            Article a = new Article();

            JsonReader jsonReader = Json.createReader(new StringReader(data.toString()));
            JsonObject articleObject = jsonReader.readObject();
    
            a.setSourceName(articleObject.getJsonObject("source").getString("name"));

            JsonValue authorValue = articleObject.get("author");
            if (authorValue != null && authorValue instanceof JsonString) {
                a.setAuthor(((JsonString) authorValue).getString());
            }

            a.setTitle(articleObject.getString("title"));

            JsonValue descriptionValue = articleObject.get("description");
            if (descriptionValue != null && descriptionValue instanceof JsonString) {
                a.setDescription(((JsonString) descriptionValue).getString());
            }

            a.setUrl(articleObject.getString("url"));

            JsonValue urlToImageValue = articleObject.get("urlToImage");
            if (urlToImageValue != null && urlToImageValue instanceof JsonString) {
                a.setUrlToImage(((JsonString) urlToImageValue).getString());
            }

            JsonValue publishedAtValue = articleObject.get("publishedAt");

            if (publishedAtValue != null && publishedAtValue instanceof JsonString) {
                a.setContent(((JsonString) publishedAtValue).getString());
            }
            
            JsonValue contentValue = articleObject.get("content");
            if (contentValue != null && contentValue instanceof JsonString) {
                a.setContent(((JsonString) contentValue).getString());
            }

            articles.add(a);
            jsonReader.close();
        }
        return articles;
    }

    public void markBookmarkedArticles(String email, List<Article> articles) {
        bookmarkRepo.markBookmarkedArticles(email, articles);
    }

    public boolean isArticleBookmarked(String email, String title) {
        return bookmarkRepo.isArticleBookmarked(email, title);
    }

    public void deleteArticle(String email, String title) {
        bookmarkRepo.deleteArticle(email, title);;
    }

    public void addArticle(String email, String title, String articleData) {
        bookmarkRepo.addArticle(email, title, articleData);;
    }
}
