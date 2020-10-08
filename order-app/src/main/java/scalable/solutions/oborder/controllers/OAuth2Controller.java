package scalable.solutions.oborder.controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import scalable.solutions.oborder.dto.CredentialsDTO;
import scalable.solutions.oborder.utils.RestUtils;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

	public static final String ACCESS_TOKEN = "ACCESS_TOKEN";

	public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

	public static final String TOKEN_TYPE = "TOKEN_TYPE";

	public static final String AUTHORIZATION = "AUTHORIZATION";

	@Autowired
	private Environment environment;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private HttpSession session;

	/**
	 * Login endpoint. Request an access token from OAuth2 server
	 * 
	 * @param credentials
	 *            User credentials
	 * @return A JWT token on success error message otherwise
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> login(@RequestBody(required = true) CredentialsDTO credentials) throws JSONException {

		// @formatter:off
		// get properties from configuration
		String uri = environment.getProperty("security.oauth2.client.access-token-uri");
		String client = environment.getProperty("security.oauth2.client.id");
		String secret = environment.getProperty("security.oauth2.client.client-secret");
		String grant = environment.getProperty("security.oauth2.client.grant-type");

		// params
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.set("grant_type", grant);
		params.set("username", credentials.getUsername());
		params.set("password", credentials.getPassword());
		// headers
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, basicAuth(client, secret));
		
		// call
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);

		// send back response
		return storeToken(session, response);
		// @formatter:on
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
		String refreshToken = RestUtils.getRefreshTokenFromSession(session);

		// get properties from configuration
		String uri = environment.getProperty("security.oauth2.client.access-token-uri");
		String client = environment.getProperty("security.oauth2.client.id");
		String secret = environment.getProperty("security.oauth2.client.client-secret");

		// params
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.set("grant_type", "refresh_token");
		params.set("refresh_token", refreshToken);
		
		// headers
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, basicAuth(client, secret));
		
		// call
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);

		// send back response
		return storeToken(session, response);
		// @formatter:on
	}

	/**
	 * Build a HTTP Basic Authorization header value.
	 * 
	 * @param username
	 *            User name
	 * @param password
	 *            Password
	 * @return HTTP Basic Authorization header value
	 */
	private String basicAuth(String username, String password) {
		String auth = username + ":" + password;
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		return "Basic " + new String(encodedAuth);
	}

	/**
	 * Store token in session
	 * 
	 * @param session
	 *            HttpSession object.
	 * @param response
	 *            Response entity.
	 * @return New response entity
	 */
	private ResponseEntity<String> storeToken(HttpSession session, ResponseEntity<String> response) {
		// @formatter:off
		HttpStatus status = response.getStatusCode();
		String entity = response.getBody();
		if (status == HttpStatus.OK) {
			JSONObject fieldsJson = new JSONObject(entity);
			session.setAttribute(ACCESS_TOKEN, fieldsJson.get("access_token"));
			session.setAttribute(REFRESH_TOKEN, fieldsJson.get("refresh_token"));
			session.setAttribute(TOKEN_TYPE, fieldsJson.get("token_type"));
			session.setAttribute(AUTHORIZATION,
					String.join(" ", 
							fieldsJson.get("token_type").toString(), 
							fieldsJson.get("access_token").toString()));
			return new ResponseEntity<>("{\"message\": \"OK\"}", status);
		}
		return RestUtils.clone(response);
		// @formatter:on
	}
}
