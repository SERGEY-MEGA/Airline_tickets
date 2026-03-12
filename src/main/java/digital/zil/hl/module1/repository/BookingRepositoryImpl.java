package digital.zil.hl.module1.repository;

import digital.zil.hl.module1.model.Booking;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class BookingRepositoryImpl implements BookingRepository {
    private static final Map<Long, Booking> bookings = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(idGenerator.getAndIncrement());
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public List<Booking> findByFlightId(Long flightId) {
        return bookings.values().stream()
                .filter(b -> b.getFlightId().equals(flightId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByFlightIdAndSeat(Long flightId, String seat) {
        return bookings.values().stream()
                .anyMatch(b -> b.getFlightId().equals(flightId) && b.getSeat().equalsIgnoreCase(seat));
    }

    @Override
    public long countByFlightId(Long flightId) {
        return bookings.values().stream()
                .filter(b -> b.getFlightId().equals(flightId))
                .count();
    }
    
    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }
}
