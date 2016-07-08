package lux.socialnetwork.ParseMethods;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Activity")
public class Activity extends ParseObject {

	public Activity() {
		// A default constructor is required.
	}

	public static String FOLLOW = "follow";
	public static String COMMENT = "comment";
	public static String LIKE = "like";
	
	public ParseUser getFromUser(){
		return getParseUser("fromUser");
	}
	public ParseUser getToUser(){
		return getParseUser("toUser");
	}
	public ParseUser getUser() {
		return getParseUser("fromUser");
	}
	
	public String getType(){
		return getString("type");
	}
	public String getContent(){
		return getString("content");
	}
	
	public void setFromUser(ParseUser fromUser){
		put("fromUser", fromUser);
	}
	public void setToUser(ParseUser toUser){
		put("toUser", toUser);
	}
	public void setPhoto(Photo parsePhoto){
		put("photo", parsePhoto);
	}
	public void setType(String type){
		put("type", type);
	}
	public void setContent(String content){
		put("content", content);
	}
	
}
