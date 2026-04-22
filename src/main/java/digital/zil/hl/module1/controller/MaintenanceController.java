package digital.zil.hl.module1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import digital.zil.hl.module1.repository.BookingRepository;
import digital.zil.hl.module1.repository.FlightRepository;
import digital.zil.hl.module1.repository.PassengerRepository;

/**
 * Учебный служебный endpoint для LAB5.
 * Нужен, чтобы перед нагрузочным тестом очистить данные и заново заполнить сервис скриптом.
 */
@RestController
public class MaintenanceController {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;

    public MaintenanceController(
            BookingRepository bookingRepository,
            PassengerRepository passengerRepository,
            FlightRepository flightRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.flightRepository = flightRepository;
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear() {
        bookingRepository.clear();
        passengerRepository.clear();
        flightRepository.clear();
        return ResponseEntity.noContent().build();
    }
}
