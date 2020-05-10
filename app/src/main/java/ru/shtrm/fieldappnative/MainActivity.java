package ru.shtrm.fieldappnative;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import ru.shtrm.fieldappnative.db.realm.Channel;
import ru.shtrm.fieldappnative.db.realm.MeasuredValue;
import ru.shtrm.fieldappnative.fragments.ChannelsFragment;
import ru.shtrm.fieldappnative.fragments.SettingsFragment;
import ru.shtrm.fieldappnative.rest.ForegroundService;

public class MainActivity extends AppCompatActivity {
    private static final int NO_FRAGMENT = 0;
    private static final int FRAGMENT_CHARTS = 1;
    private static final int FRAGMENT_CHANNELS = 2;
    private static final int FRAGMENT_LAST_VALUES = 5;
    private static final int FRAGMENT_SETTINGS = 10;

    private static final String TAG = "MainActivity";

    private static final int LOGIN = 0;
    private static boolean isExitTimerStart = false;
    public int currentFragment = NO_FRAGMENT;
    Bundle savedInstance = null;
    ProgressDialog mProgressDialog;
    private boolean isLogged = false;
    private ProgressDialog authorizationDialog;
    private boolean splashShown = false;
    private Realm realmDB;

    public static Locale getLocale(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = sharedPreferences.getString("lang_key", "ru");
        return new Locale(lang);
    }

