package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data;

public class Ad {

    private String id;
    private String title;
    private String shortDesc;
    private String longDesc;
    private String phoneNumber;
    private String location;
    private String visitedNumber;
    private String visibilityRight;
//    private ImageView image;
    private String image;

    public Ad() {

    }

    public Ad(String title, String shortDesc, String longDesc, String phoneNumber, String location, String visitedNumber, String imageLink, String visibilityRight) {
        this.title = title;
        this.shortDesc = shortDesc;
        this.longDesc = longDesc;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.visitedNumber = visitedNumber;
        this.image = imageLink;
        this.visibilityRight=visibilityRight;
    }

//    public Ad(String id, String title, String shortDesc, String longDesc, String phoneNumber, String location, int visitedNumber, String image) {
//        this.id = id;
//        this.title = title;
//        this.shortDesc = shortDesc;
//        this.longDesc = longDesc;
//        this.phoneNumber = phoneNumber;
//        this.location = location;
//        this.visitedNumber = visitedNumber;
//        this.image = image;
//    }

//    public Ad(String title, String shortDesc, String longDesc, String phoneNumber, String location, ImageView image, int visitedNumber) {
//        this.title = title;
//        this.shortDesc = shortDesc;
//        this.longDesc = longDesc;
//        this.phoneNumber = phoneNumber;
//        this.location = location;
//        this.visitedNumber = visitedNumber;
////        this.image = image;
//    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getVisitedNumber() {
        return visitedNumber;
    }

    public void setVisitedNumber(String visitedNumber) {
        this.visitedNumber = visitedNumber;
    }

    public String getVisibilityRight() {
        return visibilityRight;
    }

    public void setVisibilityRight(String visibilityRight) {
        this.visibilityRight = visibilityRight;
    }

    //    public ImageView getImage() {
//        return image;
//    }
//
//    public void setImage(ImageView image) {
//        this.image = image;
//    }

}
