package digital.zil.hl.module1.repository.jpa;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import digital.zil.hl.module1.controller.exception.AirlineException;
import digital.zil.hl.module1.model.Booking;
import digital.zil.hl.module1.repository.BookingRepository;

import java.util.List;

import static java.lang.String.format;

/**
 * Репозиторий LAB2/LAB3 для бронирований.
 * Использует БД, но сохраняет те же бизнес-правила, что и LAB1.
 */
@Repository
@Profile({"lab2", "lab3"})
@Transactional(readOnly = true)
public class JpaBookingRepository implements BookingRepository {

    private static final String BOOKING_NOT_FOUND_MSG = "Бронирование с ID %d не найдено";
    private static final String SEAT_ALREADY_BOOKED_MSG = "Место '%s' на рейсе ID %d уже занято";
    private static final String FLIGHT_FULLY_BOOKED_MSG = "Рейс ID %d заполнен. Вместимость: %d";
    private static final String FLIGHT_NOT_FOUND_MSG = "Рейс с ID %d не найден";

    private final SpringDataBookingJpaRepository repository;
    private final SpringDataFlightJpaRepository flightRepository;

    public JpaBookingRepository(
            SpringDataBookingJpaRepository repository,
            SpringDataFlightJpaRepository flightRepository
    ) {
        this.repository = repository;
        this.flightRepository = flightRepository;
    }

    @Override
    public List<Booking> findAll() {
        return repository.findAll();
    }

    @Override
    public Booking findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AirlineException(format(BOOKING_NOT_FOUND_MSG, id)));
    }

    @Override
    public List<Booking> findByFlightId(Long flightId) {
        return repository.findByFlightId(flightId);
    }

    @Override
    public long countByFlightId(Long flightId) {
        return repository.countByFlightId(flightId);
    }

    @Override
    public boolean isSeatTaken(Long flightId, String seat) {
        return repository.existsByFlightIdAndSeatIgnoreCase(flightId, seat);
    }

    /**
     * Сначала блокирует рейс, потом проверяет вместимость и занятость места.
     * Это уменьшает риск гонок при одновременных запросах.
     */
    @Override
    @Transactional
    public Booking save(Booking booking, int flightCapacity) {
        flightRepository.findByIdForUpdate(booking.getFlightId())
                .orElseThrow(() -> new AirlineException(format(FLIGHT_NOT_FOUND_MSG, booking.getFlightId())));

        if (countByFlightId(booking.getFlightId()) >= flightCapacity) {
            throw new AirlineException(format(FLIGHT_FULLY_BOOKED_MSG, booking.getFlightId(), flightCapacity));
        }
        if (isSeatTaken(booking.getFlightId(), booking.getSeat())) {
            throw new AirlineException(format(SEAT_ALREADY_BOOKED_MSG, booking.getSeat(), booking.getFlightId()));
        }

        try {
            return repository.save(booking);
        } catch (DataIntegrityViolationException ex) {
            throw new AirlineException(format(SEAT_ALREADY_BOOKED_MSG, booking.getSeat(), booking.getFlightId()));
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new AirlineException(format(BOOKING_NOT_FOUND_MSG, id));
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void clear() {
        repository.deleteAll();
    }
}
