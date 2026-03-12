package digital.zil.hl.module1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import digital.zil.hl.module1.model.Passenger;
import digital.zil.hl.module1.service.PassengerService;

import java.util.List;

@RestController
public class PassengerController {

    private final PassengerService passengerService;

    @Autowired
    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping("/passengers")
    public List<Passenger> getPassengers() {
        return passengerService.getAllPassengers();
    }

    @GetMapping("/passengers/{id}")
    public Passenger getPassengerById(@PathVariable Long id) {
        return passengerService.getPassengerById(id);
    }

    @PostMapping("/passengers")
    public Passenger savePassenger(@RequestBody Passenger passenger) {
        return passengerService.savePassenger(passenger);
    }

    @PutMapping("/passengers/{id}")
    public Passenger updatePassenger(@PathVariable Long id, @RequestBody Passenger passenger) {
        return passengerService.updatePassenger(id, passenger);
    }

    @DeleteMapping("/passengers/{id}")
    public void deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
    }
}
