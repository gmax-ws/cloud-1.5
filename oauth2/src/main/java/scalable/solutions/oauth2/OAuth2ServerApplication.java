package scalable.solutions.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.security.Principal;

/**
 * Authentication and authorization (OAuth2) server.
 * <p>
 * OAuth2 endpoints are:
 * <p>
 * /uaa/oauth/token the Token endpoint, for clients to acquire access tokens.
 * With Spring Cloud Security this is the oauth2.client.tokenUri.
 * <p>
 * /uaa/oauth/authorize the Authorization endpoint to obtain user approval for a
 * token grant. Spring Cloud Security configures this in a client app as
 * oauth2.client.authorizationUri.
 * <p>
 * /uaa/oauth/check_token the Check Token endpoint (not part of the OAuth2
 * spec). Can be used to decode a token remotely. Spring Cloud Security
 * configures this in a client app as oauth2.resource.tokenInfoUri.
 *
 * @author Marius
 */
@RestController
@EnableResourceServer
@EnableWebSecurity
@SpringBootApplication
public class OAuth2ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2ServerApplication.class, args);
    }

    /**
     * This endpoint is used with random tokens to get user attributes in order
     * to implement services endpoints security. It is not used by JWT tokens.
     * JWT tokens are validated locally using exported public key.
     *
     * @param user Principal attributes
     * @return Principal attributes
     */
    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @Configuration
    @Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
    protected static class LoginConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/**")
                    .authenticated()
                    .and()
                    .httpBasic()
                    .realmName("OAuth2Server")
                    .and()
                    .csrf()
                    .disable();
        }

        @Autowired
        DataSource dataSource;

        /**
         * Authentication. For POC the list of users is build in memory. In a
         * production environment should be fetch from database or from other
         * systems like LDAP
         *
         * @param auth Authentication manager
         * @throws Exception On errors
         */
        @Autowired
        protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.jdbcAuthentication().dataSource(dataSource).
                    usersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username=?").
                    authoritiesByUsernameQuery("SELECT username, role FROM roles WHERE username=?");
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

        @Autowired
        DataSource dataSource;

        @Value("${security.oauth2.resource.token-type}")
        private String tokenType;

        @Value("${security.oauth2.resource.key-store}")
        private String keyStore;

        @Value("${security.oauth2.resource.key-store-password}")
        private String keyStorePassword;

        @Value("${security.oauth2.resource.keyAlias}")
        private String keyAlias;

        @Autowired
        private Environment environment;

        @Autowired
        private AuthenticationManager authenticationManager;

        /**
         * Set encryption keys for JWT tokens (see project resources folder)
         *
         * @return Access token converter
         */
        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            Resource keyStoreResource = new ClassPathResource(this.keyStore);
            KeyStoreKeyFactory keyFactory = new KeyStoreKeyFactory(keyStoreResource,
                    this.keyStorePassword.toCharArray());
            KeyPair keyPair = keyFactory.getKeyPair(this.keyAlias);
            converter.setKeyPair(keyPair);
            return converter;
        }

        /**
         * Configure Authorization Server for random or JWT tokens.
         */
        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
            endpoints.authenticationManager(authenticationManager);
            if ("jwt".equalsIgnoreCase(this.tokenType)) {
                endpoints.accessTokenConverter(jwtAccessTokenConverter());
            }
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
            oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
        }

        /**
         * Build a list of clients. For POC the list is build in memory. On a
         * production environment the list should be build from database or from
         * other sources like LDAP.
         */
        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.jdbc(dataSource);
        }
    }
}
