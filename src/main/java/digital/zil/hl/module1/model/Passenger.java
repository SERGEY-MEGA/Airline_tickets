package digital.zil.hl.module1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "passengers")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    @NonNull
    private String fullName;

    @Column(name = "passport_data", nullable = false)
    @NonNull
    private String passportData;

    @Column
    private String contacts;

    @OneToMany(mappedBy = "passenger")
    private List<Booking> bookings = new ArrayList<>();

    public Passenger() {
    }

    public Passenger(Long id, String fullName, String passportData, String contacts) {
        this.id = id;
        this.fullName = fullName;
        this.passportData = passportData;
        this.contacts = contacts;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassportData() { return passportData; }
    public void setPassportData(String passportData) { this.passportData = passportData; }

    public String getContacts() { return contacts; }
    public void setContacts(String contacts) { this.contacts = contacts; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    @Override
    public String toString() {
        return "Passenger{id=" + id + ", fullName='" + fullName + "', passportData='" + passportData +
                "', contacts='" + contacts + "'}";
    }
}
