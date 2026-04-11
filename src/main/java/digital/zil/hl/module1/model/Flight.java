package digital.zil.hl.module1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность рейса.
 * В LAB1 живёт в HashMap, в LAB2 хранится в таблице flights.
 */
@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false, unique = true)
    @NonNull
    private String flightNumber;

    @Column(nullable = false)
    @NonNull
    private String destination;

    @Column(name = "departure_date", nullable = false)
    @NonNull
    private LocalDate departureDate;

    @Column(nullable = false)
    private int capacity;

    /**
     * Связанные бронирования.
     * В коде сервиса мы не обязаны использовать это поле, но связь полезна для JPA-модели.
     */
    @OneToMany(mappedBy = "flight")
    private List<Booking> bookings = new ArrayList<>();

    public Flight() {
    }

    public Flight(Long id, String flightNumber, String destination, LocalDate departureDate, int capacity) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    @Override
    public String toString() {
        return "Flight{id=" + id + ", flightNumber='" + flightNumber + "', destination='" + destination +
                "', departureDate=" + departureDate + ", capacity=" + capacity + "}";
    }
}
