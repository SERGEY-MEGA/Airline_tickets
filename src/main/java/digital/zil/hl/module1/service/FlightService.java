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

/**
 * Сервис для работы с рейсами.
 * Здесь лежит бизнес-логика рейсов: валидация и расчёт свободных мест.
 */
@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public FlightService(FlightRepository flightRepository, BookingRepository bookingRepository) {
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Возвращает все рейсы без дополнительной обработки.
     */
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    /**
     * Возвращает один рейс по id.
     */
    public Flight getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    /**
     * Создаёт рейс после проверки обязательных полей.
     */
    public Flight saveFlight(Flight flight) {
        validateFlight(flight);
        return flightRepository.save(flight);
    }

    /**
     * Обновляет существующий рейс.
     */
    public Flight updateFlight(Long id, Flight flight) {
        validateFlight(flight);
        return flightRepository.update(id, flight);
    }

    /**
     * Удаляет рейс по id.
     */
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
     * Возвращает список доступности рейсов.
     * Можно фильтровать:
     * только по направлению,
     * только по дате,
     * или сразу по двум параметрам.
     */
    public List<FlightAvailabilityResponse> getFlightsAvailabilityByFilters(String destination, LocalDate date) {
        if (!hasText(destination) && date == null) {
            throw new AirlineException("Нужно указать направление рейса, дату вылета или оба параметра");
        }

        return flightRepository.findByFilters(destination, date).stream()
                .map(this::buildAvailabilityResponse)
                .toList();
    }

    /**
     * Собирает DTO с количеством занятых и свободных мест.
     * Формула простая: capacity - bookedSeats.
     */
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

    /**
     * Проверяет обязательные поля рейса перед сохранением или обновлением.
     */
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

    /**
     * Маленькая вспомогательная проверка для строковых полей.
     */
    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
