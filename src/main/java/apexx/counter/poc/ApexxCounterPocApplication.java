package apexx.counter.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ApexxCounterPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApexxCounterPocApplication.class, args);
	}

}
