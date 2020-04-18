package ru.shtrm.fieldappnative.rest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import ru.shtrm.fieldappnative.AuthorizedUser;
import ru.shtrm.fieldappnative.R;
import ru.shtrm.fieldappnative.db.realm.MeasuredValue;
import ru.shtrm.fieldappnative.db.realm.ReferenceUpdate;
import ru.shtrm.fieldappnative.fragments.MeasuresFragment;

public class GetResultsService extends Service {
    public static final String ACTION = "ru.shtrm.fieldappnative.rest.GET_ORDERS";
    public static final String ORDER_STATUS_UUIDS = "orderStatusUuids";
    private static final String TAG = GetResultsService.class.getSimpleName();
    private boolean isRuning;
    private Context context;

    /**
     * Метод для выполнения приёма данных с сервера.
     */
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            int count = 0;
            Log.d(TAG, "run() started...");
            AuthorizedUser user = AuthorizedUser.getInstance();
            boolean isValidUser = user.getLogin() != null && user.getToken() != null;
            if (!isValidUser) {
                finishService();
                return;
            }

            // обновляем справочники
            MeasuresFragment.updateReferencesForOrders(context);

            // получаем список последних измерений
            String changedDate = ReferenceUpdate.lastChangedAsStr(MeasuredValue.class.getSimpleName());
            Call<List<MeasuredValue>> measureCall = AppAPIFactory.getMeasuredValueService().get(changedDate);
            try {
                retrofit2.Response<List<MeasuredValue>> r = measureCall.execute();
                List<MeasuredValue> list = r.body();
                if (list != null && list.size() > 0) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(list);
                    realm.commitTransaction();
                    realm.close();
                    count = list.size();
                    ReferenceUpdate.saveReferenceData("MeasuredValue", new Date());
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при получении");
                e.printStackTrace();
            }

            // тестовая реализация штатного уведомления
            // собщаем количество полученных нарядов
            NotificationManager notificationManager = null;
            if (context != null) {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (notificationManager != null) {
                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage("ru.shtrm.fieldappnative");
                if (intent != null) {
                    intent.putExtra("action", "MeasureFragment");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("count", count);

                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
                    NotificationCompat.Builder nb = new NotificationCompat.Builder(context, "toir")
                            .setSmallIcon(R.drawable.toir_notify)
                            .setAutoCancel(true)
                            .setTicker("Получены новые измерения")
                            .setContentText("Полученно " + count + " измерений.")
                            .setContentIntent(contentIntent)
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle("Дата Сервер")
                            .setDefaults(NotificationCompat.DEFAULT_ALL);
                    notificationManager.notify(1, nb.build());
                }
            }

            Log.d(TAG, "run() ended...");
            finishService();
        }
    };

    @Override
    public void onCreate() {
        isRuning = false;
        context = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        if (!isRuning) {
            Log.d(TAG, "Запускаем поток получения с сервера...");
            isRuning = true;
            new Thread(task).start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        isRuning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void finishService() {
        Log.d(TAG, "finishService()");
        stopSelf();
    }
}
