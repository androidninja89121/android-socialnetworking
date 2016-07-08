package lux.socialnetwork;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import lux.socialnetwork.ParseMethods.Activity;


public class SignUpActivity extends ActionBarActivity {
    public TextView HelpText;
    public EditText Password;
    public EditText Username;
    public EditText Email;
    public Button SignUp;
    public String UsernameText;
    public String PasswordText;
    public String EmailText;
    private String TAG = "Log";
    public ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ParseObject.registerSubclass(Activity.class);

        //Change Color of StatusBar
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(Color.parseColor("#2B85D3"));

        //Interface
        HelpText = (TextView) findViewById(R.id.HelpText);
        Password = (EditText) findViewById(R.id.password_field);
        Username = (EditText) findViewById(R.id.username_field);
        Email = (EditText) findViewById(R.id.email_field);
        SignUp = (Button) findViewById(R.id.register_button);

        //Setting HelpText with Helvetica-Neue-Ultra-Light as Font
        String fontPath = "fonts/helvetica-neue-ultra-light.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        HelpText.setTypeface(tf);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Getting Strings fom EditTexts
                UsernameText = Username.getText().toString();
                PasswordText = Password.getText().toString();
                EmailText = Email.getText().toString();


                //Check if everything is okay with the strings from the EditTexts
                if (UsernameText.length() < 1 || PasswordText.length() < 1 || EmailText.length() < 1) {
                    Log.e(TAG, "No Username / No Password / No Email");
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage("Please make sure you have put in all information!")
                            .setTitle("Hold on...")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if (!isEmailValid(EmailText)) {
                    Log.e(TAG, "No valid Email");
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage("Please make sure that your email is correct!")
                            .setTitle("Hold on...")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    //Starting Progressdialog(Loading window)
                    dialog = new ProgressDialog(SignUpActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
                    dialog.setMessage("Signing up...");
                    dialog.show();

                    //Starting Parse signUp process
                    SignUpProgress(UsernameText, PasswordText, EmailText);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static boolean isEmailValid(String emailText) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return emailText.matches(emailPattern);

    }

    private void goToMainActivity() {
        Intent Intent = new Intent(this, MainActivity.class);
        startActivity(Intent);
        finish(); // This closes the login screen so it's not on the back stack
    }

    private void SignUpProgress(String UsernameFromField, String PasswordFromField, final String EmailFromField) {
        //Take away space and Transform to lowercase
        String UsernameOnePiece = UsernameFromField.replace(" ", "");
        String UsernameFix = UsernameOnePiece.toLowerCase();

        //Transform to lowercase
        String UsernameLower = UsernameFromField.toLowerCase();


        //Setting up nwe ParseUser
        ParseUser NewUser = new ParseUser();
        NewUser.setUsername(UsernameFromField);
        NewUser.setPassword(PasswordFromField);
        NewUser.setEmail(EmailFromField);
        NewUser.put("usernameFix", UsernameFix);
        NewUser.put("displayName", UsernameFromField);
        NewUser.put("displayName_lower", UsernameLower);

        //Giving us all the information about our new User, before we start to register our User
        Log.e(TAG, "UsernameFromField: " + UsernameFromField);
        Log.e(TAG, "PasswordFromField: " + PasswordFromField);
        Log.e(TAG, "EmailFromField: " + EmailFromField);
        Log.e(TAG, "UsernameFix: " + UsernameFix);
        Log.e(TAG, "UsernameLower: " + UsernameLower);


        NewUser.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.e(TAG, "SignUp successful!");
                    autoFollowDevelopers();


                    //When everything is done, we can now go to the MainActivity
                    goToMainActivity();

                    dialog.dismiss();
                }
                else {
                    String ErrorText = null;
                    dialog.dismiss();
                    //Handling ParseErrors
                    e.printStackTrace();
                    Log.e("Log", "Error:" + e);
                    switch (e.getCode()) {
                        case ParseException.USERNAME_TAKEN:
                            ErrorText = "This username already exists.";
                            break;
                        case ParseException.OBJECT_NOT_FOUND:
                            ErrorText = "Username and/or password is wrong";
                            break;
                        case ParseException.CONNECTION_FAILED:
                            ErrorText = "No connection. Please try again later.";
                            break;
                        default:
                            break;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(ErrorText)
                            .setTitle("Hold on...")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    // Login failed. Look at the ParseException to see what happened.
                }
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


        // handle errors from facebook
    }
}
