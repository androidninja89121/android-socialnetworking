package lux.socialnetwork;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

import lux.socialnetwork.ParseMethods.Activity;
import lux.socialnetwork.ParseMethods.Hashtags;
import lux.socialnetwork.ParseMethods.Photo;
import lux.socialnetwork.ParseMethods.SensitiveData;

public class SocialNetworkApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());

        //Register ParseMethods
        ParseObject.registerSubclass(Photo.class);
        ParseObject.registerSubclass(Activity.class);
        ParseObject.registerSubclass(Hashtags.class);

        // Add your initialization code here
        Parse.initialize(this, "dk6SeFe6vRDKOghKJseDVCmeXVfcN9zpvnzmlFWe", "yTcYZYJXlw6kRx9Ol0seKJbChCb6Q2b9CX8AacjI");

        //Facebook init
        ParseFacebookUtils.initialize(getApplicationContext());

        //Facebook init
        FacebookSdk.setApplicationId("949988298379222");

        //Enable automatic user
        ParseUser.enableAutomaticUser();


    }
}
