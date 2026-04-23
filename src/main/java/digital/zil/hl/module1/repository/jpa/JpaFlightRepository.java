package digital.zil.hl.module1.repository.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import digital.zil.hl.module1.controller.exception.AirlineException;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.repository.FlightRepository;
import digital.zil.hl.module1.repository.jpa.springdata.SpringDataFlightJpaRepository;

import java.time.LocalDate;
import java.util.List;

import static java.lang.String.format;

/**
 * PostgreSQL-репозиторий для рейсов.
 * Прячет Spring Data JPA за общим интерфейсом FlightRepository.
 */
@Profile("postgres")
@Transactional(readOnly = true)
public class JpaFlightRepository implements FlightRepository {

    private static final String FLIGHT_NOT_FOUND_MSG = "Рейс с ID %d не найден";
    private static final String FLIGHT_NOT_FOUND_NUMBER_MSG = "Рейс с номером '%s' не найден";
    private static final String FLIGHT_ALREADY_EXISTS_MSG = "Рейс с номером '%s' уже существует";

    private final SpringDataFlightJpaRepository repository;

    public JpaFlightRepository(SpringDataFlightJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Flight> findAll() {
        return repository.findAll();
    }

    @Override
    public Flight findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AirlineException(format(FLIGHT_NOT_FOUND_MSG, id)));
    }

    @Override
    public Flight findByFlightNumber(String flightNumber) {
        return repository.findByFlightNumberIgnoreCase(flightNumber)
                .orElseThrow(() -> new AirlineException(format(FLIGHT_NOT_FOUND_NUMBER_MSG, flightNumber)));
    }

    /**
     * В PostgreSQL-режиме выборка делается через JPA:
     * по направлению,
     * по дате,
     * или сразу по двум фильтрам.
     */
    @Override
    public List<Flight> findByFilters(String destination, LocalDate departureDate) {
        boolean hasDestination = destination != null && !destination.isBlank();
        boolean hasDate = departureDate != null;

        if (hasDestination && hasDate) {
            return repository.findByDestinationIgnoreCaseAndDepartureDate(destination, departureDate);
        }
        if (hasDestination) {
            return repository.findByDestinationIgnoreCase(destination);
        }
        return repository.findByDepartureDate(departureDate);
    }

    @Override
    @Transactional
    public Flight save(Flight flight) {
        if (repository.existsByFlightNumberIgnoreCase(flight.getFlightNumber())) {
            throw new AirlineException(format(FLIGHT_ALREADY_EXISTS_MSG, flight.getFlightNumber()));
        }
        return repository.save(flight);
    }

    @Override
    @Transactional
    public Flight update(Long id, Flight flight) {
        if (!repository.existsById(id)) {
            throw new AirlineException(format(FLIGHT_NOT_FOUND_MSG, id));
        }
        if (repository.existsByFlightNumberIgnoreCaseAndIdNot(flight.getFlightNumber(), id)) {
            throw new AirlineException(format(FLIGHT_ALREADY_EXISTS_MSG, flight.getFlightNumber()));
        }

        flight.setId(id);
        return repository.save(flight);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new AirlineException(format(FLIGHT_NOT_FOUND_MSG, id));
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void clear() {
        repository.deleteAll();
    }
}
