package lux.socialnetwork;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lux.socialnetwork.ParseMethods.Hashtags;
import lux.socialnetwork.ParseMethods.Photo;

public class CommentActivity extends android.app.Activity {

    public String DisplayName;
    public TextView UsernameView;
    public ParseImageView ProfilePicView;

    EditText CommentInput;
    ImageButton CommentSubmit;
    static Photo pPhoto;
    private String PhotoId;
    public ParseUser PhotoUser;

    ParseImageView PhotoView;
    public ListView CommentList;
    public ParseFile thumbnailFile;
    ProgressBar Progress;
    private SwipyRefreshLayout RefreshAnimation;
    public RelativeLayout LikeBtn;
    public ImageView ImageLike;
    public static boolean liked = false;
    public TextView LikesNum;
    public int likeNumber;


    //private ParseQueryAdapter<ParseObject> Adapter;
    public static CommentListAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        ParseObject.registerSubclass(lux.socialnetwork.ParseMethods.Activity.class);
        ParseObject.registerSubclass(lux.socialnetwork.ParseMethods.Hashtags.class);


        View footer = getLayoutInflater().inflate(R.layout.comment_footer, null);


        //UI
        UsernameView = (TextView) findViewById(R.id.user_name);
        ProfilePicView = (ParseImageView) findViewById(R.id.user_thumbnail);
        CommentList = (ListView) findViewById(R.id.comment_list);
        CommentInput = (EditText) footer.findViewById(R.id.new_comment_input);
        CommentSubmit = (ImageButton) footer.findViewById(R.id.new_comment_send);
        PhotoView = (ParseImageView) findViewById(R.id.image_detail_view);
        Progress = (ProgressBar) findViewById(R.id.progress);
        LikeBtn = (RelativeLayout) findViewById(R.id.LayoutLike);
        ImageLike = (ImageView) findViewById(R.id.imgLike);
        LikesNum = (TextView) findViewById(R.id.like_number);



        //Adapter = new ParseQueryAdapter<ParseObject>(this, "Activity");
        //Adapter.setTextKey("title");



        // Initialize ListView and set initial view to Adapter
        Adapter = new CommentListAdapter(this);
        CommentList.setAdapter(Adapter);

        CommentList.addFooterView(footer);
        Adapter.loadObjects();

