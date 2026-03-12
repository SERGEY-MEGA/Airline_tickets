package digital.zil.hl.module1.repository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import digital.zil.hl.module1.controller.exeption.AirlineException;
import digital.zil.hl.module1.model.Booking;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Repository
public class BookingRepository {

    public static final String BOOKING_NOT_FOUND_MSG   = "Бронирование с ID %d не найдено";
    public static final String SEAT_ALREADY_BOOKED_MSG = "Место '%s' на рейс ID %d уже занято";
    public static final String FLIGHT_FULLY_BOOKED_MSG = "Рейс ID %d заполнен. Вместимость: %d";

    private final Map<Long, Booking> bookings = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    public Booking findById(Long id) {
        final var booking = bookings.get(id);
        if (booking == null) {
            throw new AirlineException(format(BOOKING_NOT_FOUND_MSG, id));
        }
        return booking;
    }

    public List<Booking> findByFlightId(Long flightId) {
        return bookings.values().stream()
                .filter(b -> b.getFlightId().equals(flightId))
                .collect(Collectors.toList());
    }

    public long countByFlightId(Long flightId) {
        return bookings.values().stream()
                .filter(b -> b.getFlightId().equals(flightId))
                .count();
    }

    public boolean isSeatTaken(Long flightId, String seat) {
        return bookings.values().stream()
                .anyMatch(b -> b.getFlightId().equals(flightId) && b.getSeat().equalsIgnoreCase(seat));
    }

    /**
     * Бронирование места. Метод synchronized — ключ к потокобезопасности:
     * Проверка свободных мест И проверка занятости конкретного кресла
     * выполняются атомарно, пока другие потоки ждут очереди.
     * Это аналог транзакции SELECT + INSERT в реляционной БД.
     */
    public synchronized Booking save(@NonNull Booking booking, int flightCapacity) {
        // Проверка 1: вместимость рейса
        if (countByFlightId(booking.getFlightId()) >= flightCapacity) {
            throw new AirlineException(format(FLIGHT_FULLY_BOOKED_MSG, booking.getFlightId(), flightCapacity));
        }
        // Проверка 2: место не занято
        if (isSeatTaken(booking.getFlightId(), booking.getSeat())) {
            throw new AirlineException(format(SEAT_ALREADY_BOOKED_MSG, booking.getSeat(), booking.getFlightId()));
        }
        booking.setId(idCounter.getAndIncrement());
        bookings.put(booking.getId(), booking);
        return booking;
    }

    public void delete(Long id) {
        if (bookings.remove(id) == null) {
            throw new AirlineException(format(BOOKING_NOT_FOUND_MSG, id));
        }
    }

    public void clear() {
        bookings.clear();
    }
}
