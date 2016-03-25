package mosis.myplaces;

/**
 * Created by Neca on 6.3.2016..
 */

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;

public class MyPlacesApplication extends Application {
    private static MyPlacesApplication instance;

    public MyPlacesApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }
}
