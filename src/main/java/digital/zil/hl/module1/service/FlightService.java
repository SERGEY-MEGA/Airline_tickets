package digital.zil.hl.module1.service;

import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.repository.BookingRepository;
import digital.zil.hl.module1.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;

    public FlightService(FlightRepository flightRepository, BookingRepository bookingRepository) {
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
    }

    public Flight createFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    public List<Map<String, Object>> getAvailability(String destination, LocalDate date) {
        List<Flight> flights = flightRepository.findByDestinationAndDepartureDate(destination, date);
        return flights.stream().map(flight -> {
            long bookedSeats = bookingRepository.countByFlightId(flight.getId());
            long availableSeats = flight.getCapacity() - bookedSeats;
            
            Map<String, Object> result = new HashMap<>();
            result.put("flight", flight);
            result.put("availableSeats", availableSeats);
            return result;
        }).collect(Collectors.toList());
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }
}
