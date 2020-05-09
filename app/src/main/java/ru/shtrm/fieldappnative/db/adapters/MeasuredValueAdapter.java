package ru.shtrm.fieldappnative.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.shtrm.fieldappnative.R;
import ru.shtrm.fieldappnative.db.realm.Channel;
import ru.shtrm.fieldappnative.db.realm.MeasuredValue;

public class MeasuredValueAdapter extends RealmBaseAdapter<MeasuredValue> implements ListAdapter {
    public static final String TABLE_NAME = "MeasuredValue";

    public MeasuredValueAdapter(RealmResults<MeasuredValue> data) {
        super(data);
    }

    @Override
    public MeasuredValue getItem(int position) {
        MeasuredValue measuredValue = null;
        if (adapterData != null) {
            measuredValue = adapterData.get(position);
        }

        return measuredValue;
    }

    @Override
    public long getItemId(int position) {
        MeasuredValue measuredValue;
        if (adapterData != null) {
            measuredValue = adapterData.get(position);
            return measuredValue.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.measure_item_layout, parent, false);
            viewHolder.measure = convertView.findViewById(R.id.measure);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MeasuredValue measuredValue;
        if (adapterData != null && viewHolder.measure != null) {
            measuredValue = adapterData.get(position);
            if (measuredValue != null) {
                String sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US)
                        .format(measuredValue.getDate());
                viewHolder.measure.setText(measuredValue.getValue().concat(" [").concat(sDate).concat("]"));
            }
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, null, parent);
    }

    private static class ViewHolder {
        TextView measure;
    }
}
