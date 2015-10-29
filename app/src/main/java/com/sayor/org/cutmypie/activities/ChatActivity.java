package com.sayor.org.cutmypie.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sayor.org.cutmypie.R;
import com.sayor.org.cutmypie.adapters.ChatListAdapter;
import com.sayor.org.cutmypie.models.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends ActionBarActivity {
    private ChatListAdapter aChatList;
    private ListView lvChat;
    private EditText etMessage;
    private Button btSend;
    private ArrayList<Message> mMessages, mBuffer;
    private boolean mFirstLoad;
    private String rid,rname,sUserId;
    private static final int MAX_CHAT_MESSAGES_TO_SHOW = 25;
    private ProgressDialog progressDialog;
    private Handler handler;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        rid = i.getStringExtra("receiverId");
        rname = i.getStringExtra("receiverName");

        lvChat = (ListView)findViewById(R.id.lvChat);
        etMessage = (EditText)findViewById(R.id.etMessage);
        btSend = (Button)findViewById(R.id.btSend);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching chats");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        getSupportActionBar().setTitle(rname+" chats");
        mFirstLoad = true;
        sUserId = ParseUser.getCurrentUser().getObjectId();
        mMessages = new ArrayList<Message>();
        mBuffer = new ArrayList<Message>();
        aChatList = new ChatListAdapter(this,sUserId,mMessages);
        handler = new Handler();
        lvChat.setTranscriptMode(1);
        lvChat.setAdapter(aChatList);
        setupMessagePosting();

        //SystemClock.sleep(3000);
        // Run the runnable object defined every 100ms
        //handler.postDelayed(runnable, 100);

    }

    // Setup message field and posting
    private void setupMessagePosting() {
        receiveMessage();
        btSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String body = etMessage.getText().toString();
                // Use Message model to create new messages now
                Message message = new Message();
                message.setUserId(sUserId);
                message.setReceiverId(rid);
                message.setReceiverName(rname);
                message.setBody(body);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            receiveMessage();
                        } else {
                            Toast.makeText(ChatActivity.this, "error in inserting message", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                etMessage.setText("");
            }
        });
    }

    // Query messages from Parse so we can load them into the chat adapter
    private void receiveMessage() {
        // Construct query to execute
        ParseQuery<Message> query1 = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query1.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query1.whereEqualTo("userId", sUserId);
        //query.whereEqualTo("userId", rid);
        query1.whereEqualTo("ReceiverId", rid);
        query1.orderByAscending("ReceiverId");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query1.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mBuffer.addAll(messages);
                    ParseQuery<Message> query2 = ParseQuery.getQuery(Message.class);
                    // Configure limit and sort order
                    query2.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
                    query2.whereEqualTo("userId", rid);
                    //query.whereEqualTo("userId", rid);
                    query2.whereEqualTo("ReceiverId", sUserId);
                    query2.orderByAscending("createdAt");
                    // Execute query to fetch all messages from Parse asynchronously
                    // This is equivalent to a SELECT query with SQL
                    query2.findInBackground(new FindCallback<Message>() {
                        public void done(List<Message> messages, ParseException e) {
                            if (e == null) {
                                mBuffer.addAll(messages);
                                aChatList.addAll(mBuffer);
                                //aChatList.notifyDataSetChanged(); // update adapter
                                progressDialog.dismiss();
                                // Scroll to the bottom of the list on initial load
                                if (mFirstLoad) {
                                    lvChat.setSelection(aChatList.getCount() - 1);
                                    mFirstLoad = false;
                                }
                            } else {
                                Log.d("message", "Error: " + e.getMessage());
                            }
                        }
                    });

                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });


    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            receiveMessage();
       //     handler.postDelayed(this, 100);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

        if (id == R.id.action_logout) {
            Intent i = new Intent(this, LoginActivity.class);
            ParseUser.logOut();
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
