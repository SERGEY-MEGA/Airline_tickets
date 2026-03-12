package digital.zil.hl.module1.repository;

import digital.zil.hl.module1.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PassengerRepositoryImpl implements PassengerRepository {
    private static final Map<Long, Passenger> passengers = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Passenger save(Passenger passenger) {
        if (passenger.getId() == null) {
            passenger.setId(idGenerator.getAndIncrement());
        }
        passengers.put(passenger.getId(), passenger);
        return passenger;
    }

    @Override
    public Optional<Passenger> findById(Long id) {
        return Optional.ofNullable(passengers.get(id));
    }
}
