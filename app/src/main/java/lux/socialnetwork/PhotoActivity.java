package lux.socialnetwork;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lux.socialnetwork.ParseMethods.Hashtags;


public class PhotoActivity extends ActionBarActivity {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private ImageView ImageView;
    private Button Cancel;
    private EditText FirstComment;
    private Button Submit;
    private String TAG = "Log";
    private String extra;
    public Bitmap photoBit;
    public Bitmap thumbnailBit;
    public ParseFile photo;
    public ParseFile thumbnail;
    public byte[] photos;
    public byte[] thumbnails;
    public ProgressDialog mProgressDialog;
    public ParseObject foto;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        //ParseObject.registerSubclass(Photo.class);
        ParseObject.registerSubclass(lux.socialnetwork.ParseMethods.Activity.class);
        ParseObject.registerSubclass(Hashtags.class);


        Cancel = (Button) findViewById(R.id.cancel);
        Submit = (Button) findViewById(R.id.submit);
        FirstComment = (EditText) findViewById(R.id.first_comment);
        ImageView = (ImageView) findViewById(R.id.imageView);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B85D3")));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(Color.parseColor("#2B85D3"));

        LinearLayout myLayout = new LinearLayout(this);
        myLayout.setOrientation(LinearLayout.VERTICAL);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            extra = extras.getString("Chosen");
        }

        ChosenPhoto(extra);

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Cancel Clicked");
                goToMainActivity();
            }
        });





        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = new ProgressDialog(PhotoActivity.this);
                // Set progressdialog title
                mProgressDialog.setTitle("Upload...");
                // Set progressdialog message
                mProgressDialog.setIndeterminate(false);
                // Show progressdialog
                mProgressDialog.show();


                Log.e(TAG, "Submit Clicked");
                photo = new ParseFile("file", photos);
                thumbnail = new ParseFile("file", thumbnails);
                Sumbit(photo, thumbnail);
            }
        });


    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "ActivityResult");


        LinearLayout myLayout = new LinearLayout(this);
        myLayout.setOrientation(LinearLayout.VERTICAL);

        switch (requestCode) {
            case PICK_FROM_GALLERY:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Bundle extras = data.getExtras();

                        photoBit = extras.getParcelable("data");
                        ByteArrayOutputStream PhotoStream = new ByteArrayOutputStream();
                        photoBit.compress(Bitmap.CompressFormat.PNG, 100, PhotoStream);
                        photos = PhotoStream.toByteArray();

                        thumbnailBit = Bitmap.createScaledBitmap(photoBit, 86, 86, true);
                        thumbnailBit = extras.getParcelable("data");
                        ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
                        thumbnailBit.compress(Bitmap.CompressFormat.PNG, 100, thumbnailStream);
                        thumbnails = thumbnailStream.toByteArray();

                        ImageView.setImageBitmap(photoBit);
                    }
                }

            case PICK_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Bundle extras = data.getExtras();


                        photoBit = extras.getParcelable("data");
                        ByteArrayOutputStream PhotoStream = new ByteArrayOutputStream();
                        photoBit.compress(Bitmap.CompressFormat.PNG, 100, PhotoStream);
                        photos = PhotoStream.toByteArray();

                        thumbnailBit = Bitmap.createScaledBitmap(photoBit, 86, 86, true);
                        thumbnailBit = extras.getParcelable("data");
                        ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
                        thumbnailBit.compress(Bitmap.CompressFormat.PNG, 100, thumbnailStream);
                        thumbnails = thumbnailStream.toByteArray();


                        ImageView.setImageBitmap(photoBit);
                    }
                }

        }

    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_photo, menu);



            return true;
        }
    private void goToMainActivity() {
        Intent Intent = new Intent(this, MainActivity.class);
        startActivity(Intent);
        finish();
    }

    public void ChosenPhoto(String extra) {
        String gallery = "gallery";
        String camera = "camera";



            if (extra.equals(gallery)) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("return-data", true);
                intent.putExtra("aspectX", 560);
                intent.putExtra("aspectY", 560);
                intent.putExtra("outputX", 560);
                intent.putExtra("outputY", 560);
                intent.putExtra("noFaceDetection", true);
                intent.putExtra("screenOrientation", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(intent, PICK_FROM_GALLERY);
            } else if (extra.equals(camera)) {
                Log.e(TAG, "Action1");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 560);
                intent.putExtra("aspectY", 560);
                intent.putExtra("outputX", 560);
                intent.putExtra("outputY", 560);
                intent.putExtra("noFaceDetection", true);
                intent.putExtra("screenOrientation", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }

    }
    public void Sumbit(ParseFile file, ParseFile thumbnail){
        Log.e(TAG, "Starting Sumbit_Method");
        foto = ParseObject.create("Photo");
        foto.put("user", ParseUser.getCurrentUser());
        foto.put("image", file);
        foto.put("thumbnail", thumbnail);
        foto.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    String PhotoObjectId = foto.getObjectId();

                    Log.e(TAG, "ObjId = " + PhotoObjectId);

                    String firstComment = FirstComment.getText().toString().trim();

                    if(firstComment.length() < 1) {
                        mProgressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                        builder.setMessage("Your photo has been uploaded!")
                                .setTitle("Yay!")
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialoge = builder.create();
                        Log.e(TAG, "Everything worked fine! Let's go back to the MainActivity!");
                        goToMainActivity();

                    }
                    else{
                        submitWithFirstComment(PhotoObjectId, foto);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    builder.setMessage("Please try again")
                            .setTitle("Upload error.")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialoge = builder.create();

                    Log.e(TAG, "We've got some Error here: " + e);
                }
            }
        });
    }



    private void submitWithFirstComment(String fotoId, ParseObject foto) {


        List<String> hashTags = new ArrayList<String>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        String firstComment = FirstComment.getText().toString().trim();
        String regexPattern = "(#\\w+)";
        Pattern p = Pattern.compile(regexPattern);
        Matcher m = p.matcher(firstComment);
        while (m.find()) {
            String hashtag = m.group(1);
            String NOhashtag = hashtag.replace("#", "");
            NOhashtag = NOhashtag.toLowerCase();
            hashTags.add(NOhashtag);
        }

        Log.i(TAG, "11");
        if (firstComment.length() > 0) { //photo.objectForKey("user")
            lux.socialnetwork.ParseMethods.Activity comment = new lux.socialnetwork.ParseMethods.Activity();
            comment.setContent(firstComment);
            comment.setToUser(currentUser);//TODO
            comment.setFromUser(currentUser);
            if(hashTags.size() != 0){
                comment.put("hashtags", hashTags);

            }
            comment.setType(lux.socialnetwork.ParseMethods.Activity.COMMENT);
            comment.put("photo", foto);
            Log.i(TAG, "1");
            ParseACL acl = new ParseACL(currentUser);
            acl.setPublicReadAccess(true);
            comment.setACL(acl);

            //cache PAP

            comment.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if(e == null){
                        Log.e(TAG, "CommentSubmit = OK");
                        mProgressDialog.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                        builder.setMessage("Your photo has been uploaded!")
                                .setTitle("Jaa!")
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialoge = builder.create();
                        Log.e(TAG, "Everything worked fine! Let's go back to the MainActivity!");
                        goToMainActivity();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                        builder.setMessage("Please try again")
                                .setTitle("Upload error.")
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialoge = builder.create();

                        Log.e(TAG, "We've got some Error here in CommentSubmit: " + e);
                    }
                }

            });

            Matcher me = p.matcher(firstComment);

            while (me.find()) {
                String hashtext = me.group(1);
                hashtext = hashtext.replace("#", "");

                Log.e(TAG, "Hashtag = " + hashtext);
                Hashtags Hashtag = new Hashtags();
                Hashtag.setHashtag(hashtext);
                Hashtag.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.e(TAG, "HashtagSubmit = OK");
                            mProgressDialog.dismiss();
                        } else {
                            Log.e(TAG, "HashtagSubmit = ERROR");
                            Log.e(TAG, "HashtagSubmit error = " + e);
                        }
                    }

                });
            }

        }

    }
}







