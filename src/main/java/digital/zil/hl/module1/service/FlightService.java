package digital.zil.hl.module1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import digital.zil.hl.module1.controller.exeption.AirlineException;
import digital.zil.hl.module1.controller.dto.AvailabilityResponse;
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
     * Возвращает доступность мест для конкретного рейса по его ID.
     */
    public AvailabilityResponse getFlightAvailability(Long flightId) {
        Flight flight = flightRepository.findById(flightId);
        int booked = (int) bookingRepository.countByFlightId(flightId);
        int available = flight.getCapacity() - booked;

        return new AvailabilityResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getCapacity(),
                booked,
                available
        );
    }

    /**
     * Возвращает доступность мест по номеру рейса (например, "SU301").
     */
    public AvailabilityResponse getFlightAvailabilityByNumber(String flightNumber) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber);
        int booked = (int) bookingRepository.countByFlightId(flight.getId());
        int available = flight.getCapacity() - booked;

        return new AvailabilityResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getCapacity(),
                booked,
                available
        );
    }

    /**
     * Возвращает доступность мест по ВСЕМ рейсам.
     */
    public List<AvailabilityResponse> getAllFlightsAvailability() {
        return flightRepository.findAll().stream()
                .map(flight -> {
                    int booked = (int) bookingRepository.countByFlightId(flight.getId());
                    int available = flight.getCapacity() - booked;
                    return new AvailabilityResponse(
                            flight.getId(),
                            flight.getFlightNumber(),
                            flight.getCapacity(),
                            booked,
                            available
                    );
                })
                .toList();
    }
}
