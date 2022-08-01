package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
@EnableJpaAuditing
public class InflearnSpringDataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(InflearnSpringDataJpaApplication.class, args);
    }

    /**
     * 예제라 임의의 ID를 입력하도록 함
     * 실제 서비스에서는 현재 세션의 User ID를 넣도록 구현하면됨
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
