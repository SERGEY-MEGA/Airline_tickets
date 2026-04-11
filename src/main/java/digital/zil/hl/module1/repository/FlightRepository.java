package digital.zil.hl.module1.repository;

import digital.zil.hl.module1.model.Flight;

import java.time.LocalDate;
import java.util.List;

/**
 * Общий контракт репозитория рейсов.
 * Благодаря интерфейсу сервису всё равно, где лежат данные: в HashMap или в PostgreSQL.
 */
public interface FlightRepository {

    List<Flight> findAll();

    Flight findById(Long id);

    Flight findByFlightNumber(String flightNumber);

    /**
     * Ищет рейсы по гибким фильтрам.
     * Можно передать только destination, только departureDate или оба параметра сразу.
     */
    List<Flight> findByFilters(String destination, LocalDate departureDate);

    Flight save(Flight flight);

    Flight update(Long id, Flight flight);

    void delete(Long id);

    void clear();
}
