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

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.andbasx.animation.SplashView;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.andbasx.concurrency.ThreadUtils;
import com.pepperonas.andbasx.system.SystemUtils;
import com.pepperonas.jbasx.format.StringFormatUtils;
import com.pepperonas.jbasx.log.Log;
import com.pepperonas.registerdroid.model.DatabaseHelper;
import com.pepperonas.registerdroid.dialogs.DialogSelectRegister;
import com.pepperonas.registerdroid.fragments.FragmentBalance;
import com.pepperonas.registerdroid.fragments.FragmentHistory;
import com.pepperonas.registerdroid.fragments.FragmentSettings;
import com.pepperonas.registerdroid.interfaces.IDrawerHandler;
import com.pepperonas.registerdroid.various.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * @author Martin Pfeffer, Sebastian Grätz
 *         <p/>
 *         The app's main driver (Host-Activity).
 */
public class MainActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   IDrawerHandler {

    private static final String TAG = "MainActivity";

    private Fragment mFragment;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private DatabaseHelper mDb;
    private boolean mIsExitPressedOnce = false;


    /**
     * Activity gets started.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SplashView mSplashView = (SplashView) findViewById(R.id.splash_view);

        ThreadUtils.runDelayed(1500, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mSplashView.finish();
                startApp(savedInstanceState);
                return null;
            }
        });
    }


    private void startApp(Bundle savedInstanceState) {
        if (AesPrefs.getInt(Config.ACTIVE_REGISTER, -1) == -1) {
            AesPrefs.putInt(Config.ACTIVE_REGISTER, 0);
        }

        mDb = new DatabaseHelper(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();

                ImageView iv = (ImageView) findViewById(R.id.iv_logo);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");

                        Intent chooserIntent = Intent.createChooser(MainActivity.this.getIntent(), getString(R.string.select_icon));
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                        startActivityForResult(chooserIntent, Config.REQ_CODE_SELECT_PICTURE);
                    }
                });
            }


            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            /**
             * Prevent {@link FragmentBalance} from recreation.
             */
            makeFragmentTransaction(-1);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        mDb = new DatabaseHelper(this);
    }


    @Override
    protected void onPause() {
        mDb.close();

        super.onPause();
    }


    /**
     * If we start the intent to change the navigation-drawer icon,
     * the {@link MainActivity} gets resumed here.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Config.REQ_CODE_SELECT_PICTURE:
                storeIcon(data);
                break;
        }
    }


    /**
     * Store the icon on the device's storage.
     */
    private void storeIcon(Intent data) {
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("icon", Context.MODE_PRIVATE);
            File path = new File(directory, "custom_app_icon.png");

            InputStream is = MainActivity.this.getContentResolver().openInputStream(data.getData());
            FileOutputStream fos = new FileOutputStream(path);

            int read;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                fos.write(bytes, 0, read);
            }
            fos.close();
            is.close();

            AesPrefs.put(Config.CUSTOM_ICON_PATH, path.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadIconFromStorage();
    }


    /**
     * Load the icon from the device's storage.
     */
    private void loadIconFromStorage() {
        ImageView iv = (ImageView) findViewById(R.id.iv_logo);
        try {
            File f = new File(AesPrefs.get(Config.CUSTOM_ICON_PATH, ""));
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            iv.setImageBitmap(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Overriding to be able to close the {@link NavigationView}.
     * when the BACK-key is touched.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            touchTwiceToExit();
        }
    }


    /**
     * Check double-touch config and close the app if needed.
     */
    private void touchTwiceToExit() {
        if (!AesPrefs.getBoolean(R.string.AP_TOUCH_TWICE_TO_EXIT, true)) mIsExitPressedOnce = true;
        if (mIsExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        ToastUtils.toastShort(R.string.touch_twice_to_close);

        mIsExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsExitPressedOnce = false;
            }
        }, Config.DELAY_ON_BACK_PRESSED);
    }


    /**
     * Set the state of the Drawer when the orientation has changed.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * Handle if an item in the {@link NavigationView} gets selected.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_balance) {
            makeFragmentTransaction(0);
            setToolbarTitle(getString(R.string.nav_item_balance));

        } else if (id == R.id.nav_history) {
            makeFragmentTransaction(1);
            setToolbarTitle(getString(R.string.nav_item_history));

        } else if (id == R.id.nav_register_overview) {
            new DialogSelectRegister(this);
            return true;

        } else if (id == R.id.nav_settings) {
            makeFragmentTransaction(2);
            setToolbarTitle(getString(R.string.nav_item_settings));

        } else if (id == R.id.nav_send) {
            setToolbarTitle(getString(R.string.app_name));

            /**
             * Start an intent to send/share the summary of a
             * {@link com.pepperonas.registerdroid.model.Register}.
             * This will offer the user different apps to send the summary.
             * */
            SystemUtils.sendShareTextIntent(
                    this,
                    new String[]{"martinpaush@gmail.com"},
                    getString(R.string.intent_info_send_mail),
                    getString(R.string.register_print),
                    mDb.generatePrintableRegisterOutput());
        }

        return true;
    }


    /**
     * Replace the {@link Fragment}, which is shown in {@link com.pepperonas.registerdroid.R.id#main_frame}
     * by a {@link android.view.animation.Animation}.
     *
     * @param selection The selection the user has chosen in
     *                  {@link MainActivity#onNavigationItemSelected}.
     */
    public void makeFragmentTransaction(int selection) {
        switch (selection) {
            case 0:
            case -1:
                mFragment = FragmentBalance.newInstance((selection == 0) ? 0 : -1);
                break;

            case 1:
                mFragment = FragmentHistory.newInstance(1);
                break;

            case 2:
                mFragment = FragmentSettings.newInstance(2);
                break;
        }

        android.support.v4.app.FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (selection == -1) {
            fragmentTransaction.setCustomAnimations(R.anim.anim_push_up_in, R.anim.anim_push_down_out);
        } else fragmentTransaction.setCustomAnimations(R.anim.anim_push_left_in, R.anim.anim_push_left_out);

        fragmentTransaction.replace(R.id.main_frame, mFragment);
        fragmentTransaction.commit();
    }


    /**
     * Show the name of the {@link Fragment} in the {@link Toolbar}.
     */
    private void setToolbarTitle(String title) {
        mToolbar.setTitle(title + getString(R.string.for_register) + (AesPrefs.getInt(Config.ACTIVE_REGISTER, 0) + 1));
    }


    /**
     * Show the total amount of money in the {@link Toolbar}.
     *
     * @param value The total amount.
     */
    public void showTotalAsToolbarTitle(int value) {
        mToolbar.setTitle(getString(R.string.total_) + StringFormatUtils.formatDecimalForcePrecision((float) value / 100f, 2) + getString(R.string.euro_sign) +
                          getString(R.string.in_register) + (AesPrefs.getInt(Config.ACTIVE_REGISTER, 0) + 1));
    }


    /**
     * React when the volume rockers are pressed.
     * <p/>
     * This function will in- or decrement the value, which is shown in the focused
     * {@link android.widget.EditText} in {@link FragmentBalance}.
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        FragmentBalance f;

        if (mFragment instanceof FragmentBalance) {
            f = (FragmentBalance) mFragment;
        } else return super.dispatchKeyEvent(event);

        int action = event.getAction();
        switch (event.getKeyCode()) {

            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    f.fireAnUpdate(+1);
                }
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    f.fireAnUpdate(-1);
                }
                return true;

            default:
                return super.dispatchKeyEvent(event);
        }
    }


    /**
     * When a {@link com.pepperonas.registerdroid.model.Register} contains no data,
     * the {@link NavigationView} should not be closed.
     * By this situation we close the Drawer manually only it makes sense.
     */
    @Override
    public void onCloseDrawer() {
        Log.i(TAG, "onCloseDrawer  " + "");
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        mDrawerToggle.syncState();
    }


    /**
     * Provide access to the {@link DatabaseHelper}, so that a {@link Fragment} or
     * {@link com.pepperonas.registerdroid.dialogs.DialogEditEntry} is able to interact with the database.
     */
    public DatabaseHelper getDatabase() {
        return mDb;
    }

}
