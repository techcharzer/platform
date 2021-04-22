package com.cz.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class RedisConfiguration {

	private RedisConfigurationProps props;

	@Bean
	protected JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(props.getHost(),
				props.getPort());
		redisStandaloneConfiguration.setDatabase(props.getDb());
		return new JedisConnectionFactory(redisStandaloneConfiguration);
	}

}
