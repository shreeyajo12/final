package com.example.shreeya;
package com.st.messages;
import java.util.ArrayList;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    EditText editMessage;
    ProgressDialog progressDialog;
    Handler progresshandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                setContentView(R.layout.contact);
                listView = (ListView) findViewById(R.id.contactsView);
                editMessage = (EditText) findViewById(R.id.ed1);

                Cursor c = this.managedQuery(ContactsContract.Data.CONTENT_URI, null,
                        Data.MIMETYPE + "=?", // condition
                        new String[] { ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE }, // value
                        null);

                ArrayList<contacts> contacts = new ArrayList<>();

                while (c.moveToNext()) {
                    int type = c.getInt(c.getColumnIndex(Phone.TYPE));
                    if (type == Phone.TYPE_MOBILE) {
                        contacts con = new contacts(c.getString(c
                                .getColumnIndex(Contacts.DISPLAY_NAME)), c.getString(c
                                .getColumnIndex(Phone.NUMBER)));
                        contacts.add(contacts);
                    }
                }
                listView.setAdapter(new Contactsadapter(this, contacts));

                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Sending Messages.. Please wait!");

                progresshandler = new Handler() {
                    public void handleMessage(Message msg) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Messages Sent",
                                Toast.LENGTH_LONG).show();
                    }
                };
            }

            class SendMessagesThread extends Thread {
                Handler handler;

                public SendMessagesThread(Handler handler) {
                    this.handler = handler;
                }

                public void run() {
                    SmsManager smsManager = SmsManager.getDefault();
                    // Find out which contacts are selected
                    for (int i = 0; i < listView.getCount(); i++) {
                        View item = (View) listView.getChildAt(i);
                        boolean selected = ((CheckBox) item.findViewById(R.id.selected)).isChecked();
                        if (selected) {
                            String mobile = ((TextView) item.findViewById(R.id.mobile)).getText().toString();
                            try {
                                smsManager.sendTextMessage(mobile, null, editMessage.getText().toString(), null, null);
                            } catch (Exception ex) {
                                Log.d("Mobile", "Could not send message to " + mobile);
                            }
                        }
                    }
                    Message m = handler.obtainMessage();
                    handler.sendMessage(m);
                } // run
            } // Thread

            public void MainActivity (View v) {
                if (editMessage.getText().toString().length() > 0) {
                    SendMessagesThread thread = new SendMessagesThread(progresshandler);
                    thread.start();
                    progressDialog.show();
                } else {
                    Toast.makeText(this, "Please enter message!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        }