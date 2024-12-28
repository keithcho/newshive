package cloud.newshive.mini_project.repository;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmailRepo {
    
    @Autowired
    @Qualifier("valueTemplate")
    RedisTemplate<String, String> template;

    public void addVerificationCode(String email, String code) {
        String key = "verification:" + email;
        template.opsForValue().set(key, code, 10L, TimeUnit.MINUTES);
    }

    public String getVerificationCode(String email) {
        String key = "verification:" + email;
        return template.opsForValue().get(key);
    }
    
    public Boolean deleteVerificationCode(String email) {
        String key = "verification:" + email;
        return template.delete(key);
    }
}
