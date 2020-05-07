package ru.shtrm.fieldappnative.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.fieldappnative.ChannelInfoActivity;
import ru.shtrm.fieldappnative.R;
import ru.shtrm.fieldappnative.db.adapters.ChannelAdapter;
import ru.shtrm.fieldappnative.db.adapters.MeasureTypeAdapter;
import ru.shtrm.fieldappnative.db.realm.Channel;
import ru.shtrm.fieldappnative.db.realm.MeasureType;

public class ChannelsFragment extends Fragment {
    private static final String TAG;

    static {
        TAG = ChannelsFragment.class.getSimpleName();
    }

    private Realm realmDB;
    private boolean isInit;
    private Spinner typeSpinner;
    private ListView channelListView;

    public static ChannelsFragment newInstance() {
        return new ChannelsFragment();
    }

    public static void showDialogChannel(ViewGroup parent, LayoutInflater inflater, Context context) {
        final View addChannelLayout;
        final Spinner measureTypeSpinner;
        final MeasureTypeAdapter measureTypeAdapter;
        addChannelLayout = inflater.inflate(R.layout.add_channel, parent, false);
        measureTypeSpinner = addChannelLayout.findViewById(R.id.spinner_measure_type);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<MeasureType> measureType = realm.where(MeasureType.class).findAll();
        measureTypeAdapter = new MeasureTypeAdapter(measureType);
        measureTypeSpinner.setAdapter(measureTypeAdapter);
        realm.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.menu_add_channel));
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
                EditText titleEdit = addChannelLayout.findViewById(R.id.add_title);
                String title = titleEdit.getText().toString();
                MeasureType currentMeasureType = null;

                int position = measureTypeSpinner.getSelectedItemPosition();
                if (position != AdapterView.INVALID_POSITION) {
                    currentMeasureType = measureTypeAdapter.getItem(position);
                }
                if (currentMeasureType != null) {
                    Realm realm = Realm.getDefaultInstance();
                    UUID uuid = UUID.randomUUID();
                    Date date = new Date();
                    realm.beginTransaction();
                    long nextId = Channel.getLastId() + 1;
                    Channel channel = new Channel();
                    channel.set_id(nextId);
                    channel.setUuid(uuid.toString().toUpperCase());
                    channel.setMeasureType(currentMeasureType);
                    channel.setTitle(title);
                    channel.setCreatedAt(date);
                    channel.setChangedAt(date);
                    channel.setSent(false);
                    realm.copyToRealmOrUpdate(channel);

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

    private void initView() {
        FillListViewChannels(null);
    }

    private void FillListViewChannels(String channelTypeUuid) {
        RealmResults<Channel> channels;
        if (channelTypeUuid != null) {
            channels = realmDB.where(Channel.class)
                    .equalTo("MeasureType.uuid", channelTypeUuid)
                    .findAll();
        } else {
            channels = realmDB.where(Channel.class).findAll();
        }

        ChannelAdapter channelAdapter = new ChannelAdapter(channels);
        channelListView.setAdapter(channelAdapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isInit) {
            initView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }

    private class ListviewClickListener implements
            AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parentView,
                                View selectedItemView, int position, long id) {
            Channel channel = (Channel) parentView.getItemAtPosition(position);
            if (channel != null) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }

                Intent equipmentInfo = new Intent(activity, ChannelInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("channel_uuid", channel.getUuid());
                equipmentInfo.putExtras(bundle);
                activity.startActivity(equipmentInfo);
            }
        }
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener {
        //boolean userSelect = false;

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }

        @Override
        public void onItemSelected(AdapterView<?> parentView,
                                   View selectedItemView, int position, long id) {

            String type = null;
            MeasureType typeSelected = (MeasureType) typeSpinner.getSelectedItem();
            if (typeSelected != null) {
                type = typeSelected.getUuid();
            }
            FillListViewChannels(type);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.channels_layout, container, false);
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setSubtitle(getString(R.string.menu_channels));
        }
        realmDB = Realm.getDefaultInstance();

        // обработчик для выпадающих списков у нас один
        SpinnerListener spinnerListener = new SpinnerListener();
        channelListView = rootView.findViewById(R.id.channels_listView);

        RealmResults<MeasureType> measureTypes = realmDB.where(MeasureType.class).findAll();
        typeSpinner = rootView.findViewById(R.id.simple_spinner);
        MeasureTypeAdapter typeSpinnerAdapter = new MeasureTypeAdapter(measureTypes);
        typeSpinnerAdapter.notifyDataSetChanged();
        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinner.setOnItemSelectedListener(spinnerListener);

        channelListView.setOnItemClickListener(new ListviewClickListener());

        FloatingActionButton addButton = rootView.findViewById(R.id.fab_add_channel);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        return true;
                    }
                });
            }
        });
        initView();
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        isInit = true;

        return rootView;
    }
}
