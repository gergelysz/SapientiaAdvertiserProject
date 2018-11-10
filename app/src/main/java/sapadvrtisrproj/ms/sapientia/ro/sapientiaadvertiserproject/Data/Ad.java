package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data;

public class Ad {

    private String title;
    private String shortDesc;
    private String longDesc;
    private String phoneNumber;
    private String location;

    public Ad() {
    }

    public Ad(String title, String shortDesc, String longDesc, String phoneNumber, String location) {
        this.title = title;
        this.shortDesc = shortDesc;
        this.longDesc = longDesc;
        this.phoneNumber = phoneNumber;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
