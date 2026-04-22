package digital.zil.hl.module1.repository.jpa.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import digital.zil.hl.module1.model.Passenger;

/**
 * Spring Data JPA репозиторий для таблицы passengers.
 */
public interface SpringDataPassengerJpaRepository extends JpaRepository<Passenger, Long> {
}
