package counter.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CounterPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(CounterPocApplication.class, args);
	}

}
