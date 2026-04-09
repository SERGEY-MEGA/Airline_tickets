package digital.zil.hl.module1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import digital.zil.hl.module1.controller.dto.BookingRequest;
import digital.zil.hl.module1.controller.dto.BookingResponse;
import digital.zil.hl.module1.model.Booking;
import digital.zil.hl.module1.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingResponse> getBookings(@RequestParam(required = false) Long flightId) {
        List<Booking> bookings = flightId == null
                ? bookingService.getAllBookings()
                : bookingService.getBookingsByFlightId(flightId);

        return bookings.stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public BookingResponse getBookingById(@PathVariable Long id) {
        return toResponse(bookingService.getBookingById(id));
    }

    @PostMapping
    public ResponseEntity<BookingResponse> saveBooking(@RequestBody BookingRequest request) {
        Booking savedBooking = bookingService.saveBooking(toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedBooking));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    private Booking toModel(BookingRequest request) {
        return new Booking(
                null,
                request.getFlightId(),
                request.getPassengerId(),
                request.getServiceClass(),
                request.getSeat()
        );
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getFlightId(),
                booking.getPassengerId(),
                booking.getServiceClass(),
                booking.getSeat()
        );
    }
}
