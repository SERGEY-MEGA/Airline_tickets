package digital.zil.hl.module1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import digital.zil.hl.module1.controller.exception.AirlineException;
import digital.zil.hl.module1.model.Passenger;
import digital.zil.hl.module1.repository.PassengerRepository;

import java.util.List;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;

    @Autowired
    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    public Passenger getPassengerById(Long id) {
        return passengerRepository.findById(id);
    }

    public Passenger savePassenger(Passenger passenger) {
        validatePassenger(passenger);
        return passengerRepository.save(passenger);
    }

    public Passenger updatePassenger(Long id, Passenger passenger) {
        validatePassenger(passenger);
        return passengerRepository.update(id, passenger);
    }

    public void deletePassenger(Long id) {
        passengerRepository.delete(id);
    }

    private void validatePassenger(Passenger passenger) {
        if (passenger == null) {
            throw new AirlineException("Данные пассажира обязательны");
        }
        if (!hasText(passenger.getFullName())) {
            throw new AirlineException("ФИО пассажира не должно быть пустым");
        }
        if (!hasText(passenger.getPassportData())) {
            throw new AirlineException("Паспортные данные обязательны");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
