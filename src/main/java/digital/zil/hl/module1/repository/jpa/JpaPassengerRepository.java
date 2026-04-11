package digital.zil.hl.module1.repository.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import digital.zil.hl.module1.controller.exception.AirlineException;
import digital.zil.hl.module1.model.Passenger;
import digital.zil.hl.module1.repository.PassengerRepository;

import java.util.List;

import static java.lang.String.format;

/**
 * Репозиторий LAB2 для пассажиров.
 */
@Repository
@Profile({"lab2", "lab3"})
@Transactional(readOnly = true)
public class JpaPassengerRepository implements PassengerRepository {

    private static final String PASSENGER_NOT_FOUND_MSG = "Пассажир с ID %d не найден";

    private final SpringDataPassengerJpaRepository repository;

    public JpaPassengerRepository(SpringDataPassengerJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Passenger> findAll() {
        return repository.findAll();
    }

    @Override
    public Passenger findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AirlineException(format(PASSENGER_NOT_FOUND_MSG, id)));
    }

    @Override
    @Transactional
    public Passenger save(Passenger passenger) {
        return repository.save(passenger);
    }

    @Override
    @Transactional
    public Passenger update(Long id, Passenger passenger) {
        if (!repository.existsById(id)) {
            throw new AirlineException(format(PASSENGER_NOT_FOUND_MSG, id));
        }

        passenger.setId(id);
        return repository.save(passenger);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new AirlineException(format(PASSENGER_NOT_FOUND_MSG, id));
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void clear() {
        repository.deleteAll();
    }
}
