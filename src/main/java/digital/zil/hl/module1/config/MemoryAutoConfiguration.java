package digital.zil.hl.module1.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Режим memory работает без базы данных.
 * Отключает JDBC и JPA, чтобы приложение использовало in-memory репозитории.
 */
@Configuration
@Profile("memory")
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        DataJpaRepositoriesAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class MemoryAutoConfiguration {
}
