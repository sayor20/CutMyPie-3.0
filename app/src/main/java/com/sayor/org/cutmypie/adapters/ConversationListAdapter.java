package com.sayor.org.cutmypie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sayor.org.cutmypie.R;
import com.sayor.org.cutmypie.models.Conversation;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

public class ConversationListAdapter extends ArrayAdapter<Conversation> {

    public ConversationListAdapter(Context context, List<Conversation> conversations) {
        super(context, 0, conversations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView ivProfile;
        TextView tvProfileName, tvBody;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.conversation_item, parent, false);
        }
        ivProfile = (ImageView)convertView.findViewById(R.id.ivProfile);
        tvProfileName = (TextView)convertView.findViewById(R.id.tvProfileName);
        tvBody = (TextView)convertView.findViewById(R.id.tvProfileBody);

        Conversation conv = getItem(position);
        Picasso.with(getContext()).load(getProfileUrl(conv.getUserID())).into(ivProfile);
        tvProfileName.setText(conv.getUserName());
        tvBody.setText(conv.getBody());

        return convertView;
    }

    // Create a gravatar image based on the hash value obtained from userId
    private static String getProfileUrl(final String userId) {
        String hex = "";
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] hash = digest.digest(userId.getBytes());
            final BigInteger bigInt = new BigInteger(hash);
            hex = bigInt.abs().toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "http://www.gravatar.com/avatar/" + hex + "?d=identicon";
    }
}