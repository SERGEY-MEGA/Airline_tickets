package digital.zil.hl.module1.repository.memory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import digital.zil.hl.module1.controller.exception.AirlineException;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.repository.FlightRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;

/**
 * In-memory репозиторий для рейсов.
 * Хранит данные в static HashMap и работает без базы данных.
 */
@Repository
@Profile("memory")
public class InMemoryFlightRepository implements FlightRepository {

    private static final String FLIGHT_NOT_FOUND_MSG = "Рейс с ID %d не найден";
    private static final String FLIGHT_NOT_FOUND_NUMBER_MSG = "Рейс с номером '%s' не найден";
    private static final String FLIGHT_ALREADY_EXISTS_MSG = "Рейс с номером '%s' уже существует";

    private static final Map<Long, Flight> FLIGHTS = new HashMap<>();
    private static final AtomicLong ID_COUNTER = new AtomicLong(1);

    /**
     * Возвращает копию списка, чтобы внешние слои не работали с внутренней коллекцией напрямую.
     */
    @Override
    public List<Flight> findAll() {
        return new ArrayList<>(FLIGHTS.values());
    }

    @Override
    public Flight findById(Long id) {
        Flight flight = FLIGHTS.get(id);
        if (flight == null) {
            throw new AirlineException(format(FLIGHT_NOT_FOUND_MSG, id));
        }
        return flight;
    }

    @Override
    public Flight findByFlightNumber(String flightNumber) {
        return FLIGHTS.values().stream()
                .filter(flight -> flight.getFlightNumber().equalsIgnoreCase(flightNumber))
                .findFirst()
                .orElseThrow(() -> new AirlineException(format(FLIGHT_NOT_FOUND_NUMBER_MSG, flightNumber)));
    }

    /**
     * В memory-режиме фильтрация выполняется вручную по данным из HashMap.
     */
    @Override
    public List<Flight> findByFilters(String destination, LocalDate departureDate) {
        List<Flight> result = new ArrayList<>();
        for (Flight flight : FLIGHTS.values()) {
            boolean matchesDestination = destination == null || destination.isBlank()
                    || flight.getDestination().equalsIgnoreCase(destination);
            boolean matchesDate = departureDate == null || flight.getDepartureDate().equals(departureDate);

            if (matchesDestination && matchesDate) {
                result.add(flight);
            }
        }
        return result;
    }

    /**
     * Сохраняет новый рейс и вручную назначает id, как это делала бы база данных.
     */
    @Override
    public Flight save(Flight flight) {
        for (Flight savedFlight : FLIGHTS.values()) {
            if (savedFlight.getFlightNumber().equalsIgnoreCase(flight.getFlightNumber())) {
                throw new AirlineException(format(FLIGHT_ALREADY_EXISTS_MSG, flight.getFlightNumber()));
            }
        }

        flight.setId(ID_COUNTER.getAndIncrement());
        FLIGHTS.put(flight.getId(), flight);
        return flight;
    }

    /**
     * Полностью заменяет данные рейса по его id.
     */
    @Override
    public Flight update(Long id, Flight flight) {
        if (!FLIGHTS.containsKey(id)) {
            throw new AirlineException(format(FLIGHT_NOT_FOUND_MSG, id));
        }

        for (Flight savedFlight : FLIGHTS.values()) {
            if (!savedFlight.getId().equals(id)
                    && savedFlight.getFlightNumber().equalsIgnoreCase(flight.getFlightNumber())) {
                throw new AirlineException(format(FLIGHT_ALREADY_EXISTS_MSG, flight.getFlightNumber()));
            }
        }

        flight.setId(id);
        FLIGHTS.put(id, flight);
        return flight;
    }

    @Override
    public void delete(Long id) {
        if (FLIGHTS.remove(id) == null) {
            throw new AirlineException(format(FLIGHT_NOT_FOUND_MSG, id));
        }
    }

    @Override
    public void clear() {
        FLIGHTS.clear();
        ID_COUNTER.set(1);
    }
}
