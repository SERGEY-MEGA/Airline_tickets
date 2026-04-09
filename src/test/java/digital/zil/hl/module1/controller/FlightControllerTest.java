package digital.zil.hl.module1.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import digital.zil.hl.module1.Application;
import digital.zil.hl.module1.repository.FlightRepository;
import digital.zil.hl.module1.repository.BookingRepository;
import digital.zil.hl.module1.repository.PassengerRepository;
import digital.zil.hl.module1.model.Flight;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("lab1")
public class FlightControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @BeforeEach
    public void init() {
        bookingRepository.clear();
        passengerRepository.clear();
        flightRepository.clear();
    }

    @Test
    public void saveFlight_should_returnCreatedFlight() throws Exception {
        String json = "{\"flightNumber\":\"SU100\",\"destination\":\"Moscow\",\"departureDate\":\"2026-03-20\",\"capacity\":150}";

        mvc.perform(post("/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flightNumber").value("SU100"));
    }

    @Test
    public void updateFlight_should_changeFlightNumber() throws Exception {
        Flight flight = flightRepository.save(new Flight(null, "SU100", "Moscow", LocalDate.of(2026, 3, 20), 150));
        String updatedJson = "{\"flightNumber\":\"SU200\",\"destination\":\"Moscow\",\"departureDate\":\"2026-03-20\",\"capacity\":150}";

        mvc.perform(put("/flights/" + flight.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("SU200"));
    }

    @Test
    public void getAvailability_should_returnCorrectSeats() throws Exception {
        Flight flight = flightRepository.save(new Flight(null, "SU101", "London", LocalDate.of(2027, 4, 1), 200));

        mvc.perform(get("/flights/" + flight.getId() + "/availability")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightId").value(flight.getId()))
                .andExpect(jsonPath("$.flightNumber").value("SU101"))
                .andExpect(jsonPath("$.destination").value("London"))
                .andExpect(jsonPath("$.departureDate").value("2027-04-01"))
                .andExpect(jsonPath("$.capacity").value(200))
                .andExpect(jsonPath("$.bookedSeats").value(0))
                .andExpect(jsonPath("$.availableSeats").value(200));
    }
}
