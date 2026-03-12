package digital.zil.hl.module1.repository;

import digital.zil.hl.module1.model.Flight;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FlightRepository {
    Flight save(Flight flight);
    Optional<Flight> findById(Long id);
    List<Flight> findAll();
    List<Flight> findByDestinationAndDepartureDate(String destination, LocalDate date);
}
