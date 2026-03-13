package digital.zil.hl.module1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import digital.zil.hl.module1.controller.dto.AvailabilityResponse;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.service.FlightService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/flights")
    public List<Flight> getFlights() {
        return flightService.getAllFlights();
    }

    @GetMapping("/flights/{id}")
    public Flight getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id);
    }

    /**
     * Выводит информацию о рейсе и остаток свободных мест.
     * Пример: GET /flights/6/availability
     */
    @GetMapping("/flights/{flightId}/availability")
    public AvailabilityResponse getFlightAvailability(@PathVariable Long flightId) {
        return flightService.getFlightAvailability(flightId);
    }

    /**
     * Выводит остаток свободных мест по номеру рейса.
     * Пример: GET /flights/number/SU301/availability
     */
    @GetMapping("/flights/number/{flightNumber}/availability")
    public AvailabilityResponse getAvailabilityByNumber(@PathVariable String flightNumber) {
        return flightService.getFlightAvailabilityByNumber(flightNumber);
    }
    @PostMapping("/flights")
    public Flight saveFlight(@RequestBody Flight flight) {
        return flightService.saveFlight(flight);
    }

    /**
     * Полное обновление рейса по ID.
     * Пример: изменить номер рейса SU100 → SU200:
     * PUT /flights/1
     * Body: { "flightNumber": "SU200", "destination": "Moscow", "departureDate": "2026-03-20", "capacity": 150 }
     */
    @PutMapping("/flights/{id}")
    public Flight updateFlight(@PathVariable Long id, @RequestBody Flight flight) {
        return flightService.updateFlight(id, flight);
    }

    @DeleteMapping("/flights/{id}")
    public void deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
    }
}
