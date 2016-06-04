package com.pepperonas.registerdroid.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.registerdroid.BuildConfig;
import com.pepperonas.registerdroid.MainActivity;
import com.pepperonas.registerdroid.R;

/**
 * @author Martin Pfeffer, Sebastian Grätz
 *         <p/>
 *         Show the preference (not in use).
 */
public class FragmentSettings
        extends com.github.machinarius.preferencefragment.PreferenceFragment
        implements Preference.OnPreferenceClickListener {

    private static final String TAG = "FragmentSettings";


    public static FragmentSettings newInstance(int i) {
        FragmentSettings fragment = new FragmentSettings();

        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);

        addCbxPref();

        addBuildPref();

        ((MainActivity) getActivity()).onCloseDrawer();
    }


    private void addBuildPref() {
        Preference buildVersion = findPreference(getString(R.string.AP_BUILD_VERSION));
        buildVersion.setTitle(R.string.build_version);
        buildVersion.setSummary(BuildConfig.VERSION_NAME);
    }


    private void addCbxPref() {
        CheckBoxPreference cbkTouchTwiceToExit = (CheckBoxPreference) findPreference(getString(R.string.AP_TOUCH_TWICE_TO_EXIT));
        cbkTouchTwiceToExit.setOnPreferenceClickListener(this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity main = (MainActivity) getActivity();
        main.setTitle(getString(R.string.settings));

        updateSummaries();
    }


    private void updateSummaries() {

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated()");
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.AP_TOUCH_TWICE_TO_EXIT))) {
            CheckBoxPreference cbkTouchTwiceToExit = (CheckBoxPreference) findPreference(getString(R.string.AP_TOUCH_TWICE_TO_EXIT));
            AesPrefs.putBoolean(R.string.AP_TOUCH_TWICE_TO_EXIT, cbkTouchTwiceToExit.isChecked());
        }
        return true;
    }

}

