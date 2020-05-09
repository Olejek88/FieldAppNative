package ru.shtrm.fieldappnative.db.realm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 2.05.17.
 */
public class Channel extends RealmObject implements ISend {
    @Index
    private long _id;
    @PrimaryKey
    private String uuid;
    private String title;
    private MeasureType measureType;
    private Date createdAt;
    private Date changedAt;
    private boolean sent;

    public static long getLastId() {
        Realm realm = Realm.getDefaultInstance();
        Number lastId = realm.where(Channel.class).max("_id");
        if (lastId == null) {
            lastId = 0;
        }
        realm.close();
        return lastId.longValue();
    }

    public String getLastMeasure() {
        Realm realm = Realm.getDefaultInstance();
        MeasuredValue measuredValue = realm.where(MeasuredValue.class).
                equalTo("channel.uuid", this.getUuid()).
                sort("createdAt",Sort.DESCENDING).findFirst();
        if (measuredValue != null) {
            String sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US)
                    .format(measuredValue.getDate());
            return measuredValue.getValue().concat(" [").concat(sDate).concat("]");
        }
        realm.close();
        return "нет измерений";
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }

    public boolean isSent() {
        return sent;
    }

    @Override
    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MeasureType getMeasureType() {
        return measureType;
    }

    public void setMeasureType(MeasureType measureType) {
        this.measureType = measureType;
    }
}
