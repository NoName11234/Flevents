package de.flyndre.fleventsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
/**
 * This is the Main-Class of the Application.
 * @author Ruben Kraft
 * @version $I$
 */
@SpringBootApplication
public class FleventsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleventsBackendApplication.class, args);
	}

}
