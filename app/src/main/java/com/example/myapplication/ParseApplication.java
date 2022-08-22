package com.example.myapplication;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Register your parse models
        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("I7kEBL4stPpDaQt3MddAm8jf4VXC5PGUyCmXmzah")
                .clientKey("Qlrf9QkVmK669KRNxLLcfCVAoqgK0nyJu4wfBlJH")
                .server("https://parseapi.back4app.com")
                .build()

        );
    }

};


