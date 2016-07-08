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

import java.util.List;

import lux.socialnetwork.ParseMethods.Activity;

/**
 * Created by Yannick Erpelding on 30.07.15.
 */


public class CommentListAdapter extends ParseQueryAdapter<Activity> {

    private List<String> itemList;
    private Context context;
    public ParseFile thumbnailFile;
    ParseImageView ProfilePicView;
    public TextView UsernameView;
    public TextView Comments;
    public ParseUser CommentOwner;



    public CommentListAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Activity>() {
            public ParseQuery create() {

                ParseQuery<Activity> CommentQuery = new ParseQuery<Activity>("Activity");
                CommentQuery.whereEqualTo("photo", CommentActivity.getPhoto());
                CommentQuery.whereEqualTo("type", "comment");
                CommentQuery.orderByAscending("created_at");
                return CommentQuery;

            }
        });

    }

    //@Override
    public View getItemView(final Activity comment, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.comment_list_items, null);
        }
        super.getItemView(comment, v, parent);
        //ParseUser PhotoUser = CommentActivity.getUser();

        String Content = (String) comment.get("content");
        final ParseUser CommentUser = comment.getUser();

        ProfilePicView  = (ParseImageView) v.findViewById(R.id.user_thumbnail);
        UsernameView = (TextView) v.findViewById(R.id.user_name);
        Comments = (TextView) v.findViewById(R.id.comment);
        Comments.setText(Content);

        try {
            UsernameView.setText((String) CommentUser.fetchIfNeeded().get("displayName"));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            thumbnailFile = CommentUser.fetchIfNeeded().getParseFile("profilePictureSmall");
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

        ProfilePicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser fromUser = CommentUser;
                goToProfile(v, fromUser);
            }
        });
        UsernameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser fromUser = CommentUser;
                goToProfile(v,fromUser);
            }
        });
        return v;
    }


    public void goToProfile(View v, ParseUser fromUser){
        Intent myIntent = new Intent(v.getContext(), ProfileActivity.class);
        //myIntent.putExtra("PhotoID", pPhoto.getObjectId());
        myIntent.putExtra("UserID", fromUser.getObjectId());
        v.getContext().startActivity(myIntent);
        Log.e("Log", "Go to Profile");
        Log.e("Notif", "Notif - UserID: " + fromUser.getObjectId());
    }

}


