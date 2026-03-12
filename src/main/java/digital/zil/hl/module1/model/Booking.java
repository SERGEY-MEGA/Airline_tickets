package digital.zil.hl.module1.model;

import org.springframework.lang.NonNull;

public class Booking {

    private Long id;

    @NonNull
    private Long flightId;

    @NonNull
    private Long passengerId;

    private String serviceClass;
    private String seat;

    public Booking() {
    }

    public Booking(Long id, Long flightId, Long passengerId, String serviceClass, String seat) {
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

    public String getServiceClass() { return serviceClass; }
    public void setServiceClass(String serviceClass) { this.serviceClass = serviceClass; }

    public String getSeat() { return seat; }
    public void setSeat(String seat) { this.seat = seat; }

    @Override
    public String toString() {
        return "Booking{id=" + id + ", flightId=" + flightId + ", passengerId=" + passengerId +
                ", serviceClass='" + serviceClass + "', seat='" + seat + "'}";
    }
}
