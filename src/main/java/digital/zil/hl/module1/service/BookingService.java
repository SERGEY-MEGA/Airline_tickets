package digital.zil.hl.module1.service;

import digital.zil.hl.module1.controller.exeption.BusinessException;
import digital.zil.hl.module1.model.Booking;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.repository.BookingRepository;
import digital.zil.hl.module1.repository.FlightRepository;
import digital.zil.hl.module1.repository.PassengerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;

    // Use a lock object for synchronization specifically for booking to avoid race conditions.
    private final Object bookingLock = new Object();

    public BookingService(BookingRepository bookingRepository, FlightRepository flightRepository, PassengerRepository passengerRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
    }

    public Booking bookTicket(Booking booking) {
        // Validate payload
        if (booking.getFlightId() == null || booking.getPassengerId() == null || booking.getSeat() == null) {
            throw new BusinessException("Missing mandatory fields: flightId, passengerId, seat");
        }

        // Validate passenger
        passengerRepository.findById(booking.getPassengerId())
                .orElseThrow(() -> new BusinessException("Passenger not found with id: " + booking.getPassengerId()));

        // Synchronization block to prevent overbooking and double-seat booking.
        // In a real high-load system, this would be a database transaction with SELECT FOR UPDATE or similar constraint, 
        // but since we are using static collections, we mock this with a synchronized block.
        synchronized (bookingLock) {
            Flight flight = flightRepository.findById(booking.getFlightId())
                    .orElseThrow(() -> new BusinessException("Flight not found with id: " + booking.getFlightId()));

            // Check if seat is already booked
            if (bookingRepository.existsByFlightIdAndSeat(flight.getId(), booking.getSeat())) {
                throw new BusinessException("Seat " + booking.getSeat() + " is already booked on flight " + flight.getId());
            }

            // Check flight capacity
            long currentBookings = bookingRepository.countByFlightId(flight.getId());
            if (currentBookings >= flight.getCapacity()) {
                throw new BusinessException("Flight " + flight.getId() + " is fully booked. Capacity: " + flight.getCapacity());
            }

            // Proceed with booking
            return bookingRepository.save(booking);
        }
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
