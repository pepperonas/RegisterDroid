package com.pepperonas.registerdroid;

import android.app.Application;
import android.util.Log;

import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.andbasx.AndBasx;

/**
 * @author Martin Pfeffer, Sebastian Grätz
 *         <p>
 *         This class loads the libraries.
 */
public class App extends Application {

    private static final String TAG = "App";


    @Override
    public void onCreate() {
        super.onCreate();

        AndBasx.init(this, AndBasx.LogMode.ALL);

        Log.d(TAG, "onCreate " + AndBasx.Version.getVersionInfo());

        AndBasx.storeLogFileOnExternalStorage("cassandroid.log", true);

        Log.d(TAG, "onCreate  " + AndBasx.Version.getVersionInfo());

        AesPrefs.init(this, "config", "123456789", AesPrefs.LogMode.ALL);
    }

}
