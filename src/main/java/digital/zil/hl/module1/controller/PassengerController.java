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

@RestController
@RequestMapping("/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    @Autowired
    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping
    public List<PassengerResponse> getPassengers() {
        return passengerService.getAllPassengers().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public PassengerResponse getPassengerById(@PathVariable Long id) {
        return toResponse(passengerService.getPassengerById(id));
    }

    @PostMapping
    public ResponseEntity<PassengerResponse> savePassenger(@RequestBody PassengerRequest request) {
        Passenger savedPassenger = passengerService.savePassenger(toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedPassenger));
    }

    @PutMapping("/{id}")
    public PassengerResponse updatePassenger(@PathVariable Long id, @RequestBody PassengerRequest request) {
        return toResponse(passengerService.updatePassenger(id, toModel(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.noContent().build();
    }

    private Passenger toModel(PassengerRequest request) {
        return new Passenger(
                null,
                request.getFullName(),
                request.getPassportData(),
                request.getContacts()
        );
    }

    private PassengerResponse toResponse(Passenger passenger) {
        return new PassengerResponse(
                passenger.getId(),
                passenger.getFullName(),
                passenger.getPassportData(),
                passenger.getContacts()
        );
    }
}
