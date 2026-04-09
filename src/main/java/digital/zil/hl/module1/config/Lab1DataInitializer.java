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

@Component
@Profile("lab1")
public class Lab1DataInitializer implements CommandLineRunner {

    private final FlightService flightService;
    private final PassengerService passengerService;
    private final BookingService bookingService;

    public Lab1DataInitializer(
            FlightService flightService,
            PassengerService passengerService,
            BookingService bookingService
    ) {
        this.flightService = flightService;
        this.passengerService = passengerService;
        this.bookingService = bookingService;
    }

    @Override
    public void run(String... args) {
        if (!flightService.getAllFlights().isEmpty()) {
            return;
        }

        Flight moscowFlight = flightService.saveFlight(
                new Flight(null, "SU100", "Moscow", LocalDate.of(2026, 5, 10), 5)
        );
        Flight kazanFlight = flightService.saveFlight(
                new Flight(null, "DP200", "Kazan", LocalDate.of(2026, 5, 10), 3)
        );
        Flight sochiFlight = flightService.saveFlight(
                new Flight(null, "S7200", "Sochi", LocalDate.of(2026, 5, 11), 4)
        );

        Passenger ivanov = passengerService.savePassenger(
                new Passenger(null, "Иван Иванов", "1234 567890", "ivanov@example.com")
        );
        Passenger petrova = passengerService.savePassenger(
                new Passenger(null, "Анна Петрова", "4321 098765", "+7-999-111-22-33")
        );
        Passenger sidorov = passengerService.savePassenger(
                new Passenger(null, "Пётр Сидоров", "7777 111222", "sidorov@example.com")
        );

        bookingService.saveBooking(
                new Booking(null, moscowFlight.getId(), ivanov.getId(), ServiceClass.ECONOMY, "1A")
        );
        bookingService.saveBooking(
                new Booking(null, kazanFlight.getId(), petrova.getId(), ServiceClass.BUSINESS, "2B")
        );
        bookingService.saveBooking(
                new Booking(null, sochiFlight.getId(), sidorov.getId(), ServiceClass.ECONOMY, "3C")
        );
    }
}
