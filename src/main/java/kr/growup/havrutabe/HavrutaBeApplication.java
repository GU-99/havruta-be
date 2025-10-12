package kr.growup.havrutabe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@EnableJpaAuditing
public class HavrutaBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HavrutaBeApplication.class, args);
    }

}
