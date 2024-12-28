package cloud.newshive.mini_project.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import cloud.newshive.mini_project.config.SecretsConfigProperties;
import cloud.newshive.mini_project.constant.RedisKey;
import cloud.newshive.mini_project.constant.Url;
import cloud.newshive.mini_project.model.Article;
import cloud.newshive.mini_project.repository.ArticleRepo;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

@Service
public class ArticleService {
    
    @Autowired
    ArticleRepo articleRepo;
    
    @Autowired
    SecretsConfigProperties secretsConfig;
    
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    
    public String pingRedis() {
        return articleRepo.ping();
    }

    // Retrieve top headlines and returns the full response entity
    public ResponseEntity<String> getTopHeadlines() throws Exception {

        // Build request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-Api-Key", secretsConfig.newsApiKey());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Build query URL
        String urlTemplate = UriComponentsBuilder.fromUriString(Url.NEWS_TOP_HEADLINES)
                .queryParam("country", "us")
                .queryParam("pageSize", 100)
                .encode()
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();

        try {
            logger.info("Getting top headlines with News API");
            ResponseEntity<String> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception();
        }
    }

    // Get news with OkSurf API
    public ResponseEntity<String> getCategoryNews() throws Exception {

        // Build request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            logger.info("Getting news with OkSurf API");
            ResponseEntity<String> response = restTemplate.exchange(
                    Url.OKSURF_NEWS_FEED,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception();
        }
    }

    public ResponseEntity<String> searchArticles(String query, int page) throws Exception {

        // Build request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-Api-Key", secretsConfig.newsApiKey());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromUriString(Url.NEWS_EVERYTHING)
                .queryParam("q", query)
                .queryParam("language", "en")
                .queryParam("pageSize", 50)
                .queryParam("page", page)
                .encode()
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();

        try {
            logger.info("Searching query " + query + " from News API");
            ResponseEntity<String> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception();
        }
    }

    // 1. Removes existing hash containing top headlines
    // 2. Adds new hash with current top headlines
    public void addTopHeadlines(String body) {

        if (articleRepo.deleteKey(RedisKey.TOP_HEADLINES)) logger.info("Cleared existing top headlines from Redis");

        logger.info("Adding current top headlines to Redis");
        JsonReader jsonReader = Json.createReader(new StringReader(body));
        JsonObject jsonObject = jsonReader.readObject();
        JsonArray articleArray = jsonObject.getJsonArray("articles");

        for (int i = 0; i < articleArray.size(); i++) {
            String articleTitle = articleArray.getJsonObject(i).getString("title");

            if (!articleTitle.equals("[Removed]")) {
                articleRepo.addTopHeadlines(articleTitle, articleArray.getJsonObject(i).toString());
            }
        }
        jsonReader.close();
        logger.info("Finished adding top headlines to Redis");
    }

    public JsonArray readTopHeadlinesJson() {
        List<Object> articleList = articleRepo.getTopHeadlines();

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        
        for (Object article : articleList) {
            JsonReader jsonReader = Json.createReader(new StringReader(article.toString()));
            JsonObject articleObject = jsonReader.readObject();
            jsonArrayBuilder.add(articleObject);
            jsonReader.close();
        }

        JsonArray response = jsonArrayBuilder.build();
        return response;
    }

    public List<Article> readTopHeadlinesList() {
        List<Object> articleList = articleRepo.getTopHeadlines();

        List<Article> articles = new ArrayList<>();

        for (Object article : articleList) {

            Article a = new Article();
            JsonReader jsonReader = Json.createReader(new StringReader(article.toString()));
            JsonObject articleObject = jsonReader.readObject();

            JsonValue sourceId = articleObject.getJsonObject("source").get("id");

            if (sourceId != null && sourceId instanceof JsonString) {
                a.setSourceId(((JsonString) sourceId).getString());
            }

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
            // a.setUrlToImage(articleObject.getString("urlToImage"));

            JsonValue urlToImageValue = articleObject.get("urlToImage");
            if (urlToImageValue != null && urlToImageValue instanceof JsonString) {
                a.setUrlToImage(((JsonString) urlToImageValue).getString());
            }

            a.setPublishedAt(articleObject.getString("publishedAt"));
            
            JsonValue contentValue = articleObject.get("content");
            if (contentValue != null && contentValue instanceof JsonString) {
                a.setContent(((JsonString) contentValue).getString());
            }

            articles.add(a);

            jsonReader.close();
        }

        return articles;
    }

