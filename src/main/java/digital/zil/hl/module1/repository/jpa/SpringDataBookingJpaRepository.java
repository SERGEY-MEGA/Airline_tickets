package digital.zil.hl.module1.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import digital.zil.hl.module1.model.Booking;

import java.util.List;

public interface SpringDataBookingJpaRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByFlightId(Long flightId);

    long countByFlightId(Long flightId);

    boolean existsByFlightIdAndSeatIgnoreCase(Long flightId, String seat);
}
