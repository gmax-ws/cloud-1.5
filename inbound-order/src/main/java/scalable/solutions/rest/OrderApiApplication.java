package scalable.solutions.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.concurrent.Executor;

/**
 * This is a microservice template having the following capabilities.
 * 
 * Protected endpoints access using JWT and Spring Security. Auto register with
 * Eureka server. Pull configuration from a Config Server. Monitoring endpoints
 * with Hystrix commands. Enable CORS (optional) access. Do not enable CORS if
 * CORS is already enabled in Zuul and the service is accessed via Zull gateway.
 * 
 * 
 * @author Marius
 *
 */
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableEurekaClient
@EnableAsync
@SpringBootApplication
public class OrderApiApplication {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(OrderApiApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OrderApiApplication.class, args).close();
	}

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("order-");
        executor.initialize();
        return executor;
    }

	// @formatter:off
//	/**
//	 * Enable CORS (optional) access. Do not enable CORS if CORS is already enabled in Zuul
//	 * and the service is accessed via Zull gateway. 
//	 * 
//	 * @author Marius
//	 *
//	 */
//	@Configuration
//	@EnableWebMvc
//	public class WebConfig extends WebMvcConfigurerAdapter {
//
//		@Override
//		public void addCorsMappings(CorsRegistry registry) {
//			registry.addMapping("/**");
//		}
//	}
	// @formatter:on

	/**
	 * Resource configuration. Configure for JWT token validation. Integrate JWT
	 * token with Spring Security in order to secure service endpoint.
	 * 
	 * @author Marius
	 *
	 */
	@Configuration
	@EnableResourceServer
	public static class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

		@Autowired
		private Environment environment;

		@Override
		public void configure(ResourceServerSecurityConfigurer config) {
			config.tokenServices(tokenServices());
			config.resourceId(environment.getProperty("security.oauth2.resource.id"));
		}

		@Bean
		public TokenStore tokenStore() {
			return new JwtTokenStore(jwtAccessTokenConverter());
		}

		@Bean
		public JwtAccessTokenConverter jwtAccessTokenConverter() {
			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
			String publicKey = environment.getProperty("security.oauth2.resource.jwt.key-value");
			converter.setVerifierKey(publicKey);
			return converter;
		}

		@Primary
		@Bean
		public DefaultTokenServices tokenServices() {
			DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
			defaultTokenServices.setTokenStore(tokenStore());
			return defaultTokenServices;
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/**").permitAll().anyRequest().authenticated();
		}
	}
}
