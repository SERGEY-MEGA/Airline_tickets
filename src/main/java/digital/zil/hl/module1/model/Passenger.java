package digital.zil.hl.module1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {
    private Long id;
    private String fullName;
    private String passportData;
    private String contacts;
}
