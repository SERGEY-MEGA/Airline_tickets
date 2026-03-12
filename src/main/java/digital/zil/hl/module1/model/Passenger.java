package digital.zil.hl.module1.model;

import org.springframework.lang.NonNull;

public class Passenger {

    private Long id;

    @NonNull
    private String fullName;

    @NonNull
    private String passportData;

    private String contacts;

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

    @Override
    public String toString() {
        return "Passenger{id=" + id + ", fullName='" + fullName + "', passportData='" + passportData +
                "', contacts='" + contacts + "'}";
    }
}
