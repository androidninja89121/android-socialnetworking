package lux.socialnetwork;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends Activity {

    public TextView Terms;
    public TextView DeleteAccount;
    public TextView Contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B85D3")));
        bar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B85D3")));
        bar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Astellungen</font>"));//


        // create manager instance after the content view is set
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setTintColor(Color.parseColor("#2777BE"));

        Terms = (TextView) findViewById(R.id.Terms);
        DeleteAccount = (TextView) findViewById(R.id.DeleteAccount);
        Contact = (TextView) findViewById(R.id.Contact);

        Terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.netzwierk.lu/terms"));
                startActivity(intent);
            }
        });

        DeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDelete();
            }
        });

        Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact.setEnabled(false);
                ArrayList<ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();


                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                "Codelight").build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, "support@codelight.lu")
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .build());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap photo = BitmapFactory.decodeResource(SettingsActivity.this.getResources(), R.drawable.codelightlogo);
                photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, baos.toByteArray())
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Website.URL, "www.codelight.lu")
                        .withValue(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_CUSTOM)
                        .build());

                try {
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    //Toast.makeText(SettingsActivity.this, "Falls du Froen hues, kannsde dei roueg stellen! Du hues elo en neien Kontakt um Handy: Codelight. Scheckt eis eng e-mail, an dann reegele mer daat! ", Toast.("")).show();
                    notif();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SettingsActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }


        });




    }

    private void notif(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(SettingsActivity.this)
                        .setSmallIcon(R.drawable.codelightlogo)
                        .setContentTitle("Contact Codelight!")
                        .setContentText("If you have questions, just ask! You now have another contact on your phone: Codelight. Send us an e-mail, and we will help you ASAP!");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, SettingsActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(SettingsActivity.this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(SettingsActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void saveDelete() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Delete Account");
        alertDialogBuilder
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Delete my Account", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    protected void deleteAccount() {
        // Find everything from the CurrentUser, and delete it
        ParseQuery<ParseObject> ActivityQueryFrom = new ParseQuery<ParseObject>("Activity");
        ActivityQueryFrom.whereEqualTo("fromUser", ParseUser.getCurrentUser());
        ActivityQueryFrom.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list.size() != 0)
                    list.get(0).deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getBaseContext(), "Deleted Successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getBaseContext(), "Cant Delete Tickle!" + e.toString(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
            }
        });


        ParseQuery<ParseObject> ActivityQueryTo = new ParseQuery<ParseObject>("Activity");
        ActivityQueryTo.whereEqualTo("toUser", ParseUser.getCurrentUser());
        ActivityQueryTo.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list.size() != 0)
                    list.get(0).deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getBaseContext(), "Deleted Successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getBaseContext(), "Cant Delete Tickle!" + e.toString(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
            }
        });



        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list.size() != 0)
                    list.get(0).deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getBaseContext(), "Deleted Successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getBaseContext(), "Cant Delete Tickle!" + e.toString(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
            }
        });

        ParseQuery<ParseObject> sensitiveQuery = new ParseQuery<ParseObject>("SensitiveData");
        sensitiveQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        sensitiveQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list.size() != 0)
                    list.get(0).deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getBaseContext(), "Deleted Successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getBaseContext(), "Cant Delete Tickle!" + e.toString(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
            }
        });


        try {
            ParseUser.getCurrentUser().delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}

