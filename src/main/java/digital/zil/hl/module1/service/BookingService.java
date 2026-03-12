package digital.zil.hl.module1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import digital.zil.hl.module1.model.Booking;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.repository.BookingRepository;
import digital.zil.hl.module1.repository.FlightRepository;
import digital.zil.hl.module1.repository.PassengerRepository;

import java.util.List;

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

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    /**
     * Бронирование места на рейс.
     * Сервис проверяет, что рейс и пассажир существуют,
     * затем передаёт вместимость в репозиторий, где в synchronized-блоке
     * атомарно проверяется свободное место и вместимость рейса.
     */
    public Booking saveBooking(Booking booking) {
        final Flight flight = flightRepository.findById(booking.getFlightId());
        passengerRepository.findById(booking.getPassengerId());
        return bookingRepository.save(booking, flight.getCapacity());
    }

    public void deleteBooking(Long id) {
        bookingRepository.delete(id);
    }
}
