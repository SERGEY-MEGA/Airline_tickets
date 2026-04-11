package digital.zil.hl.module1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import digital.zil.hl.module1.controller.dto.PassengerRequest;
import digital.zil.hl.module1.controller.dto.PassengerResponse;
import digital.zil.hl.module1.model.Passenger;
import digital.zil.hl.module1.service.PassengerService;

import java.util.List;

/**
 * REST-контроллер для работы с пассажирами.
 * Здесь нет бизнес-логики, только маршруты API и преобразование DTO.
 */
@RestController
@RequestMapping("/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    @Autowired
    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    /**
     * Возвращает список всех пассажиров.
     */
    @GetMapping
    public List<PassengerResponse> getPassengers() {
        return passengerService.getAllPassengers().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Возвращает одного пассажира по id.
     */
    @GetMapping("/{id}")
    public PassengerResponse getPassengerById(@PathVariable Long id) {
        return toResponse(passengerService.getPassengerById(id));
    }

    /**
     * Создаёт нового пассажира.
     */
    @PostMapping
    public ResponseEntity<PassengerResponse> savePassenger(@RequestBody PassengerRequest request) {
        Passenger savedPassenger = passengerService.savePassenger(toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedPassenger));
    }

    /**
     * Обновляет данные пассажира.
     */
    @PutMapping("/{id}")
    public PassengerResponse updatePassenger(@PathVariable Long id, @RequestBody PassengerRequest request) {
        return toResponse(passengerService.updatePassenger(id, toModel(request)));
    }

    /**
     * Удаляет пассажира по id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Переводит DTO запроса в доменную модель Passenger.
     */
    private Passenger toModel(PassengerRequest request) {
        return new Passenger(
                null,
                request.getFullName(),
                request.getPassportData(),
                request.getContacts()
        );
    }

    /**
     * Переводит модель Passenger в DTO ответа.
     */
    private PassengerResponse toResponse(Passenger passenger) {
        return new PassengerResponse(
                passenger.getId(),
                passenger.getFullName(),
                passenger.getPassportData(),
                passenger.getContacts()
        );
    }
}
