package ru.shtrm.fieldappnative;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.io.InputStream;

import ru.shtrm.fieldappnative.db.ToirRealm;

/**
 * @author Oleg Ivanov
 */
@SuppressWarnings("unused")
public class FieldApplication extends Application {

    public static String serverUrl = "";
    public static InputStream digicertsha2CA;

    public static boolean isInternetOn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo niMobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (niMobile != null) {
                if (niMobile.getState() == NetworkInfo.State.CONNECTED ||
                        niMobile.getState() == NetworkInfo.State.CONNECTING) {
                    return true;
                }
            }

            NetworkInfo niWiFi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (niWiFi != null) {
                return niWiFi.getState() == NetworkInfo.State.CONNECTED ||
                        niWiFi.getState() == NetworkInfo.State.CONNECTING;
            }
        }

        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //digicertsha2CA = getResources().openRawResource(R.raw.digicertsha2secureserverca);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = preferences.getString(getString(R.string.serverUrl), null);
        String defaultUrl = "http://api.field.net";
        serverUrl = defaultUrl;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp.edit().putString(getString(R.string.serverUrl), defaultUrl).apply();

        // инициализируем синглтон с данными о активном пользователе на уровне приложения
        AuthorizedUser authorizedUser = AuthorizedUser.getInstance();

        // инициализируем базу данных Realm
        ToirRealm.init(this);
    }
}
