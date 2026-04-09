package digital.zil.hl.module1.repository;

import digital.zil.hl.module1.model.Booking;

import java.util.List;

public interface BookingRepository {

    List<Booking> findAll();

    Booking findById(Long id);

    List<Booking> findByFlightId(Long flightId);

    long countByFlightId(Long flightId);

    boolean isSeatTaken(Long flightId, String seat);

    Booking save(Booking booking, int flightCapacity);

    void delete(Long id);

    void clear();
}
