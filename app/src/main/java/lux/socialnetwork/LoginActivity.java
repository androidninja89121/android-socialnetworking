package lux.socialnetwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

//import de.keyboardsurfer.android.widget.crouton.Crouton;
//import de.keyboardsurfer.android.widget.crouton.Style;


public class LoginActivity extends Activity {

    private Button FacebookButton;
    private Button LoginButton;
    private Button SignUpButton;
    private EditText Username;
    private EditText Password;
    private TextView Website;
    private String UsernameText;
    private String PasswordText;
    private String TAG = "Log";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Facebook init
        FacebookSdk.setApplicationId("1496868033860464");

        //Change Color of StatusBar
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(Color.parseColor("#2B85D3"));

        //Go to MainActivity when user is already logged in
        final ParseUser current = ParseUser.getCurrentUser();
        if (current == null) {
        }
        else if (current.getObjectId() != null){
            Log.i(TAG,"User:" + current.getUsername() );
            goToMainActivity();
        }

        //All Interface settings
        setContentView(R.layout.activity_login);
        FacebookButton = (Button) findViewById(R.id.FacebookLogin);
        LoginButton = (Button) findViewById(R.id.login_button);
        SignUpButton = (Button) findViewById(R.id.signup_button);
        Username = (EditText) findViewById(R.id.username_field);
        Password = (EditText) findViewById(R.id.password_field);
        Website = (TextView) findViewById(R.id.website);


        //Set text at bottom with Hyperlink
        String text = "Fannt eis och op codelight.lu";
        Website.setText(text);
        Pattern pattern = Pattern.compile("netzwierk.lu");
        Linkify.addLinks(Website, pattern, "http://");
        Website.setLinkTextColor(Color.WHITE);

        //When FacebookButton is clicked
        FacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "FacebookLogin button clicked");
                FacebookButtonClicked();
            }
        });

        //When SingUpButton is clicked
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUpActivity();
            }
        });

        //When LoginButton is clicked
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Login Progress begins...");

                //Getting Strings fom EditTexts
                UsernameText = Username.getText().toString();
                PasswordText = Password.getText().toString();

                //Check if there is something in the EditTexts
                if (UsernameText.length() < 1 || PasswordText.length() < 1){
                    Log.e(TAG, "No Username / No Password");
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage("Passt w.e.g. op dass der all Informatiounen aginn hutt!")
                            .setTitle("Moment...")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {

                    Log.e(TAG, "Everything okay, login to Parse.com");
                    //Starting Parse login process
                    ParseLogin(UsernameText, PasswordText);

                }

            }
        });
    }

    private void FacebookButtonClicked() {

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, Collections.singleton("public_profile"), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.e("MyApp", "Uh oh. The user cancelled the Facebook login.");
                    Log.e("MyApp", "FacebookError: " + err);
                } else if (user.isNew()) {
                    Log.e("MyApp", "User signed up and logged in through Facebook!");
                } else {
                    Log.e("MyApp", "User logged in through Facebook!");
                    goToMainActivity();

                }
            }
        });

    }

    private void getUserDetailsFromFB() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
           /* handle the result */
                        try {
                            String email = response.getJSONObject().getString("email");
                            String name = response.getJSONObject().getString("name");
                            saveNewUser(email, name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

    private void saveNewUser(String email, final String name) {
        final ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.setUsername(name);
        parseUser.setEmail(email);

        //Get rid of the space
        String UsernameOnePiece = name.replace(" ", "");

        //Transform to lowercase
        String UsernameFix = UsernameOnePiece.toLowerCase();

        //Transform to lowercase
        String UsernameLower = name.toLowerCase();

        //Setting up nwe ParseUser
        ParseUser NewUser = new ParseUser();
        NewUser.setUsername(name);
        NewUser.setPassword("0000");
        NewUser.setEmail(email);
        NewUser.put("usernameFix", UsernameFix);
        NewUser.put("displayName", name);
        NewUser.put("displayName_lower", UsernameLower);
        NewUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(LoginActivity.this, "Welcome " + name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void autoFollowDevelopers(){

        List<String> ids = new ArrayList<String>();
        ids.add("cvOHKg4uc8"); //Yannick Erpelding
        ids.add("ylFpROIHx9"); // Eric Schanet

        Log.i("Log", "Query to find friends in Parse...");


        // Select * From Users from User.objectId
        ParseQuery<ParseUser> friendsQuery = ParseUser.getQuery();
        friendsQuery.whereContainedIn("objectId", ids);
        friendsQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects != null) {
                    // friendsQuery successful, follow these users
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    for (ParseUser friend : objects) {
                        lux.socialnetwork.ParseMethods.Activity followActivity = new lux.socialnetwork.ParseMethods.Activity();
                        followActivity.setFromUser(currentUser);
                        followActivity.setToUser(friend);
                        followActivity.setType("follow");
                        followActivity.saveEventually();
                    }
                    currentUser.put("userAlreadyAutoFollowedFacebookFriends", true);
                    currentUser.saveInBackground();
                    Log.i("Log", "Query to find friends in Parse success");
                } else {
                    // friendsQuery failed
                    Log.i("Log", "Query to find friends in Parse failed");
                }
            }
        }); // end findInBackground()

    }

    private void goToMainActivity() {
        Intent Intent = new Intent(this, MainActivity.class);
        startActivity(Intent);
        finish(); // This closes the login screen so it's not on the back stack
    }

    private void goToSignUpActivity() {
        Intent Intent = new Intent(this, SignUpActivity.class);
        startActivity(Intent);

    }

    private void ParseLogin(String s1, String s2) {
        //Starting Progressdialog(Loading window)
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
        dialog.setMessage("Login...");
        dialog.show();

        //Starting ParseLogin
        ParseUser.logInInBackground(s1, s2, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    // Hooray! The user is logged in.
                    Log.e(TAG, "User Logged in!");
                    goToMainActivity();

                    dialog.dismiss();
                } else {
                    Log.e(TAG, "Login Error");
                    Log.e("MyApp", "Error:" + e);

                    Log.d("LoginError:", e.toString());
                    dialog.dismiss();

                    //Handling ParseErrors
                    String ErrorText = null;
                    switch (e.getCode()) {
                        case ParseException.USERNAME_TAKEN:
                            ErrorText = "This username already exists.";
                            break;
                        case ParseException.OBJECT_NOT_FOUND:
                            ErrorText = "Your username and/or password is incorrect.";
                            break;
                        case ParseException.CONNECTION_FAILED:
                            ErrorText = "No connection. Please try again later.";
                            break;
                        default: ErrorText = "LoginError: " + e;
                            break;
                    }

                    //Starting Error window and telling the User what is wrong with his input
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage(ErrorText)
                            .setTitle("Login error.")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialoge = builder.create();
                    dialoge.show();
                    // Login failed. Look at the ParseException to see what happened.
                }

            }
        });

    }
}