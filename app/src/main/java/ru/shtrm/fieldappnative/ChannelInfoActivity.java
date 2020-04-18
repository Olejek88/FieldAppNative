package ru.shtrm.fieldappnative;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.icu.util.Measure;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import ru.shtrm.fieldappnative.db.realm.Channel;
import ru.shtrm.fieldappnative.db.realm.MeasuredValue;

public class ChannelInfoActivity extends AppCompatActivity {
    protected BarChart mChart;

    private final static String TAG = "ChannelInfoActivity";
    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;
    CoordinatorLayout rootLayout;

    private Realm realmDB;
    private static String channel_uuid;
    private TextView tv_channel_name;
    private ListView tv_channel_listview;

    private Context context;

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

        context = this;

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
        tv_channel_listview = findViewById(R.id.channel_listView);
        initView();
    }

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.channel_layout);
        AccountHeader headerResult;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setSubtitle("Сбор данных с поля");

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.login_header)
                .withSavedInstance(savedInstanceState)
                .build();

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Выход").withIcon(FontAwesome.Icon.faw_undo).withIdentifier(DRAWER_EXIT).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == DRAWER_EXIT) {
                                System.exit(0);
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        RecyclerViewCacheUtil.getInstance().withCacheSize(2).init(result);
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(21, false);
        }
    }

    private void initView() {
        final Channel channel = realmDB.where(Channel.class).equalTo("uuid", channel_uuid).findFirst();
        if (channel == null) {
            Toast.makeText(getApplicationContext(), "Неизвестный канал!!", Toast.LENGTH_LONG).show();
            return;
        }

        tv_channel_name.setText(channel.getTitle());
        rootLayout = findViewById(R.id.coordinatorLayout);

        findViewById(R.id.fab_add_measure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        initChart();
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

    private void setData() {
        int count;
        ArrayList<String> xVals = new ArrayList<>();
        XAxis xAxis = mChart.getXAxis();

        List<MeasuredValue> measures = realmDB.where(MeasuredValue.class).equalTo("uuid", channel_uuid).findAll();
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
            if (measures.get(i)!=null) {
                yVals.add(new BarEntry(i, Float.parseFloat(measures.get(i).getValue())));
            }
        }

        BarDataSet set1 = new BarDataSet(yVals, "DataSet");
        //set1.setBarSpacePercent(35f);
        BarData data = new BarData(set1);
        data.setValueTextSize(10f);
        mChart.setData(data);
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
}