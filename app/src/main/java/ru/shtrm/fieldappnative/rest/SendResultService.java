package ru.shtrm.fieldappnative.rest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.shtrm.fieldappnative.AuthorizedUser;
import ru.shtrm.fieldappnative.db.realm.Channel;
import ru.shtrm.fieldappnative.db.realm.MeasuredValue;

public class SendResultService extends Service {
    public static final String VALUE_IDS = "valueIds";
    public static final String ACTION = "ru.shtrm.fieldappnative.rest.SEND_VALUES";
    private static final String TAG = SendResultService.class.getSimpleName();
    private boolean isRuning;

    /**
     * Метод для выполнения отправки данных на сервер.
     */
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            AuthorizedUser user = AuthorizedUser.getInstance();
            boolean isValidUser = user.getLogin() != null && user.getToken() != null;
            if (!isValidUser) {
                stopSelf();
                return;
            }

            // массив в который сохраним ид успешно переданных записей (измерения)
            LongSparseArray<String> idUuid;

            // выбираем все для отправки
            Realm realm = Realm.getDefaultInstance();
            RealmResults<Channel> channels = realm.where(Channel.class)
                    .equalTo("sent", false)
                    .findAll();
            // отправляем
            idUuid = sendChannels(realm.copyFromRealm(channels));
            // отмечаем успешно отправленные
            setSendChannels(idUuid, realm);

            // выбираем все неотправленные по каким-то причинам ранее измерения
            List<MeasuredValue> sendOldMeasuredValues = new ArrayList<>();
            RealmResults<MeasuredValue> oldMeasuredValues = realm.where(MeasuredValue.class)
                    .equalTo("sent", false).findAll();
            for (MeasuredValue measuredValue : oldMeasuredValues) {
                // дополнительно проверяем что наряд уже был отправлен
                sendOldMeasuredValues.add(realm.copyFromRealm(measuredValue));
            }

            if (sendOldMeasuredValues.size() > 0) {
                // отправляем измерения на сервер
                idUuid = sendMeasuredValues(sendOldMeasuredValues);
                // отмечаем успешно отправленные измерения
                setSendMeasuredValues(idUuid, realm);
            }
            realm.close();
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        isRuning = false;
        Context context = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        if (!isRuning) {
            Log.d(TAG, "Запускаем поток отправки на сервер...");
            isRuning = true;
            long[] ids = intent.getLongArrayExtra(VALUE_IDS);
            new Thread(task).start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRuning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Отправляем измеренные значения.
     *
     * @param list List<MeasuredValue>
     * @return LongSparseArray<String>
     */
    private LongSparseArray<String> sendMeasuredValues(List<MeasuredValue> list) {
        LongSparseArray<String> idUuid = new LongSparseArray<>();
        Call<ResponseBody> call = AppAPIFactory.getMeasuredValueService().send(list);
        try {
            retrofit2.Response response = call.execute();
            ResponseBody result = (ResponseBody) response.body();
            if (response.isSuccessful()) {
                JSONObject jObj = new JSONObject(result.string());
                // при сохранении данных на сервере произошли ошибки
                // данный флаг пока не используем
//                boolean success = (boolean) jObj.get("success");
                JSONArray data = (JSONArray) jObj.get("data");
                for (int idx = 0; idx < data.length(); idx++) {
                    JSONObject item = (JSONObject) data.get(idx);
                    Long _id = Long.parseLong(item.get("_id").toString());
                    String uuid = item.get("uuid").toString();
                    idUuid.put(_id, uuid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return idUuid;
    }

    /**
     * Отправляем новые каналы
     *
     * @param list List<{@link Channel}>
     * @return LongSparseArray<String>
     */
    private LongSparseArray<String> sendChannels(List<Channel> list) {
        LongSparseArray<String> idUuid = new LongSparseArray<>();
        Call<ResponseBody> call = AppAPIFactory.getChannelService().send(list);
        try {
            retrofit2.Response response = call.execute();
            ResponseBody result = (ResponseBody) response.body();
            if (response.isSuccessful()) {
                JSONObject jObj = new JSONObject(result.string());
                // при сохранении данных на сервере произошли ошибки
                // данный флаг пока не используем
//                boolean success = (boolean) jObj.get("success");
                JSONArray data = (JSONArray) jObj.get("data");
                for (int idx = 0; idx < data.length(); idx++) {
                    JSONObject item = (JSONObject) data.get(idx);
                    Long _id = Long.parseLong(item.get("_id").toString());
                    String uuid = item.get("uuid").toString();
                    idUuid.put(_id, uuid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return idUuid;
    }

    /**
     * Отмечаем успешно отправленные измерения
     *
     * @param idUuid LongSparseArray<String>
     */
    private void setSendMeasuredValues(LongSparseArray<String> idUuid, Realm realm) {
        realm.beginTransaction();
        for (int idx = 0; idx < idUuid.size(); idx++) {
            long _id = idUuid.keyAt(idx);
            String uuid = idUuid.valueAt(idx);
            MeasuredValue value = realm.where(MeasuredValue.class).equalTo("_id", _id)
                    .equalTo("uuid", uuid)
                    .findFirst();
            if (value != null) {
                value.setSent(true);
            }
        }

        realm.commitTransaction();
    }

    /**
     * Отмечаем успешно отправленные каналы
     *
     * @param idUuid LongSparseArray<String>
     */
    private void setSendChannels(LongSparseArray<String> idUuid, Realm realm) {
        realm.beginTransaction();
        for (int idx = 0; idx < idUuid.size(); idx++) {
            String uuid = idUuid.valueAt(idx);
            Channel value = realm.where(Channel.class).equalTo("uuid", uuid).findFirst();
            if (value != null) {
                value.setSent(true);
            }
        }
        realm.commitTransaction();
    }
}
