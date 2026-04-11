package digital.zil.hl.module1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import digital.zil.hl.module1.controller.exception.AirlineException;
import digital.zil.hl.module1.model.Booking;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.repository.BookingRepository;
import digital.zil.hl.module1.repository.FlightRepository;
import digital.zil.hl.module1.repository.PassengerRepository;

import java.util.List;

/**
 * Сервис бронирований.
 * Здесь находятся основные проверки перед сохранением брони.
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          FlightRepository flightRepository,
                          PassengerRepository passengerRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
    }

    /**
     * Возвращает все бронирования.
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Возвращает бронирование по id.
     */
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    /**
     * Возвращает все бронирования конкретного рейса.
     */
    public List<Booking> getBookingsByFlightId(Long flightId) {
        flightRepository.findById(flightId);
        return bookingRepository.findByFlightId(flightId);
    }

    /**
     * Сохраняет бронирование только после проверки бизнес-правил:
     * рейс существует, пассажир существует, место не занято и вместимость не превышена.
     */
    public Booking saveBooking(Booking booking) {
        validateBooking(booking);
        final Flight flight = flightRepository.findById(booking.getFlightId());
        passengerRepository.findById(booking.getPassengerId());
        return bookingRepository.save(booking, flight.getCapacity());
    }

    /**
     * Удаляет бронирование по id.
     */
    public void deleteBooking(Long id) {
        bookingRepository.delete(id);
    }

    /**
     * Базовая валидация входных данных бронирования.
     */
    private void validateBooking(Booking booking) {
        if (booking == null) {
            throw new AirlineException("Данные бронирования обязательны");
        }
        if (booking.getFlightId() == null) {
            throw new AirlineException("ID рейса обязателен");
        }
        if (booking.getPassengerId() == null) {
            throw new AirlineException("ID пассажира обязателен");
        }
        if (booking.getServiceClass() == null) {
            throw new AirlineException("Класс обслуживания обязателен");
        }
        if (booking.getSeat() == null || booking.getSeat().isBlank()) {
            throw new AirlineException("Место не должно быть пустым");
        }
    }
}
