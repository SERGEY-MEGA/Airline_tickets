package digital.zil.hl.module1.repository.jpa.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import digital.zil.hl.module1.model.Booking;

import java.util.List;

/**
 * Spring Data JPA репозиторий для таблицы bookings.
 */
public interface SpringDataBookingJpaRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByFlightId(Long flightId);

    long countByFlightId(Long flightId);

    boolean existsByFlightIdAndSeatIgnoreCase(Long flightId, String seat);
}
