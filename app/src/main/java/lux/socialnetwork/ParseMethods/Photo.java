package lux.socialnetwork.ParseMethods;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Photo
 */

@ParseClassName("Photo")
public class Photo extends ParseObject {
	ParseFile image;
	public Photo() {
		// A default constructor is required.
	}

    public ParseFile getImage() {
		try {
			 image = fetchIfNeeded().getParseFile("image");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return image;
	}

	public void setImage(ParseFile file) {
		put("image", file);
	}

	public ParseUser getUser() {
		return getParseUser("user");
	}

	public void setUser(ParseUser user) {
		put("user", user);
	}

	public ParseFile getThumbnail() {
		try {
			image = fetchIfNeeded().getParseFile("thumbnail");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return image;
	}

	public void setThumbnail(ParseFile file) {
		put("thumbnail", file);
	}


}
