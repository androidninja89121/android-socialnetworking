package lux.socialnetwork;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class FindFriendsActivity extends Activity {

    protected Button SearchButton;
    protected EditText searchText;
    public ListView ListView;
    private FindFriendsAdapter Adapter;
    public static String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B85D3")));
        bar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Social Network</font>"));


        ListView = (ListView) findViewById(R.id.ListView);
        SearchButton = (Button)findViewById(R.id.searchButton);
        searchText = (EditText)findViewById(R.id.searchFriend);
        searchText.requestFocus();


        Adapter = new FindFriendsAdapter(this);
        ListView.setAdapter(Adapter);


        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get text from each field in searchField and remove white spaces from any field
                username = searchText.getText().toString().toLowerCase().trim();

                //Check if fields not empty
                if (username.isEmpty()) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(FindFriendsActivity.this);
                    builder.setMessage("You need to put something in the field above.")
                            .setTitle("Nothing in search field")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Adapter.loadObjects();
                }

            }
        });

        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // Photo clicked == parent.getItemAtPosition(position)
                Log.e("Photo clicked", "Position: " + position);
                FindFriendsAdapter.FollowButton(position, ListView);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_friends, menu);
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




    public static String getSerchedUsername(){
        Log.e("FiendFriends", "text:" + username);
        return username;
    }
}
