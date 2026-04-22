package digital.zil.hl.module1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Сущность бронирования.
 * Хранит связь между рейсом и пассажиром, а также место и класс обслуживания.
 */
@Entity
@Table(
        name = "bookings",
        uniqueConstraints = @UniqueConstraint(name = "uk_booking_flight_seat", columnNames = {"flight_id", "seat"})
)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_id", nullable = false)
    private Long flightId;

    @Column(name = "passenger_id", nullable = false)
    private Long passengerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_class", nullable = false)
    private ServiceClass serviceClass;

    @Column(nullable = false)
    private String seat;

    /**
     * JPA-связи нужны PostgreSQL-режиму.
     * Поля flightId/passengerId остаются простыми для DTO и memory-режима.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", insertable = false, updatable = false)
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", insertable = false, updatable = false)
    private Passenger passenger;

    public Booking() {
    }

    public Booking(Long id, Long flightId, Long passengerId, ServiceClass serviceClass, String seat) {
        this.id = id;
        this.flightId = flightId;
        this.passengerId = passengerId;
        this.serviceClass = serviceClass;
        this.seat = seat;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }

    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }

    public ServiceClass getServiceClass() { return serviceClass; }
    public void setServiceClass(ServiceClass serviceClass) { this.serviceClass = serviceClass; }

    public String getSeat() { return seat; }
    public void setSeat(String seat) { this.seat = seat; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }

    @Override
    public String toString() {
        return "Booking{id=" + id + ", flightId=" + flightId + ", passengerId=" + passengerId +
                ", serviceClass='" + serviceClass + "', seat='" + seat + "'}";
    }
}
