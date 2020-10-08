package scalable.solutions.rest.controlers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/test")
public class TestRibbonController {

	@Value("${server.port}")
	int port;

	@RequestMapping(path = "/delay", method = RequestMethod.GET)
	public ResponseEntity<String> delay() {
		if (port != 9000) {
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new ResponseEntity<>("OK port:" + port, HttpStatus.OK);
	}

	@RequestMapping(path = "/exception", method = RequestMethod.GET)
	public ResponseEntity<String> exception() {
		if (port == 9000)
			return new ResponseEntity<>("OK port:" + port, HttpStatus.OK);
		throw new RuntimeException("Service unavailable port:" + port);
		// return new ResponseEntity<String>("Service unavailable port:" + port,
		// HttpStatus.SERVICE_UNAVAILABLE);
	}

	@RequestMapping(path = "/params", method = RequestMethod.GET)
	public String test(@RequestParam("name") String name) {
		return "Hello " + name;
	}
}
