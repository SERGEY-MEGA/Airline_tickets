package digital.zil.hl.module1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import digital.zil.hl.module1.controller.dto.FlightAvailabilityResponse;
import digital.zil.hl.module1.controller.exception.AirlineException;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.repository.BookingRepository;
import digital.zil.hl.module1.repository.FlightRepository;

import java.time.LocalDate;
import java.util.List;

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
        validateFlight(flight);
        return flightRepository.save(flight);
    }

    public Flight updateFlight(Long id, Flight flight) {
        validateFlight(flight);
        return flightRepository.update(id, flight);
    }

    public void deleteFlight(Long id) {
        flightRepository.delete(id);
    }

    /**
     * Возвращает доступность мест для конкретного рейса по его ID.
     */
    public FlightAvailabilityResponse getFlightAvailability(Long flightId) {
        Flight flight = flightRepository.findById(flightId);
        return buildAvailabilityResponse(flight);
    }

    /**
     * Возвращает доступность мест по номеру рейса (например, "SU301").
     */
    public FlightAvailabilityResponse getFlightAvailabilityByNumber(String flightNumber) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber);
        return buildAvailabilityResponse(flight);
    }

    /**
     * Возвращает доступность мест по ВСЕМ рейсам.
     */
    public List<FlightAvailabilityResponse> getAllFlightsAvailability() {
        return flightRepository.findAll().stream()
                .map(this::buildAvailabilityResponse)
                .toList();
    }

    /**
     * Возвращает список доступности для рейсов на конкретное направление и дату.
     */
    public List<FlightAvailabilityResponse> getFlightsAvailabilityByDestinationAndDate(String destination, LocalDate date) {
        if (!hasText(destination)) {
            throw new AirlineException("Направление рейса не должно быть пустым");
        }
        if (date == null) {
            throw new AirlineException("Дата вылета обязательна");
        }

        return flightRepository.findByDestinationAndDate(destination, date).stream()
                .map(this::buildAvailabilityResponse)
                .toList();
    }

    private FlightAvailabilityResponse buildAvailabilityResponse(Flight flight) {
        int booked = (int) bookingRepository.countByFlightId(flight.getId());
        int available = flight.getCapacity() - booked;

        return new FlightAvailabilityResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getDestination(),
                flight.getDepartureDate(),
                flight.getCapacity(),
                booked,
                available
        );
    }

    private void validateFlight(Flight flight) {
        if (flight == null) {
            throw new AirlineException("Данные рейса обязательны");
        }
        if (!hasText(flight.getFlightNumber())) {
            throw new AirlineException("Номер рейса не должен быть пустым");
        }
        if (!hasText(flight.getDestination())) {
            throw new AirlineException("Направление рейса не должно быть пустым");
        }
        if (flight.getDepartureDate() == null) {
            throw new AirlineException("Дата вылета обязательна");
        }
        if (flight.getCapacity() <= 0) {
            throw new AirlineException("Вместимость рейса должна быть больше нуля");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