        RefreshAnimation = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        RefreshAnimation.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("Log", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Hide the refresh after 2sec
                        CommentActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RefreshAnimation.setRefreshing(false);

                                Adapter.loadObjects();
                            }
                        });
                    }
                }, 2000);
            }
        });


        DisplayName = getIntent().getStringExtra("displayName");
        Log.i("CommentsActivity", "ExtraDis:" + DisplayName);


        PhotoId = getIntent().getStringExtra("photo");

        Progress = new ProgressBar(CommentActivity.this);
        Progress.setVisibility(View.VISIBLE);

        ParseQuery<ParseObject> LikeslQuery = ParseQuery.getQuery("Activity");
        LikeslQuery.whereEqualTo("photo", pPhoto);
        LikeslQuery.whereEqualTo("type", "like");
        LikeslQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        LikeslQuery.countInBackground(new CountCallback() {
            public void done(int countLike, ParseException e) {
            likeNumber = countLike;
                LikesNum.setText(String.valueOf(likeNumber));

            }
        });


        ParseQuery<Photo> UserQuery = ParseQuery.getQuery(Photo.class);
        UserQuery.whereEqualTo("objectId", PhotoId);
        UserQuery.getFirstInBackground(new GetCallback<Photo>() {
            @Override
            public void done(Photo photo, ParseException error) {

                //Progress.setVisibility(View.GONE);

                pPhoto = photo;
                PhotoUser = pPhoto.getUser();

                PhotoView.setParseFile(photo.getImage());
                PhotoView.loadInBackground();

                CommentSubmit.setEnabled(true);
                CommentInput.setEnabled(true);

                ProfilePic(PhotoUser);

                Adapter.loadObjects();


            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();

        ImageLike.setImageResource(R.drawable.buttonlike);

        ParseQuery<Photo> LikeQuery = ParseQuery.getQuery("Activity");
        LikeQuery.whereEqualTo("photo", pPhoto);
        LikeQuery.whereEqualTo("type", "like");
        LikeQuery.whereEqualTo("fromUser", currentUser);
        LikeQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        LikeQuery.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    if (count != 0) {
                        ImageLike.setImageResource(R.drawable.buttonlikeselected);

                    }
                } else {
                    e.printStackTrace();

                }
            }

        });

                //Set Header
        UsernameView.setText(DisplayName);

                ActionBar bar = getActionBar();
        bar.hide();

        LikeBtn.setOnClickListener(new View.OnClickListener() {


                                       @Override
                                       public void onClick(View v) {

                                           ParseUser currentUser = ParseUser.getCurrentUser();

                                           if (liked == false) {

                                               lux.socialnetwork.ParseMethods.Activity likes = new lux.socialnetwork.ParseMethods.Activity();
                                               likes.setToUser(pPhoto.getUser());
                                               likes.setFromUser(currentUser);
                                               likes.setType("like");
                                               likes.setPhoto(pPhoto);
                                               ParseACL acl = new ParseACL(currentUser);
                                               acl.setPublicReadAccess(true);
                                               likes.setACL(acl);
                                               likes.saveInBackground(new SaveCallback() {
                                                   public void done(ParseException e) {
                                                       if (e != null) {
                                                           e.printStackTrace();
                                                       } else {
                                                       }
                                                   }
                                               });

                                               likeNumber = likeNumber - 1;
                                               LikesNum.setText(String.valueOf(likeNumber));

                                           } else if (liked == true) {


                                               ParseQuery<ParseObject> LikeQueryDelete = ParseQuery.getQuery("Activity");
                                               LikeQueryDelete.whereEqualTo("photo", pPhoto);
                                               LikeQueryDelete.whereEqualTo("type", lux.socialnetwork.ParseMethods.Activity.LIKE);
                                               LikeQueryDelete.whereEqualTo("fromUser", currentUser);
                                               LikeQueryDelete.getFirstInBackground(new GetCallback<ParseObject>() {
                                                   public void done(ParseObject object, ParseException e) {
                                                       if (e != null) {
                                                           e.printStackTrace();
                                                           Log.i("like", "Upload Like Failed");
                                                       } else {
                                                           likeNumber++;
                                                           LikesNum.setText(String.valueOf(likeNumber));
                                                           LikesNum.setTextColor(Integer.parseInt("#FFF"));
                                                           try {
                                                               object.delete();
                                                           } catch (ParseException e1) {
                                                               e1.printStackTrace();
                                                               Log.i("Delete-Like", "Error");
                                                           }
                                                       }

                                                   }
                                               });

                                           }
                                       }
        });


        CommentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
            }

            public static Photo getPhoto() {
                return pPhoto;
            }

            public static ParseUser getUser() {
                return pPhoto.getUser();
            }

            private void submitComment() {
                List<String> hashTags = new ArrayList<String>();
                ParseUser currentUser = ParseUser.getCurrentUser();
                String commentText = CommentInput.getText().toString().trim();
                String regexPattern = "(#\\w+)";
                Pattern p = Pattern.compile(regexPattern);
                Matcher m = p.matcher(commentText);
                while (m.find()) {
                    String hashtag = m.group(1);
                    String NOhashtag = hashtag.replace("#", "");
                    hashTags.add(NOhashtag);
                }

                if (commentText.length() > 0 && pPhoto != null) { //photo.objectForKey("user")
                    lux.socialnetwork.ParseMethods.Activity comment = new lux.socialnetwork.ParseMethods.Activity();
                    comment.setContent(commentText);
                    comment.setToUser(pPhoto.getUser());//TODO
                    comment.setFromUser(currentUser);
                    if (hashTags.size() != 0) {
                        comment.put("hashtags", hashTags);

                    }
                    comment.setType(lux.socialnetwork.ParseMethods.Activity.COMMENT);
                    comment.setPhoto(pPhoto);
                    ParseACL acl = new ParseACL(currentUser);
                    acl.setPublicReadAccess(true);
                    acl.setPublicWriteAccess(false);
                    acl.setWriteAccess(currentUser, true);
                    comment.setACL(acl);

                    //cache PAP

                    comment.saveEventually();

                    Matcher me = p.matcher(commentText);

                    while (me.find()) {
                        String hashtext = me.group(1);
                        hashtext = hashtext.replace("#", "");
                        Hashtags Hashtag = new Hashtags();
                        Hashtag.setHashtag(hashtext);
                        Hashtag.saveEventually();
                    }
                }
                CommentInput.setText("");
                Adapter.loadObjects();
            }

            public void ProfilePic(ParseUser User) {

                //Set ProfilePic
                PhotoUser = User;

                Log.i("CommentsActivity", "User: " + PhotoUser);

                try {
                    thumbnailFile = PhotoUser.fetchIfNeeded().getParseFile("profilePictureSmall");
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
                } else {

                    // ProfilePicView.setImageResource(R.drawable.avatarplaceholder);
                }
            }
        }


