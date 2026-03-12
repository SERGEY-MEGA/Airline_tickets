package digital.zil.hl.module1.service;

import digital.zil.hl.module1.controller.exeption.BusinessException;
import digital.zil.hl.module1.model.Booking;
import digital.zil.hl.module1.model.Flight;
import digital.zil.hl.module1.model.Passenger;
import digital.zil.hl.module1.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingServiceConcurrencyTest {

    private BookingService bookingService;
    private FlightRepository flightRepository;
    private PassengerRepository passengerRepository;
    private BookingRepository bookingRepository;

    @BeforeEach
    public void setup() {
        flightRepository = new FlightRepositoryImpl();
        passengerRepository = new PassengerRepositoryImpl();
        bookingRepository = new BookingRepositoryImpl();
        bookingService = new BookingService(bookingRepository, flightRepository, passengerRepository);
    }

    @Test
    public void testConcurrentBookingsForCapacityLimit() throws InterruptedException {
        // Setup a flight with capacity 10
        Flight flight = new Flight(null, "SU101", "London", LocalDate.of(2026, 4, 1), 10);
        flight = flightRepository.save(flight);

        // Setup 15 passengers trying to book at the same time
        int numThreads = 15;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger exceptionCount = new AtomicInteger();

        for (int i = 1; i <= numThreads; i++) {
            Passenger p = new Passenger(null, "Passenger " + i, "P" + i, "");
            p = passengerRepository.save(p);
            
            final Long passengerId = p.getId();
            final Long flightId = flight.getId();
            final String seat = "Seat" + i; // Unique seats so we hit capacity limit, not seat duplication limit

            executor.submit(() -> {
                try {
                    latch.await(); // Wait for all threads to be ready
                    Booking booking = new Booking(null, flightId, passengerId, "Economy", seat);
                    bookingService.bookTicket(booking);
                    successCount.incrementAndGet();
                } catch (BusinessException e) {
                    exceptionCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        latch.countDown(); // Release all threads at once
        doneLatch.await(5, TimeUnit.SECONDS); // Wait for all threads to finish

        // Verify that exactly 10 bookings succeeded and 5 failed due to capacity limit
        assertEquals(10, successCount.get());
        assertEquals(5, exceptionCount.get());
        assertEquals(10, bookingRepository.countByFlightId(flight.getId()));
        
        executor.shutdown();
    }
    
    @Test
    public void testConcurrentBookingsForTheSameSeat() throws InterruptedException {
        Flight flight = new Flight(null, "SU102", "Paris", LocalDate.of(2026, 4, 1), 50);
        flight = flightRepository.save(flight);

        int numThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger exceptionCount = new AtomicInteger();

        for (int i = 1; i <= numThreads; i++) {
            Passenger p = new Passenger(null, "Passenger " + i, "P" + i, "");
            p = passengerRepository.save(p);
            
            final Long passengerId = p.getId();
            final Long flightId = flight.getId();
            final String seat = "1A"; // All trying to book exactly the same seat

            executor.submit(() -> {
                try {
                    latch.await(); 
                    Booking booking = new Booking(null, flightId, passengerId, "Business", seat);
                    bookingService.bookTicket(booking);
                    successCount.incrementAndGet();
                } catch (BusinessException e) {
                    exceptionCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        latch.countDown(); 
        doneLatch.await(5, TimeUnit.SECONDS); 

        // Verify exactly 1 succeeded, and 4 failed because the seat is taken
        assertEquals(1, successCount.get());
        assertEquals(4, exceptionCount.get());
        assertEquals(1, bookingRepository.countByFlightId(flight.getId()));
        
        executor.shutdown();
    }
}
