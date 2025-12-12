package bienchen.summherum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching; // <- wichtiger Import

@SpringBootApplication
@EnableCaching // <- Das schaltet den Cache-Mechanismus grundlegend an
public class SummHerumApplication {

	public static void main(String[] args) {
		SpringApplication.run(SummHerumApplication.class, args);
	}

}