    // NewsAPI
    public List<Article> storeSearchedArticles(String body) {

        List<Article> articles = new ArrayList<>();

        JsonReader jsonReader = Json.createReader(new StringReader(body));
        JsonObject jsonObject = jsonReader.readObject();
        JsonArray articleArray = jsonObject.getJsonArray("articles");

        for (int i = 0; i < articleArray.size(); i++) {
            String articleTitle = articleArray.getJsonObject(i).getString("title");

            if (!articleTitle.equals("[Removed]")) {
                Article a = new Article();

                JsonObject articleObject = articleArray.getJsonObject(i);

                JsonValue sourceId = articleObject.getJsonObject("source").get("id");

                if (sourceId != null && sourceId instanceof JsonString) {
                    a.setSourceId(((JsonString) sourceId).getString());
                }

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

                a.setPublishedAt(articleObject.getString("publishedAt"));
                
                JsonValue contentValue = articleObject.get("content");
                if (contentValue != null && contentValue instanceof JsonString) {
                    a.setContent(((JsonString) contentValue).getString());
                }

                articles.add(a);
            }
        }
        jsonReader.close();
        return articles;
    }

    // OkSurf API
    public List<Article> storeCategoryArticles(String body, String category) {

        List<Article> articles = new ArrayList<>();

        JsonReader jsonReader = Json.createReader(new StringReader(body));
        JsonObject jsonObject = jsonReader.readObject();
        JsonArray articleArray = jsonObject.getJsonArray(category);

        for (int i = 0; i < articleArray.size(); i++) {
            String articleTitle = articleArray.getJsonObject(i).getString("title");

            if (!articleTitle.equals("[Removed]")) {
                Article a = new Article();

                JsonObject articleObject = articleArray.getJsonObject(i);

                a.setSourceName(articleObject.getString("source"));
                a.setTitle(articleObject.getString("title"));
                a.setUrl(articleObject.getString("link"));

                JsonValue urlToImageValue = articleObject.get("og");
                if (urlToImageValue != null && urlToImageValue instanceof JsonString) {
                    a.setUrlToImage(((JsonString) urlToImageValue).getString());
                }
                articles.add(a);
            }
        }
        jsonReader.close();
        return articles;
    }

    public int getTotalPages(String body) {

        JsonReader jsonReader = Json.createReader(new StringReader(body));
        JsonObject jsonObject = jsonReader.readObject();

        int totalResults = jsonObject.getInt("totalResults");

        return (int) Math.ceil(totalResults/50);
    }

    // Map -> Article
    public Article articleMapper(Map<String, String> articleData) {
        Article article = new Article();

        article.setTitle(articleData.get("title"));
        article.setDescription(articleData.get("description"));
        article.setUrl(articleData.get("url"));
        article.setUrlToImage(articleData.get("urlToImage"));
        article.setSourceName(articleData.get("sourceName"));
        article.setPublishedAt(articleData.get("publishedAt"));

        return article;
    }

    // Article -> Json
    public JsonObject articleJsonSerializer(Article article) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        
        JsonObjectBuilder sourceBuilder = Json.createObjectBuilder();
        sourceBuilder.add("id", JsonValue.NULL);
        sourceBuilder.add("name", article.getSourceName());
        jsonObjectBuilder.add("source", sourceBuilder);
        
        addFieldIfNotNull(jsonObjectBuilder, "title", article.getTitle());
        addFieldIfNotNull(jsonObjectBuilder, "description", article.getDescription());
        addFieldIfNotNull(jsonObjectBuilder, "url", article.getUrl());
        addFieldIfNotNull(jsonObjectBuilder, "urlToImage", article.getUrlToImage());
        addFieldIfNotNull(jsonObjectBuilder, "publishedAt", article.getPublishedAt());
        
        return jsonObjectBuilder.build();
    }

    // Helper method to add a field only if it's not null
    private void addFieldIfNotNull(JsonObjectBuilder builder, String key, String value) {
        if (value != null) {
            builder.add(key, value);
        } else {
            builder.add(key, JsonValue.NULL);
        }
    }
}
