package cloud.newshive.mini_project.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import cloud.newshive.mini_project.constant.RedisKey;

@Repository
public class ArticleRepo {

    @Autowired
    @Qualifier("hashTemplate")
    RedisTemplate<String, String> template;

    public String ping() {
        return template.getConnectionFactory().getConnection().ping();
    }

    public Boolean addTopHeadlines(String hashKey, String value) {
        return template.opsForHash().putIfAbsent(RedisKey.TOP_HEADLINES, hashKey, value);
    }

    public Boolean deleteKey(String key) {
        return template.delete(key);
    }

    // Returns the size of the map
    public Long getSize(String key) {
        return template.opsForHash().size(key);
    }

    public List<Object> getTopHeadlines() {
        List<Object> articleList = template.opsForHash().values(RedisKey.TOP_HEADLINES);
        return articleList;
    }
}
