package scalable.solutions.rest.exceptions;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * A Global Exception Handler. Mandatory for all microservices.
 * 
 * @author Marius
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(OrderNotFoundException.class)
	public Map<String, String> handleOrderNotFoundException(HttpServletRequest request, OrderNotFoundException ex) {
		Map<String, String> resp = new LinkedHashMap<>();
		resp.put("url", request.getRequestURL().toString());
		resp.put("error-message", ex.getLocalizedMessage());
		return resp;
	}
}
