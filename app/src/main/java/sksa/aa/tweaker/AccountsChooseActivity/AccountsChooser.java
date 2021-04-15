package sksa.aa.tweaker.AccountsChooseActivity;

import android.accounts.Account;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.rm.rmswitch.RMSwitch;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.prefs.PreferenceChangeListener;

import sksa.aa.tweaker.AppInfo;
import sksa.aa.tweaker.MainActivity;
import sksa.aa.tweaker.MyAdapter;
import sksa.aa.tweaker.R;
import sksa.aa.tweaker.StreamLogs;
import sksa.aa.tweaker.Utils.RecyclerItemClickListener;

import static android.content.Context.MODE_PRIVATE;
import static sksa.aa.tweaker.MainActivity.runSuWithCmd;


public class AccountsChooser extends AppCompatActivity {


    @NonNull
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        Bundle b = getIntent().getExtras();
        String path = b.getString("path");

        String getAccounts = runSuWithCmd(
                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                        "'SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC;'").getInputStreamLog();


        ArrayList<AccountInfo> allAccounts = new ArrayList<>();

        final ProgressDialog dialog = ProgressDialog.show(AccountsChooser.this, "",
                getString(R.string.loading), true);
        dialog.show();

        while (getAccounts.length() < 1) {

        }

        dialog.dismiss();



        for (String str : getAccounts.split(Objects.requireNonNull(System.getProperty("line.separator")))) {
            allAccounts.add(new AccountInfo(str, false));
        }

        final int[] selected = {0};

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(getString(R.string.choose_accounts));

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.hide();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (AccountsChooser.this, MainActivity.class);
                Bundle b = new Bundle();
                intent.putExtra("MultiAccounts", true);
                intent.putExtra("xpmode", false);
                startActivity(intent);

            }
        });


        final RecyclerView recyclerView = findViewById(R.id.apps_info);
        recyclerView.setHasFixedSize(true);
        final AccountAdapter rvAdapter = new AccountAdapter(allAccounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        RMSwitch rSwitch = view.findViewById(R.id.checkbox_app);
                        rSwitch.toggle();
                        rvAdapter.onClickSaveAppsWhiteList(view, position);
                        if (rvAdapter.getChecked(position)){
                            selected[0]++;
                            fab.show();
                        } else {
                            selected[0]--;
                            if (selected[0] == 0) {
                                fab.hide();
                            }
                        }
                    }


                    @Override
                    public void onLongItemClick(View view, int position) {
                        //no need
                    }

                })
        );

        recyclerView.setAdapter(rvAdapter);


    }



}