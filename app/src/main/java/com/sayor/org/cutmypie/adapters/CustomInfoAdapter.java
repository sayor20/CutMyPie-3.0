package com.sayor.org.cutmypie.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.sayor.org.cutmypie.R;

public class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater mInflater;
    byte[] photoData;

    public CustomInfoAdapter(LayoutInflater i, byte[] photoData){
        mInflater = i;
        this.photoData = photoData;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = mInflater.inflate(R.layout.custom_infowindow_layout, null);
        // Populate fields

        TextView title = (TextView) v.findViewById(R.id.tvTitle);
        title.setText(marker.getTitle());
        TextView description = (TextView) v.findViewById(R.id.tvDesc);
        description.setText(marker.getSnippet());
        ImageView ivThumbnail = (ImageView) v.findViewById(R.id.ivThumbnail);
        if(photoData!=null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
            ivThumbnail.setImageBitmap(bitmap);
        }
        // Return info window contents
        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