    public static void updateApk(Activity context) {
        if (FieldApplication.serverUrl.equals("")) {
            Toast.makeText(context,
                    context.getString(R.string.not_set_server_address), Toast.LENGTH_LONG)
                    .show();
            return;
        }
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.sync_data));
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setMax(100);
        final Downloader downloaderTask = new Downloader(dialog);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloaderTask.cancel(true);
            }
        });
        String fileName = "last.apk";
        String updateUrl = FieldApplication.serverUrl + "/app/" + fileName;
        File file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File outputFile = new File(file, fileName);
        downloaderTask.execute(updateUrl, outputFile.toString());
        dialog.show();
    }

    /**
     * Инициализация приложения при запуске
     */
    public void init() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // обнуляем текущего активного пользователя
        AuthorizedUser.getInstance().reset();
        if (!initDB()) {
            // принудительное обновление приложения
            finish();
        }
        // запускаем сервис который будет в фоне заниматься получением/отправкой данных
        Intent intent = new Intent(this, ForegroundService.class);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        savedInstance = savedInstanceState;

        // инициализация приложения
        init();
        Log.d(TAG, "onCreate:before read: isLogged=" + isLogged);
        if (savedInstanceState != null) {
            isLogged = savedInstanceState.getBoolean("isLogged");
            Log.d(TAG, "onCreate:after read: isLogged=" + isLogged);
            splashShown = savedInstanceState.getBoolean("splashShown");
            AuthorizedUser aUser = AuthorizedUser.getInstance();
            aUser.setTagId(savedInstanceState.getString("tagId"));
            aUser.setToken(savedInstanceState.getString("token"));
            aUser.setUuid(savedInstanceState.getString("userUuid"));
            aUser.setLogin(savedInstanceState.getString("userLogin"));
            Log.d(TAG, "onCreate finished");
        }

        Log.d(TAG, "onCreate");
        if (splashShown) {
            if (isLogged) {
                setMainLayout(savedInstanceState);
            } else {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivityForResult(loginIntent, LOGIN);
            }
        }
    }

    public boolean initDB() {
        boolean success = false;
        try {
            // получаем базу realm
            realmDB = Realm.getDefaultInstance();
            Log.d(TAG, "Realm DB schema version = " + realmDB.getVersion());
            Log.d(TAG, "db.version=" + realmDB.getVersion());
            if (realmDB.getVersion() == 0) {
                success = true;
            } else {
                Toast toast = Toast.makeText(this, getString(R.string.toast_db_actual), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                success = true;
            }
            RealmResults<MeasuredValue> measuredValues = realmDB.where(MeasuredValue.class).
                    sort("createdAt", Sort.DESCENDING).
                    findAll();
            for (MeasuredValue measuredValue : measuredValues) {
                if (measuredValue.getDate() == null) {
                    realmDB.beginTransaction();
                    measuredValue.deleteFromRealm();
                    realmDB.commitTransaction();
                }
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(this, getString(R.string.toast_db_error),
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }

        return success;
    }

    /**
     * Устанавливам основной экран приложения
     */
    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        final BottomBar bottomBar = findViewById(R.id.bottomBar);
        assert bottomBar != null;
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                switch (tabId) {
                    case R.id.menu_channels:
                        currentFragment = FRAGMENT_CHANNELS;
                        tr.replace(R.id.frame_container, ChannelsFragment.newInstance());
                        break;
/*
                    case R.id.menu_values:
                        currentFragment = FRAGMENT_LAST_VALUES;
                        tr.replace(R.id.frame_container, MeasuresFragment.newInstance());
                        break;
*/
                }

                tr.commit();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(getString(R.string.subtitle_repair));
        toolbar.setTitleTextColor(Color.WHITE);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, ChannelsFragment.newInstance()).commit();
    }

    /**
     * Обработчик клика меню обновления приложения
     *
     * @param menuItem Элемент меню
     */
    public void onActionUpdate(MenuItem menuItem) {
        if (!FieldApplication.isInternetOn(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Нет соединения с сетью", Toast.LENGTH_LONG).show();
            return;
        }
        updateApk(MainActivity.this);
    }


    public void onActionSettings(MenuItem menuItem) {
        Log.d(TAG, "onActionSettings");
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        currentFragment = FRAGMENT_SETTINGS;
        SettingsFragment fragment = new SettingsFragment();
        tr.replace(R.id.frame_container, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_update) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: isLogged=" + isLogged);
        outState.putBoolean("isLogged", isLogged);
        outState.putBoolean("splashShown", splashShown);
        AuthorizedUser authorizedUser = AuthorizedUser.getInstance();
        outState.putString("tagId", authorizedUser.getTagId());
        outState.putString("token", authorizedUser.getToken());
        outState.putString("userUuid", authorizedUser.getUuid());
        outState.putString("userLogin", authorizedUser.getLogin());
        Log.d(TAG, "onSaveInstanceState finished");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /* функция заполняет массив профилей - пользователей */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExitTimerStart) {
                return super.onKeyDown(keyCode, event);
            } else if (currentFragment == FRAGMENT_CHANNELS) {
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        isExitTimerStart = false;
                    }
                };
                handler.postDelayed(runnable, 5000);
                Toast.makeText(this, "Нажмите \"назад\" ещё раз для выхода.", Toast.LENGTH_LONG)
                        .show();
                isExitTimerStart = true;
            } else if (currentFragment == FRAGMENT_SETTINGS) {
                FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                currentFragment = FRAGMENT_CHANNELS;
                tr.replace(R.id.frame_container, ChannelsFragment.newInstance());
                tr.commit();
            } else if (findViewById(R.id.loginButton) != null) {
                return super.onKeyDown(keyCode, event);
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void mOnClickMethod(View view) {
        Intent i = new Intent(MainActivity.this, PrefsActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        if (realmDB != null) {
            realmDB.close();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();

        if (!splashShown) {
            // показываем приветствие
            setContentView(R.layout.start_screen);
            // запускаем таймер для показа экрана входа
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                public void run() {
                    splashShown = true;
                    if (isLogged) {
                        setMainLayout(savedInstance);
                    } else {
                        setContentView(R.layout.login_layout);
                        ImageView iW = findViewById(R.id.login_header);
                        if (iW != null) {
                            SharedPreferences sp = PreferenceManager
                                    .getDefaultSharedPreferences(getApplicationContext());
                        }
                        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivityForResult(loginIntent, LOGIN);
                    }
                }
            }, 3000);
        }

        if (splashShown && !isLogged) {
            //ShowSettings();
        }
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_main);
    }

    private void setLocale() {
        final Resources resources = getResources();
        final Configuration configuration = resources.getConfiguration();
        final Locale locale = getLocale(this);
        if (!configuration.locale.equals(locale)) {
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOGIN:
                if (resultCode == RESULT_OK) {
                    isLogged = true;
                    setMainLayout(savedInstance);
                }
                break;
            default:
                break;
        }
    }
}
