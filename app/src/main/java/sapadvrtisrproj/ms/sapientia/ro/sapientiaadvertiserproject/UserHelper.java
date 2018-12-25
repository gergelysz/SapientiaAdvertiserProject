package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject;

import android.text.TextUtils;

public class UserHelper {
    private String userId;
    public UserHelper(String userId){
        if (TextUtils.isEmpty(userId)|| userId.equals("null")){
            this.userId=HomeFragmentLoadingNumber.getUserId();
        }
        else{
            this.userId=userId;
            HomeFragmentLoadingNumber.setUserId(userId);
        }

    }

    public String getUserId() {
        if (TextUtils.isEmpty(userId)|| userId.equals("null")){
            this.userId=HomeFragmentLoadingNumber.getUserId();
            return this.userId;
        }
        else{
            return this.userId;
        }
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
