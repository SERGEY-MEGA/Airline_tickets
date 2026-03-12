package digital.zil.hl.module1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    private Long id;
    private String flightNumber;
    private String destination;
    private LocalDate departureDate;
    private int capacity;
}
