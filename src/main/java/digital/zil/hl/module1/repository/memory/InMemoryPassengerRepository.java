package digital.zil.hl.module1.repository.memory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import digital.zil.hl.module1.controller.exception.AirlineException;
import digital.zil.hl.module1.model.Passenger;
import digital.zil.hl.module1.repository.PassengerRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;

/**
 * Репозиторий LAB1 для пассажиров.
 * Данные — в {@code HashMap}; ид считаются через {@code AtomicLong}.
 */
@Repository
@Profile("lab1")
public class InMemoryPassengerRepository implements PassengerRepository {

    private static final String PASSENGER_NOT_FOUND_MSG = "Пассажир с ID %d не найден";

    private static final Map<Long, Passenger> PASSENGERS = new HashMap<>();
    private static final AtomicLong ID_COUNTER = new AtomicLong(1);

    @Override
    public List<Passenger> findAll() {
        return new ArrayList<>(PASSENGERS.values());
    }

    @Override
    public Passenger findById(Long id) {
        Passenger passenger = PASSENGERS.get(id);
        if (passenger == null) {
            throw new AirlineException(format(PASSENGER_NOT_FOUND_MSG, id));
        }
        return passenger;
    }

    @Override
    public Passenger save(Passenger passenger) {
        passenger.setId(ID_COUNTER.getAndIncrement());
        PASSENGERS.put(passenger.getId(), passenger);
        return passenger;
    }

    @Override
    public Passenger update(Long id, Passenger passenger) {
        if (!PASSENGERS.containsKey(id)) {
            throw new AirlineException(format(PASSENGER_NOT_FOUND_MSG, id));
        }
        passenger.setId(id);
        PASSENGERS.put(id, passenger);
        return passenger;
    }

    @Override
    public void delete(Long id) {
        if (PASSENGERS.remove(id) == null) {
            throw new AirlineException(format(PASSENGER_NOT_FOUND_MSG, id));
        }
    }

    @Override
    public void clear() {
        PASSENGERS.clear();
        ID_COUNTER.set(1);
    }
}
