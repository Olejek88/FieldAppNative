package ru.shtrm.fieldappnative.rest;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.fieldappnative.AuthorizedUser;
import ru.shtrm.fieldappnative.R;
import ru.shtrm.fieldappnative.db.realm.MeasuredValue;

public class ForegroundService extends Service {

    private static final String TAG = ForegroundService.class.getSimpleName();
    private static final long START_INTERVAL = 60000;
    private Handler sendResult;
    private Handler getOrder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel();
        } else {
            channelId = "sman";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.toir_notify)
                .setContentText("Дата Сервер")
                .setSubText("Получение/отправка данных");
        Notification notification;
        notification = builder.build();
        startForeground(777, notification);

        // запуск отправки результатов работы на сервер
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startSendResult();
            }
        }, 20000);

        // запуск получения нарядов с сервера
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startGetResults();
            }
        }, 40000);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        String channelId = "sman";
        String channelName = "My Background Service";
        NotificationChannel channel = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (service != null) {
            service.createNotificationChannel(channel);
        }

        return channelId;
    }

    /**
     *
     */
    private void startSendResult() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long[] ids;
                Intent serviceIntent;

                Log.d(TAG, "startSendResult()");

                if (!isValidUser()) {
                    Log.d(TAG, "Нет активного пользователя для отправки на сервер.");
                    // взводим следующий запуск
                    sendResult.postDelayed(this, START_INTERVAL);
                    return;
                }

                // получаем данные для отправки
                AuthorizedUser user = AuthorizedUser.getInstance();
                Realm realm = Realm.getDefaultInstance();
                RealmResults<MeasuredValue> values = realm.where(MeasuredValue.class)
                        .equalTo("user.uuid", user.getUuid())
                        .equalTo("sent", false)
                        .findAll();
                if (values.size() == 0) {
                    Log.d(TAG, "Нет значений для отправки.");
                    ids = null;
                } else {
                    ids = new long[values.size()];
                    for (int i = 0; i < values.size(); i++) {
                        ids[i] = values.get(i).get_id();
                    }
                }

                // стартуем сервис отправки данных на сервер
                Context context = getApplicationContext();
                serviceIntent = new Intent(context, SendResultService.class);
                serviceIntent.setAction(SendResultService.ACTION);
                Bundle bundle = new Bundle();
                bundle.putLongArray(SendResultService.VALUE_IDS, ids);
                serviceIntent.putExtras(bundle);
                context.startService(serviceIntent);
                realm.close();

                // взводим следующий запуск
                sendResult.postDelayed(this, START_INTERVAL);
            }
        };
        sendResult = new Handler();
        sendResult.postDelayed(runnable, START_INTERVAL);
    }

    /**
     *
     */
    private void startGetResults() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "startGetResults()");

                if (!isValidUser()) {
                    Log.d(TAG, "Нет активного пользователя для получения результатов");
                    // взводим следующий запуск
                    getOrder.postDelayed(this, START_INTERVAL);
                    return;
                }

                // стартуем сервис получения новых значений с сервера
                Context context = getApplicationContext();
                Intent serviceIntent = new Intent(context, GetResultsService.class);
                serviceIntent.setAction(GetResultsService.ACTION);
                context.startService(serviceIntent);

                // взводим следующий запуск
                getOrder.postDelayed(this, START_INTERVAL);
            }
        };
        getOrder = new Handler();
        getOrder.postDelayed(runnable, START_INTERVAL);
    }

    private boolean isValidUser() {
        AuthorizedUser user = AuthorizedUser.getInstance();
        boolean isValidUser = user.getLogin() != null && user.getToken() != null;
        return isValidUser;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
