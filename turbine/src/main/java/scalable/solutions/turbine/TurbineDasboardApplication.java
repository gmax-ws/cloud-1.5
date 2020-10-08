package scalable.solutions.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * Turbine Dashboard. Collect Hystrix streams from configured services and provide a centralized dashboard.
 * 
 * @author Marius
 *
 */
@EnableHystrixDashboard
@EnableTurbine
@SpringBootApplication
public class TurbineDasboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurbineDasboardApplication.class, args);
	}
}
