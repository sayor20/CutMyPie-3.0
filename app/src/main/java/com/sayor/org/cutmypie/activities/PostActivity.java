package com.sayor.org.cutmypie.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sayor.org.cutmypie.R;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class PostActivity extends ActionBarActivity {

    private EditText etFoodDesc;
    private EditText etFeedCap;
    private EditText etExp;
    private ImageView ivPhoto;
    Bitmap scaledTakenImage;
    Bitmap takenImage;
    ParseFile photoFile;
    ByteArrayOutputStream buffer;
    Location location;
    ProgressDialog progressDialog;

    private String photoFileName ="foodimg.jpg";
    private int CAPTURE_REQUEST_CODE = 234;
    private String APP_TAG = "cutmypie";

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        loadView();
    }

    private void loadView() {
        etFoodDesc = (EditText) findViewById(R.id.etFoodDesc);
        etFeedCap = (EditText) findViewById(R.id.etFeedCap);
        etExp = (EditText) findViewById(R.id.etExp);
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
    }

    public void getLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(bestProvider);
        if(location !=null){
            MenuItem m= (MenuItem)findViewById(R.id.action_post);
            m.setIcon(R.drawable.ic_got_location);
        }else{
            Toast.makeText(this, "error in getting data", Toast.LENGTH_SHORT).show();
        }
    }

    public void getPhoto(){
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //i.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoUri(photoFileName));
        startActivityForResult(i, CAPTURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri takenPhotoUri = getPhotoUri(photoFileName);
                // by this point we have the camera photo on disk
                takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());

                takenImage = (Bitmap) data.getExtras().get("data");
                //resize the image to fit in imageview
                scaledTakenImage = Bitmap.createScaledBitmap(takenImage, 150, 100, true);
                // Load the taken image into a preview
                ivPhoto.setImageBitmap(scaledTakenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getPhotoUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {

            // Get safe storage directory for photos
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public void post(){
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        buffer = new ByteArrayOutputStream();
        scaledTakenImage.compress(Bitmap.CompressFormat.JPEG, 100, buffer);
        photoFile = new ParseFile(buffer.toByteArray());
        photoFile.saveInBackground();

        if(etFoodDesc.getText()==null || etFeedCap.getText()==null ||etExp.getText()==null ||location==null || photoFile==null){
            Toast.makeText(this, "Please provide all the information", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseObject parseObject = new ParseObject("FoodData");
        parseObject.put("fooddesc",etFoodDesc.getText().toString() );
        parseObject.put("feedcap", etFeedCap.getText().toString());
        parseObject.put("timeexp", etExp.getText().toString());
        parseObject.put("lat", location.getLatitude());
        parseObject.put("lon", location.getLongitude());
        parseObject.put("photo", photoFile);
        parseObject.put("ownerid", ParseUser.getCurrentUser().getObjectId());
        parseObject.put("ownername", ParseUser.getCurrentUser().getUsername());
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                    Toast.makeText(PostActivity.this, "Posted Successfully",Toast.LENGTH_SHORT).show();
            }
        });
        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post) {
            post();
            return true;
        }
        if (id == R.id.action_location) {
            getLocation();
            return true;
        }
        if (id == R.id.action_photo) {
            getPhoto();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
