package ru.shtrm.fieldappnative;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import ru.shtrm.fieldappnative.db.realm.Channel;

public class ChannelInfoActivity extends AppCompatActivity {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmDB.close();
    }
}
