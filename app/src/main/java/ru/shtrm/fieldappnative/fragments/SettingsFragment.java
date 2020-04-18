package ru.shtrm.fieldappnative.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import ru.shtrm.fieldappnative.MainActivity;
import ru.shtrm.fieldappnative.R;

import static ru.shtrm.fieldappnative.MainActivity.updateApk;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String DIALOG_FRAGMENT_TAG =
            "android.support.v7.preference.PreferenceFragment.DIALOG";

    private static final String TAG = "AppSettings";
    private Activity mainActivityConnector = null;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);

        mainActivityConnector = getActivity();
        if (mainActivityConnector == null)
            return;

        Preference updateAppButton = getPreferenceManager().findPreference("updateApp");
        if (updateAppButton != null) {
            updateAppButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    updateApk(mainActivityConnector);
                    return true;
                }
            });
        }

        String appVersion;
        try {
            PackageManager pm = mainActivityConnector.getPackageManager();
            String packageName = mainActivityConnector.getPackageName();
            mainActivityConnector.getApplicationContext().getClassLoader();

            PackageInfo pInfo = pm.getPackageInfo(packageName, 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = "unknown";
        }

        Log.d(TAG, "version:" + appVersion);

        // элемент интерфейса со списком драйверов считывателей
        ListPreference langList = (ListPreference) this.findPreference(getResources().getString(
                R.string.langListKey));
        langList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = (String) newValue;
                setLocale(value);
                Toast.makeText(mainActivityConnector.getApplicationContext(),
                        getString(R.string.lang_warning), Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mainActivityConnector.getBaseContext().getResources().updateConfiguration(config,
                mainActivityConnector.getBaseContext().getResources().getDisplayMetrics());
        mainActivityConnector.getResources().updateConfiguration(config,
                mainActivityConnector.getBaseContext().getResources().getDisplayMetrics());
        Intent intent = new Intent(mainActivityConnector, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity mainActivityConnector = getActivity();
        if (mainActivityConnector == null) {
            onDestroyView();
        }
    }
}
