package com.esecforte.smsforwarder.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.esecforte.smsforwarder.MyApp;
import com.esecforte.smsforwarder.R;
import com.esecforte.smsforwarder.data.AppPref;
import com.esecforte.smsforwarder.model.SMSForwardEntry;
import com.esecforte.smsforwarder.receivers.SmsReceiver;
import com.esecforte.smsforwarder.ui.base.BaseActivity;
import com.esecforte.smsforwarder.utils.AnalyticsEvents;
import com.esecforte.smsforwarder.utils.AppUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.esecforte.smsforwarder.data.AppPref.SHOW_OPTM_DIALOG;
import static com.esecforte.smsforwarder.data.AppPref.USER_PH_NO_DATA;
import static com.esecforte.smsforwarder.receivers.SmsReceiver.startSplit;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_PERMISSIONS = 1012;
    private static final int MENU_ITEM_ITEM1 = 1;
    List<SMSForwardEntry> smsForwardEntries = new ArrayList<>();
    AppPref appPref;
    private RecyclerView sms_ph_rv;
    private SMSForwardAdapter smsForwardAdapter;
    private View no_data_ly;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appPref = AppPref.getInstance(this);
        initView();
        requestAppPermissions(new
                        String[]{Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE}, R.string.runtime_permissions_txt
                , REQUEST_PERMISSIONS);

    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        if (!appPref.getBool(SHOW_OPTM_DIALOG)) {
            showAutoStartDialog();
            appPref.putBool(SHOW_OPTM_DIALOG, true);
        }
    }



    void initView() {
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        View fab_add = findViewById(R.id.fab_add);
        sms_ph_rv = findViewById(R.id.sms_ph_rv);
        no_data_ly = findViewById(R.id.no_data_ly);
        sms_ph_rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddSmsForwardFragment addSmsForwardFragment = new AddSmsForwardFragment();
                addSmsForwardFragment.id = smsForwardAdapter.getItemCount() + 1;
                addSmsForwardFragment.show(getSupportFragmentManager(), "add");

            }
        });
        fab_add.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSnackbar(findViewById(R.id.main_ly), appPref.getSuccessSmsCount(false) + " message success, " + appPref.getFailedSmsCount(false) + " message failed");
                return true;
            }
        });
        smsForwardEntries = AppUtils.getFormattedSmsEntry(appPref.getString(USER_PH_NO_DATA));
        smsForwardAdapter = new SMSForwardAdapter();
        sms_ph_rv.setAdapter(smsForwardAdapter);
        if (smsForwardEntries.isEmpty())
            no_data_ly.setVisibility(View.VISIBLE);
        else
            no_data_ly.setVisibility(View.GONE);

    }

    public void addEntryData(SMSForwardEntry smsForwardEntry) {
        smsForwardEntries.add(smsForwardEntry);
        smsForwardAdapter.notifyDataSetChanged();
        saveData();
        Analytics.track(AnalyticsEvents.SMS_FORWARD_ADDED);
    }

    public void edit(){
        smsForwardAdapter.notifyDataSetChanged();
        saveData();
        Analytics.track(AnalyticsEvents.SMS_FORWARD_EDITED);

    }



    void saveData() {
        try {
            appPref.putString(USER_PH_NO_DATA, AppUtils.getFormattedSmsEntry(smsForwardEntries));
        } catch (JSONException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

        if (smsForwardEntries.isEmpty())
            no_data_ly.setVisibility(View.VISIBLE);
        else
            no_data_ly.setVisibility(View.GONE);

    }

    private void showAutoStartDialog() {

        Intent intent = new Intent();
        String manufacturer = android.os.Build.MANUFACTURER;
        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
        } else if ("oppo".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
        } else if ("vivo".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
        } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
            intent.setComponent(new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"));
        }
        Context context = this;
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.isEmpty())
            return;
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setCancelable(false);
        builder.setTitle("Optimize App")
                .setMessage("Please enable Auto start permission for better performance !!!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Analytics.track(AnalyticsEvents.AUTO_START_LAUNCHED);
                        enableAutoStart();

                    }
                })
                .show();
    }

    private void enableAutoStart() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"));
            }
            Context context = this;
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_ITEM1, Menu.NONE, "Log");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_ITEM1:
                startActivity(new Intent(this, LogActivity.class));
                return true;

            default:
                return false;
        }
    }

    private class SMSForwardAdapter extends RecyclerView.Adapter<SMSForwardAdapter.ViewHolder> {
        public SMSForwardAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_with_no_list_item, parent, false);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SMSForwardEntry smsForwardEntry = smsForwardEntries.get(position);
            holder.sms_group_title.setText(smsForwardEntry.getGroupName());
            holder.enable_switch.setOnCheckedChangeListener(null);
            holder.enable_switch.setChecked(smsForwardEntry.isEnabled());
            holder.enable_switch.setOnCheckedChangeListener(holder);
            holder.sms_ly.removeAllViews();
            holder.phone_no_ly.removeAllViews();
            for (String sms : smsForwardEntry.getSmsNumbers()) {
                ViewGroup sms_ly = holder.sms_ly;
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.phone_no_item, sms_ly, false);
                chip.setChecked(true);
                chip.setChecked(false);
                chip.setText(sms);
                chip.setCloseIconVisible(false);
                sms_ly.addView(chip);
            }

            for (String sms : smsForwardEntry.getForwardNumbers()) {
                ViewGroup sms_ly = holder.phone_no_ly;
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.phone_no_item, sms_ly, false);
                chip.setChecked(true);
                chip.setChecked(false);
                chip.setCloseIconVisible(false);
                chip.setText(sms);
                sms_ly.addView(chip);
            }
        }

        @Override
        public int getItemCount() {
            return smsForwardEntries.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
            private final TextView sms_group_title;
            private final SwitchCompat enable_switch;
            private final FlexboxLayout sms_ly;
            private final FlexboxLayout phone_no_ly;
            private final Button delete_btn,edit_btn;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                sms_group_title = itemView.findViewById(R.id.sms_group_title);
                enable_switch = itemView.findViewById(R.id.enable_switch);
                sms_ly = itemView.findViewById(R.id.sms_ly);
                phone_no_ly = itemView.findViewById(R.id.phone_no_ly);
                delete_btn = itemView.findViewById(R.id.delete_btn);
                delete_btn.setOnClickListener(this);
                edit_btn = itemView.findViewById(R.id.edit_btn);
                edit_btn.setOnClickListener(this);
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != -1 && adapterPosition < smsForwardEntries.size()) {
                    smsForwardEntries.get(adapterPosition).setEnabled(isChecked);
                    saveData();
                    if (isChecked)
                        Analytics.track(AnalyticsEvents.SMS_FORWARD_ENABLED);
                    else
                        Analytics.track(AnalyticsEvents.SMS_FORWARD_DISABLED);
                }


            }

            @Override
            public void onClick(View v) {
                int adapterPosition = getAdapterPosition();

                if (v==delete_btn &&adapterPosition != -1 && adapterPosition < smsForwardEntries.size()) {
                    smsForwardEntries.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                    saveData();
                    Analytics.track(AnalyticsEvents.SMS_FORWARD_DELETED);
                }else if (v==edit_btn){
                    AddSmsForwardFragment addSmsForwardFragment = new AddSmsForwardFragment();
                    addSmsForwardFragment.mSmsForwardEntry=smsForwardEntries.get(adapterPosition);
                    addSmsForwardFragment.show(getSupportFragmentManager(), "add");

                }

            }
        }
    }

}
