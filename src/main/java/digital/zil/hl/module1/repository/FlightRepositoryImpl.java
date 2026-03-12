package digital.zil.hl.module1.repository;

import digital.zil.hl.module1.model.Flight;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class FlightRepositoryImpl implements FlightRepository {
    private static final Map<Long, Flight> flights = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Flight save(Flight flight) {
        if (flight.getId() == null) {
            flight.setId(idGenerator.getAndIncrement());
        }
        flights.put(flight.getId(), flight);
        return flight;
    }

    @Override
    public Optional<Flight> findById(Long id) {
        return Optional.ofNullable(flights.get(id));
    }

    @Override
    public List<Flight> findAll() {
        return new ArrayList<>(flights.values());
    }

    @Override
    public List<Flight> findByDestinationAndDepartureDate(String destination, LocalDate date) {
        return flights.values().stream()
                .filter(f -> f.getDestination().equalsIgnoreCase(destination) && f.getDepartureDate().equals(date))
                .collect(Collectors.toList());
    }
}
