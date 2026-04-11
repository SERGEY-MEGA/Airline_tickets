package digital.zil.hl.module1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в Spring Boot приложение.
 * Отсюда запускается весь backend: контроллеры, сервисы и репозитории.
 */
@SpringBootApplication
public class Application {

    /**
     * Запускает Spring-контекст и поднимает REST API.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
