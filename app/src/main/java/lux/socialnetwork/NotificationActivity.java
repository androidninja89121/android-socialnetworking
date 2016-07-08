package lux.socialnetwork;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.parse.ParseObject;

public class NotificationActivity extends Activity {

    public static NotificationAdapter Adapter;
    public static ListView NotificationList;
    private SwipyRefreshLayout RefreshAnimation;
    ProgressBar Progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B85D3")));
        //bar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B85D3")));
        bar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Activity</font>"));//

        final ProgressDialog dialog = new ProgressDialog(NotificationActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
        dialog.setMessage("Loading...");
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Hide the refresh after 2sec
                NotificationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        }, 2000);


        //Reload Action
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
                        NotificationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RefreshAnimation.setRefreshing(false);
                                updateHomeList();
                            }
                        });
                    }
                }, 2000);
            }
        });

        ParseObject.registerSubclass(lux.socialnetwork.ParseMethods.Activity.class);
        NotificationList = (ListView) findViewById(R.id.ListView);


        // Initialize ListView and set initial view to Adapter
        Adapter = new NotificationAdapter(this);
        NotificationList.setAdapter(Adapter);
        Adapter.loadObjects();



    }

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

    private void goToMainActivity() {
        Intent Intent = new Intent(this, MainActivity.class);
        startActivity(Intent);
        finish(); // This closes the login screen so it's not on the back stack
    }
    private static void updateHomeList() {
        NotificationList.smoothScrollToPosition(0);
        NotificationList.setAdapter(Adapter);
        Adapter.loadObjects();
    }

}
