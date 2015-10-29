package com.sayor.org.cutmypie.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sayor.org.cutmypie.R;
import com.sayor.org.cutmypie.adapters.ConversationListAdapter;
import com.sayor.org.cutmypie.models.Conversation;
import com.sayor.org.cutmypie.models.Message;

import java.util.ArrayList;
import java.util.List;


public class ConversationActivity extends ActionBarActivity {

    TextView tvMsg;
    ListView lvConv;
    List bufferList, linkListID, linkListName;
    ConversationListAdapter cAdapter;
    String userID, lastreceiver;
    Conversation conversation;
    List<Conversation> conversations;
    ProgressDialog progressDialog;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvMsg = (TextView) findViewById(R.id.tvMsg);
        lvConv = (ListView) findViewById(R.id.lvConv);
        userID = ParseUser.getCurrentUser().getObjectId();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Getting conversations");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        //convList = new ArrayList<String>();

        //bufferList = new ArrayList<String>();
        //linkListID = new ArrayList<String>();
        //linkListName = new ArrayList<String>();
        conversations = new ArrayList<Conversation>();

        cAdapter = new ConversationListAdapter(this, conversations);
        lvConv.setAdapter(cAdapter);

        lvConv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ConversationActivity.this, ChatActivity.class);
                //i.putExtra("receiverId", (String) linkListID.get(position));
                //i.putExtra("receiverName", (String) linkListName.get(position));
                i.putExtra("receiverId", conversations.get(position).getUserID());
                i.putExtra("receiverName", conversations.get(position).getUserName());
                startActivity(i);
            }
        });
        receiveMessage();
        //SystemClock.sleep(3000);
    }

    private void receiveMessage() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(50);
        query.whereEqualTo("userId", userID);
        //query.whereEqualTo("userId", rid);
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    if(messages==null)
                        tvMsg.setVisibility(View.VISIBLE);
                    else
                        tvMsg.setVisibility(View.INVISIBLE);
                    conversations.clear();
                    lastreceiver = messages.get(0).getReceiverId();
                    conversation = new Conversation(messages.get(0).getReceiverId(), messages.get(0).getReceiverName(), messages.get(0).getBody());
                    conversations.add(conversation);
                   // linkListID.add(messages.get(0).getReceiverId());
                    //linkListName.add(messages.get(0).getReceiverName());
                    for(int i=1;i<messages.size();i++) {
                        if (!lastreceiver.equals(messages.get(i).getReceiverId())) {
                            conversation = new Conversation(messages.get(i).getReceiverId(), messages.get(i).getReceiverName(), messages.get(i).getBody());
                            conversations.add(conversation);
                            //bufferList.add(messages.get(i).getBody());
                            //linkListID.add(messages.get(i).getReceiverId());
                            //linkListName.add(messages.get(i).getReceiverName());
                        }
                        lastreceiver = messages.get(i).getReceiverId();
                    }
                    cAdapter.addAll(conversations);
                    //cAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
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
}
