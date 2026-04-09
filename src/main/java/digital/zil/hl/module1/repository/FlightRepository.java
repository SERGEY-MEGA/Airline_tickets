package digital.zil.hl.module1.repository;

import digital.zil.hl.module1.model.Flight;

import java.time.LocalDate;
import java.util.List;

public interface FlightRepository {

    List<Flight> findAll();

    Flight findById(Long id);

    Flight findByFlightNumber(String flightNumber);

    List<Flight> findByDestinationAndDate(String destination, LocalDate departureDate);

    Flight save(Flight flight);

    Flight update(Long id, Flight flight);

    void delete(Long id);

    void clear();
}
