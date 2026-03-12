package digital.zil.hl.module1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import digital.zil.hl.module1.controller.exeption.AirlineException;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.repository.BookingRepository;
import digital.zil.hl.module1.repository.FlightRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public FlightService(FlightRepository flightRepository, BookingRepository bookingRepository) {
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Flight getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    public Flight saveFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    /** Полное обновление рейса (например, изменить номер с SU100 на SU200) */
    public Flight updateFlight(Long id, Flight flight) {
        return flightRepository.put(id, flight);
    }

    public void deleteFlight(Long id) {
        flightRepository.delete(id);
    }

    /**
     * Возвращает список рейсов по направлению и дате
     * вместе с количеством свободных мест на каждом.
     */
    public List<Map<String, Object>> getAvailability(String destination, LocalDate date) {
        return flightRepository.findByDestinationAndDate(destination, date).stream()
                .map(flight -> {
                    long booked = bookingRepository.countByFlightId(flight.getId());
                    long available = flight.getCapacity() - booked;
                    Map<String, Object> result = new HashMap<>();
                    result.put("flight", flight);
                    result.put("availableSeats", available);
                    return result;
                })
                .collect(Collectors.toList());
    }
}
