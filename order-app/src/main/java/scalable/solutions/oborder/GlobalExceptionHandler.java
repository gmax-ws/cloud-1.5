package scalable.solutions.oborder;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import scalable.solutions.oborder.exceptions.UnauthorizedException;

/**
 * A Global Exception Handler. Mandatory for all microservices.
 * 
 * @author Marius
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(UnauthorizedException.class)
	public Map<String, String> handleOrderNotFoundException(HttpServletRequest request, UnauthorizedException ex) {
		Map<String, String> resp = new LinkedHashMap<>();
		resp.put("path", request.getRequestURL().toString());
		resp.put("message", ex.getLocalizedMessage());
		return resp;
	}

	@ExceptionHandler(HttpStatusCodeException.class)
	public ResponseEntity<String> handleHttpStatusCodeException(HttpStatusCodeException ex) {
		return new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
	}
}
