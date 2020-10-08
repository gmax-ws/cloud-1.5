package scalable.solutions.oborder.controllers;

import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import scalable.solutions.oborder.utils.RestUtils;

@RestController
public class OrderController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private HttpSession session;

	@Value("${micro-services.gateway}")
	private String gateway;

	@Value("${micro-services.order-service}")
	private String orderService;

	@RequestMapping(value = "/orders", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> orders() throws JSONException {
		String token = RestUtils.getAccessTokenFromSession(session);

		// @formatter:off
		// headers
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, token);
		// call
		String uri = String.join("/", gateway, orderService, "orders");
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
		// send back response
		return RestUtils.clone(response);
		// @formatter:on
	}

	@RequestMapping(value = "/order/{orderId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> order(@PathVariable String orderId) throws JSONException {
		String token = RestUtils.getAccessTokenFromSession(session);

		// @formatter:off
		// headers
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, token);
		// call
		String uri = String.join("/", gateway, orderService, "order", orderId);
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
		// send back response
		return RestUtils.clone(response);
		// @formatter:on
	}

	@RequestMapping(value = "/order", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> create(@RequestBody(required = true) String order) throws JSONException {
		String token = RestUtils.getAccessTokenFromSession(session);

		// @formatter:off
		// headers
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, token);
		// call
		String uri = String.join("/", gateway, orderService, "order");
		HttpEntity<String> httpEntity = new HttpEntity<>(order, headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
		// send back response
		return RestUtils.clone(response);
		// @formatter:on
	}

	@RequestMapping(value = "/order", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<String> update(@RequestBody(required = true) String order) throws JSONException {
		String token = RestUtils.getAccessTokenFromSession(session);

		// @formatter:off
		// headers
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, token);
		// call
		String uri = String.join("/", gateway, orderService, "order");
		HttpEntity<String> httpEntity = new HttpEntity<>(order, headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, String.class);
		// send back response
		return RestUtils.clone(response);
		// @formatter:on
	}

	@RequestMapping(value = "/order/{orderId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<String> delete(@PathVariable String orderId) throws JSONException {
		String token = RestUtils.getAccessTokenFromSession(session);

		// @formatter:off
		// headers
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, token);
		// call
		String uri = String.join("/", gateway, orderService, "order", orderId);
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE, httpEntity, String.class);
		// send back response
		return RestUtils.clone(response);
		// @formatter:on
	}
}
