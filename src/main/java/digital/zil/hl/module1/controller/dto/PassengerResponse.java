package digital.zil.hl.module1.controller.dto;

public class PassengerResponse {

    private Long id;
    private String fullName;
    private String passportData;
    private String contacts;

    public PassengerResponse() {
    }

    public PassengerResponse(Long id, String fullName, String passportData, String contacts) {
        this.id = id;
        this.fullName = fullName;
        this.passportData = passportData;
        this.contacts = contacts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassportData() {
        return passportData;
    }

    public void setPassportData(String passportData) {
        this.passportData = passportData;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
}
