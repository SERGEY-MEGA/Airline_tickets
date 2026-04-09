package digital.zil.hl.module1.repository.jpa;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import digital.zil.hl.module1.model.Flight;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataFlightJpaRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByFlightNumberIgnoreCase(String flightNumber);

    boolean existsByFlightNumberIgnoreCase(String flightNumber);

    boolean existsByFlightNumberIgnoreCaseAndIdNot(String flightNumber, Long id);

    List<Flight> findByDestinationIgnoreCaseAndDepartureDate(String destination, LocalDate departureDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select flight from Flight flight where flight.id = :id")
    Optional<Flight> findByIdForUpdate(@Param("id") Long id);
}
