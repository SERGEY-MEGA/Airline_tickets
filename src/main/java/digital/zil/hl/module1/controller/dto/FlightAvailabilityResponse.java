package digital.zil.hl.module1.controller.dto;

import java.time.LocalDate;

public class FlightAvailabilityResponse {

    private Long flightId;
    private String flightNumber;
    private String destination;
    private LocalDate departureDate;
    private int capacity;
    private int bookedSeats;
    private int availableSeats;

    public FlightAvailabilityResponse() {
    }

    public FlightAvailabilityResponse(
            Long flightId,
            String flightNumber,
            String destination,
            LocalDate departureDate,
            int capacity,
            int bookedSeats,
            int availableSeats
    ) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.bookedSeats = bookedSeats;
        this.availableSeats = availableSeats;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
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

    public int getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
