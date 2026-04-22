package digital.zil.hl.module1.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import digital.zil.hl.module1.model.Booking;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.model.Passenger;
import digital.zil.hl.module1.model.ServiceClass;
import digital.zil.hl.module1.service.BookingService;
import digital.zil.hl.module1.service.FlightService;
import digital.zil.hl.module1.service.PassengerService;

import java.time.LocalDate;

/**
 * Заполняет in-memory режим тестовыми данными при старте.
 * В PostgreSQL такие данные создаются миграцией Flyway.
 */
@Component
@Profile("memory")
public class MemoryDataInitializer implements CommandLineRunner {

    private final FlightService flightService;
    private final PassengerService passengerService;
    private final BookingService bookingService;

    public MemoryDataInitializer(
            FlightService flightService,
            PassengerService passengerService,
            BookingService bookingService
    ) {
        this.flightService = flightService;
        this.passengerService = passengerService;
        this.bookingService = bookingService;
    }

    /**
     * Создаёт стартовые рейсы, пассажиров и бронирования один раз при запуске.
     * Набор совпадает с Flyway V2, чтобы Postman и curl работали в любом режиме.
     */
    @Override
    public void run(String... args) {
        // Если данные уже есть, повторно ничего не создаём.
        if (!flightService.getAllFlights().isEmpty()) {
            return;
        }

        Flight moscowFlight = flightService.saveFlight(
                new Flight(null, "SU500", "Moscow", LocalDate.of(2026, 6, 10), 6)
        );
        Flight spbFlight = flightService.saveFlight(
                new Flight(null, "DP610", "Saint Petersburg", LocalDate.of(2026, 6, 10), 4)
        );
        Flight sochiFlight = flightService.saveFlight(
                new Flight(null, "S7200", "Sochi", LocalDate.of(2026, 6, 11), 5)
        );

        Passenger ivanov = passengerService.savePassenger(
                new Passenger(null, "Иван Иванов", "1234 567890", "ivanov@example.com")
        );
        Passenger petrova = passengerService.savePassenger(
                new Passenger(null, "Анна Петрова", "4321 987654", "+7-999-123-45-67")
        );
        Passenger sidorov = passengerService.savePassenger(
                new Passenger(null, "Пётр Сидоров", "7777 111222", "sidorov@example.com")
        );

        bookingService.saveBooking(
                new Booking(null, moscowFlight.getId(), ivanov.getId(), ServiceClass.ECONOMY, "1A")
        );
        bookingService.saveBooking(
                new Booking(null, spbFlight.getId(), petrova.getId(), ServiceClass.BUSINESS, "2B")
        );
        bookingService.saveBooking(
                new Booking(null, sochiFlight.getId(), sidorov.getId(), ServiceClass.ECONOMY, "3C")
        );
    }
}
