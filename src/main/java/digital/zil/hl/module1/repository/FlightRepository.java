package digital.zil.hl.module1.repository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import digital.zil.hl.module1.controller.exeption.AirlineException;
import digital.zil.hl.module1.model.Flight;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Repository
public class FlightRepository {

    public static final String FLIGHT_NOT_FOUND_MSG          = "Рейс с ID %d не найден";
    public static final String FLIGHT_NOT_FOUND_NUMBER_MSG  = "Рейс с номером '%s' не найден";
    public static final String FLIGHT_ALREADY_EXISTS_MSG    = "Рейс с номером '%s' уже существует";

    private final Map<Long, Flight> flights = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Flight> findAll() {
        return new ArrayList<>(flights.values());
    }

    public Flight findById(Long id) {
        final var flight = flights.get(id);
        if (flight == null) {
            throw new AirlineException(format(FLIGHT_NOT_FOUND_MSG, id));
        }
        return flight;
    }

    /** Поиск рейса по номеру (например, "SU301") */
    public Flight findByFlightNumber(@NonNull String flightNumber) {
        return flights.values().stream()
                .filter(f -> f.getFlightNumber().equalsIgnoreCase(flightNumber))
                .findFirst()
                .orElseThrow(() -> new AirlineException(format(FLIGHT_NOT_FOUND_NUMBER_MSG, flightNumber)));
    }

    /** Поиск рейсов по направлению и дате — для отображения остатка мест */
    public List<Flight> findByDestinationAndDate(@NonNull String destination, @NonNull LocalDate date) {
        return flights.values().stream()
                .filter(f -> f.getDestination().equalsIgnoreCase(destination) && f.getDepartureDate().equals(date))
                .collect(Collectors.toList());
    }

    public Flight save(@NonNull Flight flight) {
        final boolean numberTaken = flights.values().stream()
                .anyMatch(f -> f.getFlightNumber().equalsIgnoreCase(flight.getFlightNumber()));
        if (numberTaken) {
            throw new AirlineException(format(FLIGHT_ALREADY_EXISTS_MSG, flight.getFlightNumber()));
        }
        flight.setId(idCounter.getAndIncrement());
        flights.put(flight.getId(), flight);
        return flight;
    }

    /**
     * Полное обновление рейса (PUT).
     * Позволяет изменить любое поле, например номер рейса с SU100 на SU200.
     * Запрос: PUT /flights/{id} с телом { "flightNumber": "SU200", ... }
     */
    public Flight put(Long id, @NonNull Flight updated) {
        if (!flights.containsKey(id)) {
            throw new AirlineException(format(FLIGHT_NOT_FOUND_MSG, id));
        }
        updated.setId(id);
        flights.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        if (flights.remove(id) == null) {
            throw new AirlineException(format(FLIGHT_NOT_FOUND_MSG, id));
        }
    }

    public void clear() {
        flights.clear();
    }
}
