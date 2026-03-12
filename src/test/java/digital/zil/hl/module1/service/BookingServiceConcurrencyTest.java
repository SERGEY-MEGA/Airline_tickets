package digital.zil.hl.module1.service;

import digital.zil.hl.module1.controller.exeption.AirlineException;
import digital.zil.hl.module1.model.Booking;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.model.Passenger;
import digital.zil.hl.module1.repository.BookingRepository;
import digital.zil.hl.module1.repository.FlightRepository;
import digital.zil.hl.module1.repository.PassengerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingServiceConcurrencyTest {

    private BookingService bookingService;
    private FlightRepository flightRepository;
    private PassengerRepository passengerRepository;
    private BookingRepository bookingRepository;

    @BeforeEach
    public void setup() {
        flightRepository = new FlightRepository();
        passengerRepository = new PassengerRepository();
        bookingRepository = new BookingRepository();
        bookingService = new BookingService(bookingRepository, flightRepository, passengerRepository);
    }

    @Test
    public void concurrentBookings_should_notExceedCapacity() throws InterruptedException {
        final int CAPACITY = 10;
        final int NUM_THREADS = 15;

        Flight flight = flightRepository.save(new Flight(null, "SU200", "London", LocalDate.of(2026, 4, 1), CAPACITY));

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch ready = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(NUM_THREADS);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();

        for (int i = 1; i <= NUM_THREADS; i++) {
            Passenger p = passengerRepository.save(new Passenger(null, "Пассажир " + i, "P" + i, ""));
            final Long passengerId = p.getId();
            final Long flightId = flight.getId();
            final String seat = "Место" + i;

            executor.submit(() -> {
                try {
                    ready.await();
                    Booking booking = new Booking(null, flightId, passengerId, "Economy", seat);
                    bookingService.saveBooking(booking);
                    successCount.incrementAndGet();
                } catch (AirlineException e) {
                    errorCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.countDown();
        done.await(5, TimeUnit.SECONDS);

        // Ровно 10 бронирований должны пройти, 5 — получить ошибку вместимости
        assertEquals(CAPACITY, successCount.get(), "Только " + CAPACITY + " бронирований должны успешно пройти");
        assertEquals(NUM_THREADS - CAPACITY, errorCount.get(), "Остальные должны получить ошибку");
        assertEquals(CAPACITY, bookingRepository.countByFlightId(flight.getId()));

        executor.shutdown();
    }
}
