package digital.zil.hl.module1.controller.dto;

import digital.zil.hl.module1.model.ServiceClass;

/**
 * DTO для создания бронирования.
 * Это структура JSON, которую клиент отправляет в POST /bookings.
 */
public class BookingRequest {

    private Long flightId;
    private Long passengerId;
    private ServiceClass serviceClass;
    private String seat;

    public BookingRequest() {
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
