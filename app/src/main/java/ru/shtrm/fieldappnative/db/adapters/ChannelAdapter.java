package ru.shtrm.fieldappnative.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.shtrm.fieldappnative.R;
import ru.shtrm.fieldappnative.db.realm.Channel;
import ru.shtrm.fieldappnative.db.realm.MeasuredValue;

public class ChannelAdapter extends RealmBaseAdapter<Channel> implements ListAdapter {

    public static final String TABLE_NAME = "Channel";
    public ChannelAdapter(RealmResults<Channel> data) {
        super(data);
    }

    @Override
    public Channel getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        Channel channel;
        if (adapterData != null) {
            channel = adapterData.get(position);
            return channel.get_id();
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        viewHolder = new ChannelAdapter.ViewHolder();
        if (convertView == null) {
                convertView = inflater.inflate(R.layout.channel_item_layout, parent, false);
                viewHolder = new ChannelAdapter.ViewHolder();
                viewHolder.measure_type = convertView.findViewById(R.id.channel_measure_type);
                viewHolder.last_value = convertView.findViewById(R.id.channel_last_value);
                viewHolder.title = convertView.findViewById(R.id.channel_title);
                convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Channel channel;
        if (adapterData != null) {
            channel = adapterData.get(position);
            if (channel != null) {
                if (channel.getMeasureType() != null) {
                    viewHolder.measure_type.setText(channel.getMeasureType().getTitle());
                }
                viewHolder.title.setText(channel.getTitle());
                viewHolder.last_value.setText(channel.getLastMeasure());
            }
        }

        return convertView;
    }


    private static class ViewHolder {
        TextView title;
        TextView last_value;
        TextView measure_type;
    }
}
