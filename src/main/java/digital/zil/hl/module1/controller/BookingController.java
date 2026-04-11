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

/**
 * REST-контроллер для работы с бронированиями.
 * Через него создаются брони и запрашиваются уже сохранённые бронирования.
 */
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Возвращает все бронирования или бронирования конкретного рейса.
     * Параметр flightId нужен для удобной демонстрации на защите.
     */
    @GetMapping
    public List<BookingResponse> getBookings(@RequestParam(required = false) Long flightId) {
        List<Booking> bookings = flightId == null
                ? bookingService.getAllBookings()
                : bookingService.getBookingsByFlightId(flightId);

        return bookings.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Возвращает одно бронирование по id.
     */
    @GetMapping("/{id}")
    public BookingResponse getBookingById(@PathVariable Long id) {
        return toResponse(bookingService.getBookingById(id));
    }

    /**
     * Создаёт новое бронирование места на рейс.
     */
    @PostMapping
    public ResponseEntity<BookingResponse> saveBooking(@RequestBody BookingRequest request) {
        Booking savedBooking = bookingService.saveBooking(toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedBooking));
    }

    /**
     * Удаляет бронирование по id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Переводит JSON запроса в модель Booking.
     */
    private Booking toModel(BookingRequest request) {
        return new Booking(
                null,
                request.getFlightId(),
                request.getPassengerId(),
                request.getServiceClass(),
                request.getSeat()
        );
    }

    /**
     * Переводит модель Booking в DTO ответа.
     */
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
