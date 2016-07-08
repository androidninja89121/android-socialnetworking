package lux.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.Date;

import lux.socialnetwork.ParseMethods.Activity;
import lux.socialnetwork.ParseMethods.Photo;

/**
 * Created by Yannick Erpelding on 30.07.15.
 */


public class NotificationAdapter extends ParseQueryAdapter<Activity> {

    public ParseFile thumbnailFile;
    public ParseFile photoFile;
    ParseImageView ProfilePicView;
    ParseImageView InfoPhoto;
    public TextView UsernameView;
    public TextView Info;
    public TextView TimeStamp;

    public long elapsedYears;
    public long elapsedMonths;
    public long elapsedWeeks;
    public long elapsedDays ;
    public long elapsedHours ;
    public long elapsedMinutes;
    public long elapsedSeconds;
    public static Photo pPhoto;

    public int position = 0;

    String FinalTimeStamp;

    public static ParseUser FromUser;


    public NotificationAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Activity>() {
            public ParseQuery create() {

                ParseQuery<Activity> NotificationQuery = new ParseQuery<Activity>("Activity");
                NotificationQuery.whereEqualTo("toUser", ParseUser.getCurrentUser());
                NotificationQuery.whereNotEqualTo("fromUser", ParseUser.getCurrentUser());
                NotificationQuery.orderByDescending("createdAt");
                return NotificationQuery;

            }
        });

    }

    //@Override
    public View getItemView(final Activity notif, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.notification_list_items, null);
        }
        super.getItemView(notif, v, parent);

        FromUser = (ParseUser) notif.get("fromUser");
        Log.e("Notif", "1 - UserID: " + FromUser.getObjectId());

        ProfilePicView  = (ParseImageView) v.findViewById(R.id.user_thumbnail);
        UsernameView = (TextView) v.findViewById(R.id.user_name);
        TimeStamp = (TextView) v.findViewById(R.id.timestamp);
        Info = (TextView) v.findViewById(R.id.info);
        InfoPhoto = (ParseImageView) v.findViewById(R.id.InfoPhoto);




        try {
            UsernameView.setText((String) FromUser.fetchIfNeeded().get("displayName"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date then = notif.getCreatedAt();
        Date now = new Date();
        now.getTime();
        TimeStamp.setText(Timestamp(then, now));

        String Type = notif.get("type").toString();
        Log.e("Notif", "Type: " + Type);

        if(Type.equals("follow")){
            Info.setText("followes you now");
        }
        if(Type.equals("like")){
            Info.setText("liked your photo");
        }
        if(Type.equals("comment")){
            Info.setText("commented your photo");
        }

        if(Type.equals("follow")){
        }
        else{
            pPhoto = (Photo) notif.get("photo");

            if(pPhoto != null){
                photoFile = pPhoto.getThumbnail();
            }
            if (photoFile != null) {
                InfoPhoto.setParseFile(photoFile);
                InfoPhoto.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {

                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else{
                InfoPhoto.setImageResource(R.drawable.avatarplaceholderprofile);
            }
        }

        try {
            thumbnailFile = FromUser.fetchIfNeeded().getParseFile("profilePictureSmall");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (thumbnailFile != null) {
            ProfilePicView.setParseFile(thumbnailFile);
            ProfilePicView.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {

                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {

            ProfilePicView.setImageResource(R.drawable.avatarplaceholderprofile);
        }



        ProfilePicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser fromUser = (ParseUser) notif.get("fromUser");
                goToProfile(v,fromUser);
            }
        });
        UsernameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser fromUser = (ParseUser) notif.get("fromUser");
                goToProfile(v,fromUser);
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToComments(v, pPhoto);
            }
        });

        if(pPhoto != null){
            InfoPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToComments(v, pPhoto);
                }
            });
        }



        return v;
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

        return FinalTimeStamp;
    }

    public void goToProfile(View v, ParseUser fromUser){
        Intent myIntent = new Intent(v.getContext(), ProfileActivity.class);
        //myIntent.putExtra("PhotoID", pPhoto.getObjectId());
        myIntent.putExtra("UserID", fromUser.getObjectId());
        v.getContext().startActivity(myIntent);
        Log.e("Log", "Go to Profile");
        Log.e("Notif", "Notif - UserID: " + fromUser.getObjectId());
    }

    public static ParseUser getUser(){
        return FromUser;
    }



    public void goToComments(View v, Photo pPhoto){
        String Extra = pPhoto.getObjectId();
        Intent myIntent = new Intent(v.getContext(), CommentActivity.class);
        myIntent.putExtra( "ID", pPhoto.getUser().getObjectId() );
        myIntent.putExtra( "photo", Extra );
        try {
            myIntent.putExtra("displayName", (String) pPhoto.getUser().fetchIfNeeded().get("displayName"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        v.getContext().startActivity(myIntent);
        Log.e("Log", "Go to Comments");
    }

}


