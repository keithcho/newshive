package cloud.newshive.mini_project.service;

import java.io.StringReader;

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

import cloud.newshive.mini_project.config.NewsConfigProperties;
import cloud.newshive.mini_project.constant.RedisKey;
import cloud.newshive.mini_project.constant.Url;
import cloud.newshive.mini_project.repository.ArticleRepo;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ArticleService {

    // @Value("${news.api.api-key}")
    // public String NEWS_API_KEY;

    @Autowired
    ArticleRepo articleRepo;

    @Autowired
    NewsConfigProperties newsConfig;

    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    
    // Retrieve top headlines and returns the full response entity
    public ResponseEntity<String> getTopHeadlines() throws Exception {

        // Build request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-Api-Key", newsConfig.apiKey()); // TODO: replace with env variables
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromUriString(Url.NEWS_TOP_HEADLINES)
                .queryParam("country", "us")
                .encode()
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();

        try {
            logger.info("Getting top headlines from News API");
            ResponseEntity<String> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new Exception();
        }

    }

    public void addTopHeadlines(String body) {

        if (articleRepo.deleteKey(RedisKey.TOP_HEADLINES)) logger.info("Cleared existing top headlines from Redis");

        logger.info("Adding current top headlines to Redis");
        JsonReader jsonReader = Json.createReader(new StringReader(body));
        JsonObject jsonObject = jsonReader.readObject();
        JsonArray articleArray = jsonObject.getJsonArray("articles");

        for (int i = 0; i < articleArray.size(); i++) {
            String articleTitle = articleArray.getJsonObject(i).getString("title");

            articleRepo.addTopHeadlines(articleTitle, articleArray.getJsonObject(i).toString());
        }
        jsonReader.close();
    }

}