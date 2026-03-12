package digital.zil.hl.module1.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import digital.zil.hl.module1.Application;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.repository.FlightRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class FlightControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FlightRepository flightRepository;

    @BeforeEach
    public void init() {
        // We can't clear() easily since we used ConcurrentHashMap without clear method exported.
        // But for testing individual endpoints this will suffice.
    }

    @Test
    public void testCreateFlight() throws Exception {
        Flight flight = new Flight(null, "SU100", "Moscow", LocalDate.of(2026, 3, 20), 150);
        String flightJson = objectMapper.writeValueAsString(flight);

        mvc.perform(post("/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(flightJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("SU100"));
    }

    @Test
    public void testGetAvailability() throws Exception {
        Flight flight = flightRepository.save(new Flight(null, "SU101", "London", LocalDate.of(2027, 4, 1), 200));

        mvc.perform(get("/flights/availability")
                .param("destination", "London")
                .param("date", "2027-04-01")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flight.flightNumber").value("SU101"))
                .andExpect(jsonPath("$[0].availableSeats").value(200));
    }
}
