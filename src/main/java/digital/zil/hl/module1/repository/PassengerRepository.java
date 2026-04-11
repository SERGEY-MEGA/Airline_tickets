package digital.zil.hl.module1.repository;

import digital.zil.hl.module1.model.Passenger;

import java.util.List;

/**
 * Общий контракт репозитория пассажиров.
 */
public interface PassengerRepository {

    List<Passenger> findAll();

    Passenger findById(Long id);

    Passenger save(Passenger passenger);

    Passenger update(Long id, Passenger passenger);

    void delete(Long id);

    void clear();
}
