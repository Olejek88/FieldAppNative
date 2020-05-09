package ru.shtrm.fieldappnative;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import ru.shtrm.fieldappnative.db.adapters.MeasuredValueAdapter;
import ru.shtrm.fieldappnative.db.realm.Channel;
import ru.shtrm.fieldappnative.db.realm.MeasuredValue;

public class ChannelInfoActivity extends AppCompatActivity {
    protected BarChart mChart;

    private final static String TAG = "ChannelInfoActivity";

    private Realm realmDB;
    private static String channel_uuid;
    private TextView tv_channel_name;

    public static void showDialogValue(ViewGroup parent, LayoutInflater inflater, Context context, final Channel channel) {
        final View addChannelLayout;
        addChannelLayout = inflater.inflate(R.layout.add_value, parent, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.menu_enter));
        builder.setView(addChannelLayout);
        builder.setIcon(R.drawable.ic_icon_tasks2);
        builder.setCancelable(false);
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final AlertDialog dialog = builder.create();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText valueView = addChannelLayout.findViewById(R.id.add_title);
                String value = valueView.getText().toString();

                if (!value.equals("")) {
                    Realm realm = Realm.getDefaultInstance();
                    UUID uuid = UUID.randomUUID();
                    Date date = new Date();
                    realm.beginTransaction();
                    long nextId = MeasuredValue.getLastId() + 1;
                    MeasuredValue measuredValue = new MeasuredValue();
                    measuredValue.set_id(nextId);
                    measuredValue.setUuid(uuid.toString().toUpperCase());
                    measuredValue.setChannel(channel);
                    measuredValue.setValue(value);
                    measuredValue.setDate(new Date());
                    measuredValue.setCreatedAt(date);
                    measuredValue.setChangedAt(date);
                    measuredValue.setSent(false);
                    realm.copyToRealmOrUpdate(measuredValue);

                    realm.commitTransaction();
                    realm.close();
                    dialog.dismiss();
                }
            }
        };
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(listener);
    }

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.channel_layout);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this;

        realmDB = Realm.getDefaultInstance();
        Bundle b = getIntent().getExtras();
        if (b != null && b.getString("channel_uuid") != null) {
            channel_uuid = b.getString("channel_uuid");
        } else {
            finish();
            return;
        }

        setContentView(R.layout.channel_layout);
        setMainLayout(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tv_channel_name = findViewById(R.id.channel_text_name);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmDB.close();
    }

    void initChart() {
        mChart = findViewById(R.id.chart1);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.setMaxVisibleValueCount(30);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawLabels(false);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setSpaceTop(15f);
        setData();
    }

    private void initView() {
        final Channel channel = realmDB.where(Channel.class).equalTo("uuid", channel_uuid).findFirst();
        if (channel == null) {
            Toast.makeText(getApplicationContext(), "Неизвестный канал!!", Toast.LENGTH_LONG).show();
            return;
        }

        tv_channel_name.setText(channel.getTitle());
        ListView tv_channel_listview = findViewById(R.id.channel_listView);
        RealmResults<MeasuredValue> values;
        values = realmDB.where(MeasuredValue.class)
                .equalTo("channel.uuid", channel.getUuid())
                .sort("date", Sort.DESCENDING)
                .findAll();
        MeasuredValueAdapter measuredValueAdapter = new MeasuredValueAdapter(values);
        tv_channel_listview.setAdapter(measuredValueAdapter);

        findViewById(R.id.fab_add_measure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogValue((ViewGroup) v.getParent(), getLayoutInflater(), v.getContext(), channel);
            }
        });
        initChart();
    }

    public class MyXAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {
        private ArrayList<String> mValues;

        MyXAxisValueFormatter(ArrayList<String> values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues.get((int) value);
        }
    }

    private void setData() {
        int count;
        ArrayList<String> xVals = new ArrayList<>();
        XAxis xAxis = mChart.getXAxis();

        List<MeasuredValue> measures = realmDB
                .where(MeasuredValue.class)
                .equalTo("channel.uuid", channel_uuid)
                .sort("date", Sort.DESCENDING)
                .findAll();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM", Locale.US);
        count = measures.size();
        for (int i = 0; i < count; i++) {
            MeasuredValue val = measures.get(i);
            if (val != null) {
                Date dateVal = val.getDate();
                if (dateVal != null) {
                    xVals.add(simpleDateFormat.format(dateVal));
                } else {
                    xVals.add("00.00");
                }
            }
        }
        xAxis.setValueFormatter(new MyXAxisValueFormatter(xVals));

        List<BarEntry> yVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (measures.get(i) != null) {
                yVals.add(new BarEntry(i, Float.parseFloat(measures.get(i).getValue())));
            }
        }

        BarDataSet set1 = new BarDataSet(yVals, "DataSet");
        //set1.setBarSpacePercent(35f);
        BarData data = new BarData(set1);
        data.setValueTextSize(10f);
        mChart.setData(data);
    }
}