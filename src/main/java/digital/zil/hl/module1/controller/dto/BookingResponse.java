package digital.zil.hl.module1.controller.dto;

import digital.zil.hl.module1.model.ServiceClass;

/**
 * DTO ответа по бронированию.
 * Используется, чтобы наружу отдавать только нужные поля.
 */
public class BookingResponse {

    private Long id;
    private Long flightId;
    private Long passengerId;
    private ServiceClass serviceClass;
    private String seat;

    public BookingResponse() {
    }

    public BookingResponse(Long id, Long flightId, Long passengerId, ServiceClass serviceClass, String seat) {
        this.id = id;
        this.flightId = flightId;
        this.passengerId = passengerId;
        this.serviceClass = serviceClass;
        this.seat = seat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public ServiceClass getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(ServiceClass serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }
}
