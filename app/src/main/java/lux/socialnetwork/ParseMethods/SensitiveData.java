package lux.socialnetwork.ParseMethods;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class SensitiveData extends ParseObject {
    public static String EMAIL = "email";

    public ParseUser getFromUser() {
        return getParseUser("fromUser");
    }

    public ParseUser getToUser() {
        return getParseUser("toUser");
    }

    public String getType() {
        return getString("type");
    }

    public String getContent() {
        return getString("content");
    }

    public void setFromUser(ParseUser fromUser) {
        put("fromUser", fromUser);
    }

    public void setToUser(ParseUser toUser) {
        put("toUser", toUser);
    }

    public void setPhoto(Photo parsePhoto) {
        put("parsePhoto", parsePhoto);
    }

    public void setType(String type) {
        put("type", type);
    }

    public void setContent(String content) {
        put("content", content);
    }

    public void setEmail(String email) {
        put("email", email);
    }

}
