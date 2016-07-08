package lux.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import lux.socialnetwork.ParseMethods.Activity;
import lux.socialnetwork.ParseMethods.Photo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Yannick Erpelding on 30.07.15.
 */


public class MainAdapter extends ParseQueryAdapter<Photo> {

    private static TextView LikesNum;
    private TextView CommentsNum;
    private LinearLayout LikeBtn;
    private LinearLayout CommentBtn;
    private LinearLayout ShareButton;
    private static ImageView ImageLike;
    ParseImageView ProfilePicView;
    TextView UsernameView;
    TextView TimeStamp;
    ParseImageView PhotoView;
    ParseFile thumbnailFile;
    ParseUser PhotoUser;

    public long elapsedYears;
    public long elapsedMonths;
    public long elapsedWeeks;
    public long elapsedDays ;
    public long elapsedHours ;
    public long elapsedMinutes;
    public long elapsedSeconds;
    private Uri.Builder builder;

    public static Photo photo;

    public static boolean likeAction = true;

    String FinalTimeStamp;


    public MainAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Photo>() {
            public ParseQuery create() {


                // First, query for the friends whom the current user follows
                ParseQuery<Activity> followingActivitiesQuery = new ParseQuery<Activity>("Activity");
                followingActivitiesQuery.whereMatches("type", "follow");
                followingActivitiesQuery.whereEqualTo("fromUser", ParseUser.getCurrentUser());

                // Get the photos from the Users returned in the previous query
                ParseQuery<Photo> photosFromFollowedUsersQuery = new ParseQuery<Photo>("Photo");
                photosFromFollowedUsersQuery.whereMatchesKeyInQuery("user", "toUser", followingActivitiesQuery);
                photosFromFollowedUsersQuery.whereExists("image");

                // Get the current user's photos
                ParseQuery<Photo> photosFromCurrentUserQuery = new ParseQuery<Photo>("Photo");
                photosFromCurrentUserQuery.whereEqualTo("user", ParseUser.getCurrentUser());
                photosFromCurrentUserQuery.whereExists("image");

                // We create a final compound query that will find all of the photos that were
                // taken by the user's friends or by the user
                ParseQuery<Photo> query = ParseQuery.or(Arrays.asList( photosFromFollowedUsersQuery, photosFromCurrentUserQuery ));
                query.include("user");
                query.orderByDescending("createdAt");
                //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);

                return query;
            }
        });

    }



    //@Override
    public View getItemView( final  Photo pPhoto, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.main_item, null);
        }
        super.getItemView(pPhoto, v, parent);

        ParseObject.registerSubclass(Activity.class);
        //int position = v.getVerticalScrollbarPosition();

        //Log.e("MainAdapter", "Loading Picture Position:::  " + position);



        ParseUser currentUser = ParseUser.getCurrentUser();

        PhotoUser = pPhoto.getUser();

        //UI
        UsernameView = (TextView) v.findViewById(R.id.user_name);
        TimeStamp = (TextView) v.findViewById(R.id.timestamp);
        ProfilePicView = (ParseImageView) v.findViewById(R.id.user_thumbnail);

        PhotoView = (ParseImageView) v.findViewById(R.id.photo);

        LikesNum = (TextView) v.findViewById(R.id.like_number);
        CommentsNum = (TextView) v.findViewById(R.id.comment_number);

        LikeBtn = (LinearLayout) v.findViewById(R.id.LayoutLike);
        LikeBtn.setEnabled(false);
        LikeBtn.setClickable(false);
        ImageLike = (ImageView) v.findViewById(R.id.imgLike);
        CommentBtn = (LinearLayout) v.findViewById(R.id.LayoutComment);
        ShareButton = (LinearLayout) v.findViewById(R.id.LayoutShare);


        final ParseUser PhotoUser = pPhoto.getUser();

        //Set username
        UsernameView.setText((String) PhotoUser.get("displayName"));
        UsernameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile(v, pPhoto);
            }
        });

        //Set Timestamp
        Date then = pPhoto.getCreatedAt();
        Date now = new Date();
        now.getTime();
        TimeStamp.setText(Timestamp(then, now));
        TimeStamp.setGravity(Gravity.RIGHT);


        //Set ProfilePic

        ProfilePicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile(v, pPhoto);

            }
        });
        thumbnailFile = PhotoUser.getParseFile("profilePictureSmall");
        if (thumbnailFile != null) {
            ProfilePicView.setParseFile(thumbnailFile);
            ProfilePicView.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if(e == null){

                    }
                    else {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {

           ProfilePicView.setImageResource(R.drawable.avatarplaceholderprofile);
        }

        //Set Photo
        PhotoView = (ParseImageView) v.findViewById(R.id.photo);
        PhotoView.setPlaceholder(getContext().getResources().getDrawable(R.drawable.placeholder_photo));
        ParseFile photoFile = pPhoto.getImage();
        PhotoView.setParseFile(photoFile);
        PhotoView.loadInBackground();
        PhotoView.setClickable(true);
        PhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToComments(v, pPhoto);

            }
        });




        //Set LikesNumber
        ParseQuery<Activity> photoLikes = new ParseQuery<Activity>("Activity");
        photoLikes.whereEqualTo("photo", pPhoto);
        photoLikes.whereEqualTo("type", "like");
        photoLikes.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        photoLikes.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    if (count == 0) {
                        LikesNum.setText("0 Likes");
                    }
                    if (count == 1)
                        LikesNum.setText(count + "Like");
                    else {
                        LikesNum.setText(count + "Likes");
                    }
                } else {
                    e.printStackTrace();

                }
            }

        });


        //Set CommentsNumber
        ParseQuery<Activity> photoComments = new ParseQuery<Activity>("Activity");
        photoComments.whereEqualTo("photo", pPhoto);
        photoComments.whereEqualTo("type", "comment");
        photoComments.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        photoComments.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    if (count == 0) {
                        CommentsNum.setText("0 Comments");
                    }
                    if (count == 1)
                        CommentsNum.setText(count + "Comment");
                    else {
                        CommentsNum.setText(count + "Comments");
                    }
                } else {
                    e.printStackTrace();

                }
            }

        });


        //See if CurrentUser has already liked the picture
        ParseQuery<Activity> UserPhotoLike = new ParseQuery<Activity>("Activity");
        UserPhotoLike.whereEqualTo("photo", pPhoto);
        UserPhotoLike.whereEqualTo("type", "like");
        UserPhotoLike.whereEqualTo("fromUser", currentUser);
        UserPhotoLike.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        UserPhotoLike.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    if(count == 0){
                        ImageLike.setImageResource(R.drawable.buttonlike);
                    }
                    else{
                        ImageLike.setImageResource(R.drawable.buttonlikeselected);
                    }
                } else {
                    e.printStackTrace();

                }
            }

        });


        CommentBtn.setClickable(true);
        CommentBtn.isEnabled();
        CommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToComments(v, pPhoto);

            }
        });


        ShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage(v, pPhoto);

            }

        });

        photo = pPhoto;
        return v;
    }

   public static void likeButton(int position, ListView list){
       View v = list.getChildAt(position -
               list.getFirstVisiblePosition());
int like;
       if(v == null)
           return;
       if(likeAction == true){
       LikesNum = (TextView) v.findViewById(R.id.like_number);

       String likeText = LikesNum.getText().toString();
       int likeCount = Integer.parseInt(likeText.replaceAll("[\\D]", ""));

       ImageLike = (ImageView) v.findViewById(R.id.imgLike);
       Object tag = ImageLike.getTag();
       like = R.drawable.buttonlike;

       if( tag != null && ((Integer)tag).intValue() == like) {
           //If LikeButton is gray
           like = R.drawable.buttonlikeselected;
           submitLike();
           likeCount++;
       }
       else if ( tag != null && ((Integer)tag).intValue() != like) {
           //If LikeButton is red
           like = R.drawable.buttonlike;
           deleteLike();
           likeCount = likeCount - 1;
       }
       else {
           //If no resource can be found
           like = R.drawable.buttonlikeselected;
           submitLike();
           likeCount++;
       }

       if (likeCount == 1)
           LikesNum.setText(likeCount + "Like");
       else {
           LikesNum.setText(likeCount + "Likes");
       }

       ImageLike.setTag(like);
       ImageLike.setImageResource(like);

       Log.e("Log", "Like-Button clicked");

       //We don't want any button spam, so we have to disable the action a few seconds
       likeAction = false;
       final Handler handler = new Handler();
       handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               // Do something after 3s = 3000ms
               likeAction = true;
           }
       }, 2000);
    }
   }


    public static void deleteLike(){
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> LikeQueryDelete = ParseQuery.getQuery("Activity");
        LikeQueryDelete.whereEqualTo("photo", photo);
        LikeQueryDelete.whereEqualTo("type", lux.socialnetwork.ParseMethods.Activity.LIKE);
        LikeQueryDelete.whereEqualTo("fromUser", currentUser);
        LikeQueryDelete.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    Log.i("LikeDelete", "Download Like Failed");

                } else {
                    try {
                        object.delete();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                        Log.i("", "Error");
                    }
                }
            }
        });

    }

    private static void submitLike() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        lux.socialnetwork.ParseMethods.Activity comment = new lux.socialnetwork.ParseMethods.Activity();
        comment.setToUser(photo.getUser());
        comment.setFromUser(currentUser);
        comment.setType(lux.socialnetwork.ParseMethods.Activity.LIKE);
        comment.setType("like");
        comment.setPhoto(photo);
        ParseACL acl = new ParseACL(ParseUser.getCurrentUser());
        acl.setPublicReadAccess(true);
        comment.setACL(acl);
        comment.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    Log.d("Log", "Like submitted");
                }
            }
        });
    }

    public Uri getUriFromUrl(String PUrl) {
        URL url = null;
        try {
            url = new URL(PUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        builder =  new Uri.Builder()
                .scheme(url.getProtocol())
                .authority(url.getAuthority())
                .appendPath(url.getPath());
        return builder.build();
    }

    private void shareImage(View v, Photo pPhoto) {

        String PUrl = pPhoto.getImage().getUrl();
        Log.e("Log", "ULR = " + PUrl);

        Uri uri = getUriFromUrl(PUrl);

        if(uri == null){
            Log.e("Log", "Uri = " + uri);
            Log.e("Log", "Uri = null");
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_TEXT, "Yay! An awesome photo from Social Network.");
        intent.putExtra(Intent.EXTRA_TITLE, "Share this image");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        Intent openInChooser = new Intent(intent);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, uri);
        v.getContext().startActivity(openInChooser);

    }


public void goToComments(View v, Photo pPhoto){
    String Extra = pPhoto.getObjectId();
    Intent myIntent = new Intent(v.getContext(), CommentActivity.class);
    myIntent.putExtra( "ID", pPhoto.getUser().getObjectId() );
    myIntent.putExtra( "photo", Extra );
    myIntent.putExtra("displayName", (String) pPhoto.getUser().get("displayName"));
    v.getContext().startActivity(myIntent);
    Log.i("Log", "Go to Comments");
    }


    public void goToProfile(View v, Photo pPhoto){
        Intent myIntent = new Intent(v.getContext(), ProfileActivity.class);
        myIntent.putExtra("PhotoID", pPhoto.getObjectId());
        myIntent.putExtra("UserID", pPhoto.getUser().toString());
        v.getContext().startActivity(myIntent);
        Log.i("Log", "Go to Profile");
    }




    public String Timestamp(Date startDate, Date endDate) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;
        long monthsInMilli = daysInMilli * 30;
        long yearsInMilli = monthsInMilli * 12;


        elapsedYears = different / yearsInMilli;
        different = different % yearsInMilli;

        elapsedMonths = different / monthsInMilli;
        different = different % monthsInMilli;

        elapsedWeeks = different / weeksInMilli;
        different = different % weeksInMilli;

        elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        elapsedSeconds = different / secondsInMilli;


        if (elapsedYears == 0 && elapsedMonths == 0 && elapsedWeeks == 0 && elapsedDays == 0 && elapsedHours == 0 && elapsedMinutes == 0) {
            //show only Seconds
            FinalTimeStamp = "" + elapsedSeconds + "s";
        }
        else if (elapsedYears == 0 && elapsedMonths == 0 && elapsedWeeks == 0 && elapsedDays == 0 && elapsedHours == 0) {
            //show only Minutes
            FinalTimeStamp = "" + elapsedMinutes + "m";
        }
        else if (elapsedYears == 0 && elapsedMonths == 0 && elapsedWeeks == 0 && elapsedDays == 0){
            //show only Hours
            FinalTimeStamp = "" + elapsedHours + "h";
        }

        else if (elapsedYears == 0 && elapsedMonths == 0 && elapsedWeeks == 0){
            //show only Days
            FinalTimeStamp = "" + elapsedDays + "d";
        }

        else if (elapsedYears == 0 && elapsedMonths == 0){
            //show only Weeks
            if(elapsedWeeks <= 1){
                FinalTimeStamp = "" + elapsedWeeks + "wk";
            }
            else {
                FinalTimeStamp = "" + elapsedWeeks + "wks";
            }
        }

        else if (elapsedYears == 0){
            //show only Months
            if(elapsedMonths <= 1) {
                FinalTimeStamp = "" + elapsedMonths + "mo";
            }
            else{
                FinalTimeStamp = "" + elapsedMonths + "mos";
            }
        }

        else {
            //show only Years
            if(elapsedYears <= 1) {
                FinalTimeStamp = "" + elapsedYears + "y";
            }
            else{
                FinalTimeStamp = "" + elapsedYears + "ys";
            }
        }

        String timeStamp = "time = " + elapsedDays + "d, " + elapsedHours + "h, " + elapsedMinutes + "m, " + elapsedSeconds + "s, ";

        return FinalTimeStamp;
    }


}


