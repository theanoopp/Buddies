package in.rgpvnotes.buddies.model;


import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;


public class AppUser {

    private String userId;
    private String userPhone;
    private String userName;
    private String status;
    private String profileUri;
    private String thumbImage;
    private boolean onlineStatus;

    @ServerTimestamp
    private Date lastSeen;

    private ArrayList<String> registrationTokens;

    public AppUser() {
    }

    public AppUser(String userId, String userPhone, String userName, String status, String profileUri, String thumbImage, boolean onlineStatus) {
        this.userId = userId;
        this.userPhone = userPhone;
        this.userName = userName;
        this.status = status;
        this.profileUri = profileUri;
        this.thumbImage = thumbImage;
        this.onlineStatus = onlineStatus;
        this.registrationTokens = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public boolean getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public ArrayList<String> getRegistrationTokens() {
        return registrationTokens;
    }

    public void setRegistrationTokens(ArrayList<String> registrationTokens) {
        this.registrationTokens = registrationTokens;
    }
}
