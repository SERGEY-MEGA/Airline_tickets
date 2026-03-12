package digital.zil.hl.module1.repository;

import digital.zil.hl.module1.model.Passenger;
import java.util.Optional;

public interface PassengerRepository {
    Passenger save(Passenger passenger);
    Optional<Passenger> findById(Long id);
}
