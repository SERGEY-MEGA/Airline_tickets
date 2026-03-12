package digital.zil.hl.module1.repository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import digital.zil.hl.module1.controller.exeption.AirlineException;
import digital.zil.hl.module1.model.Passenger;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;

@Repository
public class PassengerRepository {

    public static final String PASSENGER_NOT_FOUND_MSG = "Пассажир с ID %d не найден";

    private final Map<Long, Passenger> passengers = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Passenger> findAll() {
        return new ArrayList<>(passengers.values());
    }

    public Passenger findById(Long id) {
        final var passenger = passengers.get(id);
        if (passenger == null) {
            throw new AirlineException(format(PASSENGER_NOT_FOUND_MSG, id));
        }
        return passenger;
    }

    public Passenger save(@NonNull Passenger passenger) {
        passenger.setId(idCounter.getAndIncrement());
        passengers.put(passenger.getId(), passenger);
        return passenger;
    }

    public Passenger put(Long id, @NonNull Passenger updated) {
        if (!passengers.containsKey(id)) {
            throw new AirlineException(format(PASSENGER_NOT_FOUND_MSG, id));
        }
        updated.setId(id);
        passengers.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        if (passengers.remove(id) == null) {
            throw new AirlineException(format(PASSENGER_NOT_FOUND_MSG, id));
        }
    }

    public void clear() {
        passengers.clear();
    }
}
