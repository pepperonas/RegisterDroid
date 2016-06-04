/*
 * Copyright (c) 2016 Martin Pfeffer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
