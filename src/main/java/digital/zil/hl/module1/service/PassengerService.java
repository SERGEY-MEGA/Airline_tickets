package digital.zil.hl.module1.service;

import digital.zil.hl.module1.model.Passenger;
import digital.zil.hl.module1.repository.PassengerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PassengerService {
    private final PassengerRepository passengerRepository;

    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public Passenger registerPassenger(Passenger passenger) {
        return passengerRepository.save(passenger);
    }

    public Optional<Passenger> getPassenger(Long id) {
        return passengerRepository.findById(id);
    }
}
