package digital.zil.hl.module1.controller.dto;

/**
 * DTO для создания или обновления пассажира.
 */
public class PassengerRequest {

    private String fullName;
    private String passportData;
    private String contacts;

    public PassengerRequest() {
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
