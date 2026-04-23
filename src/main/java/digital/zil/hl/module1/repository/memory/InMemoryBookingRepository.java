package digital.zil.hl.module1.repository.memory;

import org.springframework.context.annotation.Profile;
import digital.zil.hl.module1.controller.exception.AirlineException;
import digital.zil.hl.module1.model.Booking;
import digital.zil.hl.module1.repository.BookingRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;

/**
 * In-memory репозиторий для бронирований.
 * Здесь же защищаемся от двойного бронирования места и переполнения рейса.
 */
@Profile("memory")
public class InMemoryBookingRepository implements BookingRepository {

    private static final String BOOKING_NOT_FOUND_MSG = "Бронирование с ID %d не найдено";
    private static final String SEAT_ALREADY_BOOKED_MSG = "Место '%s' на рейсе ID %d уже занято";
    private static final String FLIGHT_FULLY_BOOKED_MSG = "Рейс ID %d заполнен. Вместимость: %d";

    private static final Map<Long, Booking> BOOKINGS = new HashMap<>();
    private static final AtomicLong ID_COUNTER = new AtomicLong(1);

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(BOOKINGS.values());
    }

    @Override
    public Booking findById(Long id) {
        Booking booking = BOOKINGS.get(id);
        if (booking == null) {
            throw new AirlineException(format(BOOKING_NOT_FOUND_MSG, id));
        }
        return booking;
    }

    @Override
    public List<Booking> findByFlightId(Long flightId) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : BOOKINGS.values()) {
            if (booking.getFlightId().equals(flightId)) {
                result.add(booking);
            }
        }
        return result;
    }

    @Override
    public long countByFlightId(Long flightId) {
        long count = 0;
        for (Booking booking : BOOKINGS.values()) {
            if (booking.getFlightId().equals(flightId)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean isSeatTaken(Long flightId, String seat) {
        for (Booking booking : BOOKINGS.values()) {
            if (booking.getFlightId().equals(flightId) && booking.getSeat().equalsIgnoreCase(seat)) {
                return true;
            }
        }
        return false;
    }

    /**
     * synchronized нужен, чтобы два одновременных запроса не заняли одно и то же место.
     */
    @Override
    public synchronized Booking save(Booking booking, int flightCapacity) {
        if (countByFlightId(booking.getFlightId()) >= flightCapacity) {
            throw new AirlineException(format(FLIGHT_FULLY_BOOKED_MSG, booking.getFlightId(), flightCapacity));
        }
        if (isSeatTaken(booking.getFlightId(), booking.getSeat())) {
            throw new AirlineException(format(SEAT_ALREADY_BOOKED_MSG, booking.getSeat(), booking.getFlightId()));
        }

        booking.setId(ID_COUNTER.getAndIncrement());
        BOOKINGS.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public void delete(Long id) {
        if (BOOKINGS.remove(id) == null) {
            throw new AirlineException(format(BOOKING_NOT_FOUND_MSG, id));
        }
    }

    @Override
    public void clear() {
        BOOKINGS.clear();
        ID_COUNTER.set(1);
    }
}
