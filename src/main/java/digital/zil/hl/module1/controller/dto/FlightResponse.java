package digital.zil.hl.module1.controller.dto;

import java.time.LocalDate;

public class FlightResponse {

    private Long id;
    private String flightNumber;
    private String destination;
    private LocalDate departureDate;
    private int capacity;

    public FlightResponse() {
    }

    public FlightResponse(Long id, String flightNumber, String destination, LocalDate departureDate, int capacity) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
