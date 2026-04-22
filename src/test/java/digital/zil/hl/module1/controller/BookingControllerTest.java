package digital.zil.hl.module1.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import digital.zil.hl.module1.Application;
import digital.zil.hl.module1.model.Booking;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.model.Passenger;
import digital.zil.hl.module1.model.ServiceClass;
import digital.zil.hl.module1.repository.BookingRepository;
import digital.zil.hl.module1.repository.FlightRepository;
import digital.zil.hl.module1.repository.PassengerRepository;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("memory")
public class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    public void init() {
        bookingRepository.clear();
        passengerRepository.clear();
        flightRepository.clear();
    }

    @Test
    public void saveBooking_should_returnCreatedBooking() throws Exception {
        Flight flight = flightRepository.save(new Flight(null, "SU300", "Moscow", LocalDate.of(2026, 6, 1), 2));
        Passenger passenger = passengerRepository.save(
                new Passenger(null, "Иван Иванов", "1111 222333", "ivanov@example.com")
        );

        String json = """
                {
                  "flightId": %d,
                  "passengerId": %d,
                  "serviceClass": "ECONOMY",
                  "seat": "1A"
                }
                """.formatted(flight.getId(), passenger.getId());

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flightId").value(flight.getId()))
                .andExpect(jsonPath("$.passengerId").value(passenger.getId()))
                .andExpect(jsonPath("$.serviceClass").value("ECONOMY"))
                .andExpect(jsonPath("$.seat").value("1A"));
    }

    @Test
    public void getBookings_should_filterByFlightId() throws Exception {
        Flight firstFlight = flightRepository.save(new Flight(null, "SU301", "Kazan", LocalDate.of(2026, 6, 1), 3));
        Flight secondFlight = flightRepository.save(new Flight(null, "SU302", "Sochi", LocalDate.of(2026, 6, 2), 3));

        Passenger firstPassenger = passengerRepository.save(
                new Passenger(null, "Пассажир Один", "1000 200300", "first@example.com")
        );
        Passenger secondPassenger = passengerRepository.save(
                new Passenger(null, "Пассажир Два", "4000 500600", "second@example.com")
        );

        bookingRepository.save(
                new Booking(null, firstFlight.getId(), firstPassenger.getId(), ServiceClass.ECONOMY, "4A"),
                firstFlight.getCapacity()
        );
        bookingRepository.save(
                new Booking(null, secondFlight.getId(), secondPassenger.getId(), ServiceClass.BUSINESS, "5B"),
                secondFlight.getCapacity()
        );

        mvc.perform(get("/bookings")
                        .param("flightId", firstFlight.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].flightId").value(firstFlight.getId()))
                .andExpect(jsonPath("$[0].seat").value("4A"));
    }
}
