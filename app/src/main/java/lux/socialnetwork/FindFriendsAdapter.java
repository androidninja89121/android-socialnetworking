package lux.socialnetwork;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
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

/**
 * Created by Yannick Erpelding on 30.07.15.
 */


public class FindFriendsAdapter extends ParseQueryAdapter<ParseUser> {

    public ParseFile thumbnailFile;
    ParseImageView ProfilePicView;
    public TextView UsernameView;
    public static TextView PhotoNum;
public static ParseUser User;
    public static boolean followAction = true;
    public static Button FollowBtn;


    public FindFriendsAdapter(Context context) {
        super(context, new QueryFactory<ParseUser>() {
            public ParseQuery<ParseUser> create() {


                ParseQuery<ParseUser> UserQuery = new ParseQuery<ParseUser>("_User");
                if(FindFriendsActivity.getSerchedUsername() != null){
                    UserQuery.whereStartsWith("displayName_lower", FindFriendsActivity.getSerchedUsername());
                }
                UserQuery.orderByDescending("created_at");
                return UserQuery;

            }
        });

    }

    //@Override
    public View getItemView(final ParseUser PUser, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.find_friends_list, null);
        }
        super.getItemView(User, v, parent);

        User = PUser;

        ProfilePicView  = (ParseImageView) v.findViewById(R.id.user_thumbnail);
        UsernameView = (TextView) v.findViewById(R.id.user_name);
        PhotoNum = (TextView) v.findViewById(R.id.photos);
        FollowBtn = (Button) v.findViewById(R.id.FollowButton);

        FollowBtn.setEnabled(false);
        FollowBtn.setClickable(false);

        //Get PhotoNumber
        ParseQuery<Photo> TotalLikeQuery = ParseQuery.getQuery("Photo");
        TotalLikeQuery.whereEqualTo("user", User);
        TotalLikeQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        TotalLikeQuery.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    PhotoNum.setText(count + " Photos");
                    //	Log.i(SocialNetworkApplication.TAG, "Set PhotoNumber Success");
                    Log.e("Log", "Photos:" + count);
                } else {
                    e.printStackTrace();
                    //	Log.i(SocialNetworkApplication.TAG, "Set PhotoNumber Error");
                    Log.e("Log", "PhotoError:" + e);
                    PhotoNum.setText("0 Photos");
                }
            }

        });


        try {
            UsernameView.setText((String) User.fetchIfNeeded().get("displayName"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //See if CurrentUser has already followed the User
        ParseQuery<Activity> UserPhotoLike = new ParseQuery<Activity>("_User");
        UserPhotoLike.whereEqualTo("User", User);
        UserPhotoLike.whereEqualTo("type", "like");
        UserPhotoLike.whereEqualTo("fromUser", ParseUser.getCurrentUser());
        UserPhotoLike.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        UserPhotoLike.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    if(count == 0){
                        FollowBtn.setBackgroundResource(R.drawable.rounded_corners_followen);
                    }
                    else{
                        FollowBtn.setBackgroundResource(R.drawable.rounded_corners_following);
                    }
                } else {
                    e.printStackTrace();

                }
            }

        });

        thumbnailFile = User.getParseFile("profilePictureSmall");
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

        return v;
    }

    public static void FollowButton(int position, ListView list){
        View v = list.getChildAt(position -
                list.getFirstVisiblePosition());
        int follow;
        if(v == null)
            return;
        if(followAction == true){
            FollowBtn = (Button) v.findViewById(R.id.FollowButton);
            Object tag = FollowBtn.getTag();
            follow = R.drawable.rounded_corners_followen;

            if(tag != null ) {
                if (((Integer) tag).intValue() == follow || FollowBtn.getText() == "Follow") {
                    //If FollowButton is gray
                    follow = R.drawable.rounded_corners_following;
                    submitFollow();
                    FollowBtn.setText("Following");
                    FollowBtn.setTextColor(Color.WHITE);
                } else if (((Integer) tag).intValue() != follow || FollowBtn.getText() == "Following") {
                    //If FollowButton is blue
                    follow = R.drawable.rounded_corners_followen;
                    deleteFollow();
                    FollowBtn.setText("Follow");
                } else {
                    //If no resource can be found
                    follow = R.drawable.rounded_corners_following;
                    submitFollow();
                    Log.e("lol", "i'm giving up1");
                    FollowBtn.setText("Following");
                }
            }
            else {
                //If no resource can be found
                follow = R.drawable.rounded_corners_following;
                submitFollow();
                Log.e("lol", "i'm giving up");
                FollowBtn.setText("Following");
            }

            FollowBtn.setTag(follow);
            FollowBtn.setBackgroundResource(follow);

            Log.e("Log", "Follow-Button clicked");

            //We don't want any button spam, so we have to disable the action a few seconds
            followAction = false;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 3s = 3000ms
                    followAction = true;
                }
            }, 2000);
        }
    }

    private static void submitFollow() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        lux.socialnetwork.ParseMethods.Activity followActivity = new lux.socialnetwork.ParseMethods.Activity();
        followActivity.setFromUser(currentUser);
        followActivity.setToUser(User);
        followActivity.setType("follow");
        ParseACL acl = new ParseACL(ParseUser.getCurrentUser());
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        acl.setWriteAccess(ParseUser.getCurrentUser(), true);
        acl.setReadAccess(ParseUser.getCurrentUser(), true);
        followActivity.setACL(acl);
        followActivity.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    Log.e("Log", "Follow submitted");
                }
            }
        });
    }

    public static void deleteFollow() {
        Log.e("Log", " delete follow");

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> FollowQueryDelete = new ParseQuery<ParseObject>("Activity");
        FollowQueryDelete.whereMatches("type", "follow");
        FollowQueryDelete.whereEqualTo("fromUser", currentUser);
        FollowQueryDelete.whereEqualTo("toUser", User);
        FollowQueryDelete.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    Log.i("FollowDelete", "Download Follow Failed");

                } else {
                    Log.i("FollowDelete", "Deleting Follow success");
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


}


