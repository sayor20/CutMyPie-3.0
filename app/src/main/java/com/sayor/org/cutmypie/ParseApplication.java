package com.sayor.org.cutmypie;

import com.parse.Parse;
import com.parse.ParseObject;
import com.sayor.org.cutmypie.models.Message;

public class ParseApplication extends com.activeandroid.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();

        // Register parse models
        // ParseObject.registerSubclass(FoodData.class);
        ParseObject.registerSubclass(Message.class);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "pvoUWtH7upbjefp5UComlHmjoYVCEK7Fb3OkMZqc", "j4UnaWOHu7g2HPJ4GAXtjXMeXSoNE5uOkaqP18fs");

    }
}
