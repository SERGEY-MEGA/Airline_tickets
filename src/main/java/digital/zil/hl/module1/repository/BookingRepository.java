package digital.zil.hl.module1.repository;

import digital.zil.hl.module1.model.Booking;
import java.util.List;

public interface BookingRepository {
    Booking save(Booking booking);
    List<Booking> findByFlightId(Long flightId);
    boolean existsByFlightIdAndSeat(Long flightId, String seat);
    long countByFlightId(Long flightId);
    List<Booking> findAll();
}
