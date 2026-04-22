package digital.zil.hl.module1.repository.jpa.springdata;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import digital.zil.hl.module1.model.Flight;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Низкоуровневый Spring Data JPA репозиторий для таблицы flights.
 * Здесь объявлены SQL-подобные методы поиска без ручного написания SQL.
 */
public interface SpringDataFlightJpaRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByFlightNumberIgnoreCase(String flightNumber);

    boolean existsByFlightNumberIgnoreCase(String flightNumber);

    boolean existsByFlightNumberIgnoreCaseAndIdNot(String flightNumber, Long id);

    /**
     * Поиск рейсов только по направлению.
     */
    List<Flight> findByDestinationIgnoreCase(String destination);

    /**
     * Поиск рейсов только по дате вылета.
     */
    List<Flight> findByDepartureDate(LocalDate departureDate);

    /**
     * Поиск рейсов сразу по направлению и дате.
     */
    List<Flight> findByDestinationIgnoreCaseAndDepartureDate(String destination, LocalDate departureDate);

    /**
     * Берёт блокировку на строку рейса во время бронирования.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select flight from Flight flight where flight.id = :id")
    Optional<Flight> findByIdForUpdate(@Param("id") Long id);
}
