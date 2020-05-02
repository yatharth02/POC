package counter.poc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import counter.poc.entity.OrgRequestCounter;

/**
 * Created by OmiD.HaghighatgoO on 8/19/2019.
 */

@Configuration
public class ApexxConfig {

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}
	 
	@Bean
	public RedisTemplate<String, OrgRequestCounter> redisTemplate() {
		final RedisTemplate<String, OrgRequestCounter> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<Object>(Object.class));
		template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
		return template;
	}

}

