package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data;

public class HomeFragmentLoadingNumber {
    static private int counter=0;
    static private String userId;
    static public int loadingCounter(){
        return ++counter;
    }
    static public  String getUserId(){
        return  userId;
    }

    static public void setUserId(String user){
        userId=user;
    }
}
