package de.hahnphilipp.littleminus;

import android.app.Application;
import android.content.Context;

public class LittleMinusApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        context = null;
    }
}
