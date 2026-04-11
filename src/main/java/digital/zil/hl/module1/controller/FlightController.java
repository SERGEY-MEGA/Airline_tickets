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

/**
 * REST-контроллер для работы с рейсами.
 * Здесь только HTTP-слой: принять запрос, вызвать сервис и вернуть ответ.
 */
@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    /**
     * Возвращает список всех рейсов.
     */
    @GetMapping
    public List<FlightResponse> getFlights() {
        return flightService.getAllFlights().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Возвращает один рейс по его идентификатору.
     */
    @GetMapping("/{id}")
    public FlightResponse getFlightById(@PathVariable Long id) {
        return toResponse(flightService.getFlightById(id));
    }

    /**
     * Показывает, сколько мест занято и сколько свободно у конкретного рейса.
     */
    @GetMapping("/{flightId}/availability")
    public FlightAvailabilityResponse getFlightAvailability(@PathVariable Long flightId) {
        return flightService.getFlightAvailability(flightId);
    }

    /**
     * Возвращает доступность мест сразу по всем рейсам.
     */
    @GetMapping("/availability")
    public List<FlightAvailabilityResponse> getAllAvailability() {
        return flightService.getAllFlightsAvailability();
    }

    /**
     * Дополнительная задача:
     * ищет рейсы по направлению, по дате или сразу по двум параметрам
     * и возвращает остаток свободных мест.
     */
    @GetMapping("/free-seats")
    public List<FlightAvailabilityResponse> searchAvailability(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return flightService.getFlightsAvailabilityByFilters(destination, date);
    }

    /**
     * Удобный поиск доступности мест по номеру рейса.
     */
    @GetMapping("/number/{flightNumber}/availability")
    public FlightAvailabilityResponse getAvailabilityByNumber(@PathVariable String flightNumber) {
        return flightService.getFlightAvailabilityByNumber(flightNumber);
    }

    /**
     * Создаёт новый рейс.
     */
    @PostMapping
    public ResponseEntity<FlightResponse> saveFlight(@RequestBody FlightRequest request) {
        Flight savedFlight = flightService.saveFlight(toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedFlight));
    }

    /**
     * Обновляет существующий рейс.
     */
    @PutMapping("/{id}")
    public FlightResponse updateFlight(@PathVariable Long id, @RequestBody FlightRequest request) {
        return toResponse(flightService.updateFlight(id, toModel(request)));
    }

    /**
     * Удаляет рейс по id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Переводит входной DTO в доменную модель Flight.
     * Это нужно, чтобы контроллер не передавал request-объект дальше по слоям.
     */
    private Flight toModel(FlightRequest request) {
        return new Flight(
                null,
                request.getFlightNumber(),
                request.getDestination(),
                request.getDepartureDate(),
                request.getCapacity()
        );
    }

    /**
     * Переводит доменную модель Flight в DTO ответа.
     * Так наружу отдаётся только нужная клиенту структура JSON.
     */
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
