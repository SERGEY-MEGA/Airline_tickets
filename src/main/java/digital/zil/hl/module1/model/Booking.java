package digital.zil.hl.module1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private Long id;
    private Long flightId;
    private Long passengerId;
    private String serviceClass;
    private String seat;
}
