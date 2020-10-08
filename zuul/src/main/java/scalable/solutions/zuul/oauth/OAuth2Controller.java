package scalable.solutions.zuul.oauth;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import scalable.solutions.zuul.oauth.dto.CredentialsDTO;
import scalable.solutions.zuul.utils.RestUtility;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RefreshScope
@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    public static final String TOKEN_TYPE = "TOKEN_TYPE";

    public static final String AUTHORIZATION = "AUTHORIZATION";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpSession session;

    @Value("${security.oauth2.client.access-token-uri}")
    private String uri;

    @Value("${security.oauth2.client.id}")
    private String client;

    @Value("${security.oauth2.client.client-secret}")
    private String secret;

    @Value("${security.oauth2.client.grant-type}")
    private String grantType;

    /**
     * Login endpoint. Request an access token from OAuth2 server
     *
     * @param credentials User credentials
     * @return A JWT token on success error message otherwise
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody(required = true) CredentialsDTO credentials) {

        // @formatter:off
        // params
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("grant_type", grantType);
        params.set("username", credentials.getUsername());
        params.set("password", credentials.getPassword());

        // headers
        final MultiValueMap<String, String> headers = setHttpHeaders();

        // call
        final HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);

        // send back response
        return storeToken(session, response);
        // @formatter:on
    }

    /**
     * On logout invalidate session.
     *
     * @return OK
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> logout() {
        session.invalidate();
        return new ResponseEntity<String>("{\"message\": \"Logout OK\"}", HttpStatus.OK);
    }

    /**
     * Refresh token endpoint. Request a new access token from OAuth2 server
     *
     * @return A JWT token on success error message otherwise
     */
    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> refresh() {
        // @formatter:off
        String refreshToken = RestUtility.getRefershTokenFromSession(session);

        // params
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("grant_type", "refresh_token");
        params.set("refresh_token", refreshToken);

        // headers
        MultiValueMap<String, String> headers = setHttpHeaders();

        // call
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);

        // send back response
        return storeToken(session, response);
        // @formatter:on
    }

    /**
     * Set HTTP headers.
     *
     * @return A map of headers.
     */
    private MultiValueMap<String, String> setHttpHeaders() {
        final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, basicAuth(client, secret));
        return headers;
    }

    /**
     * Build a HTTP Basic Authorization header value.
     *
     * @param username User name
     * @param password Password
     * @return HTTP Basic Authorization header value
     */
    private String basicAuth(final String username, final String password) {
        final String auth = String.join(":", username, password);
        final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return String.join(" ", "Basic", new String(encodedAuth, StandardCharsets.UTF_8));
    }

    /**
     * Store token in session
     *
     * @param session  HttpSession object.
     * @param response Response entity.
     * @return New response entity
     */
    private ResponseEntity<String> storeToken(HttpSession session, ResponseEntity<String> response) {
        // @formatter:off
        final HttpStatus status = response.getStatusCode();
        final String entity = response.getBody();
        if (status == HttpStatus.OK) {
            final JSONObject fieldsJson = new JSONObject(entity);
            session.setAttribute(ACCESS_TOKEN, fieldsJson.get("access_token"));
            session.setAttribute(REFRESH_TOKEN, fieldsJson.get("refresh_token"));
            session.setAttribute(TOKEN_TYPE, fieldsJson.get("token_type"));
            session.setAttribute(AUTHORIZATION,
                    String.join(" ",
                            fieldsJson.get("token_type").toString(),
                            fieldsJson.get("access_token").toString()));
            return new ResponseEntity<String>("{\"message\": \"OK\"}", status);
        }
        return RestUtility.clone(response);
        // @formatter:on
    }
}
