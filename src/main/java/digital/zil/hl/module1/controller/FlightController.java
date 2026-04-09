package digital.zil.hl.module1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import digital.zil.hl.module1.controller.dto.FlightAvailabilityResponse;
import digital.zil.hl.module1.controller.dto.FlightRequest;
import digital.zil.hl.module1.controller.dto.FlightResponse;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.service.FlightService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public List<FlightResponse> getFlights() {
        return flightService.getAllFlights().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public FlightResponse getFlightById(@PathVariable Long id) {
        return toResponse(flightService.getFlightById(id));
    }

    @GetMapping("/{flightId}/availability")
    public FlightAvailabilityResponse getFlightAvailability(@PathVariable Long flightId) {
        return flightService.getFlightAvailability(flightId);
    }

    @GetMapping("/availability")
    public List<FlightAvailabilityResponse> getAllAvailability() {
        return flightService.getAllFlightsAvailability();
    }

    @GetMapping("/free-seats")
    public List<FlightAvailabilityResponse> searchAvailability(
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return flightService.getFlightsAvailabilityByDestinationAndDate(destination, date);
    }

    @GetMapping("/number/{flightNumber}/availability")
    public FlightAvailabilityResponse getAvailabilityByNumber(@PathVariable String flightNumber) {
        return flightService.getFlightAvailabilityByNumber(flightNumber);
    }

    @PostMapping
    public ResponseEntity<FlightResponse> saveFlight(@RequestBody FlightRequest request) {
        Flight savedFlight = flightService.saveFlight(toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedFlight));
    }

    @PutMapping("/{id}")
    public FlightResponse updateFlight(@PathVariable Long id, @RequestBody FlightRequest request) {
        return toResponse(flightService.updateFlight(id, toModel(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

    private Flight toModel(FlightRequest request) {
        return new Flight(
                null,
                request.getFlightNumber(),
                request.getDestination(),
                request.getDepartureDate(),
                request.getCapacity()
        );
    }

    private FlightResponse toResponse(Flight flight) {
        return new FlightResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getDestination(),
                flight.getDepartureDate(),
                flight.getCapacity()
        );
    }
}
