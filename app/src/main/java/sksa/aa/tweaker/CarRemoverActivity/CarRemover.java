package sksa.aa.tweaker.CarRemoverActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.rm.rmswitch.RMSwitch;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import sksa.aa.tweaker.MainActivity;
import sksa.aa.tweaker.NotSuccessfulDialog;
import sksa.aa.tweaker.R;
import sksa.aa.tweaker.Utils.RecyclerItemClickListener;

import static sksa.aa.tweaker.MainActivity.runSuWithCmd;


public class CarRemover extends AppCompatActivity {

    SharedPreferences.OnSharedPreferenceChangeListener listener;


    @NonNull
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final CarAdapter rvAdapter;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        Bundle b = getIntent().getExtras();
        String path = b.getString("path");

        final SharedPreferences accountsPrefs =  getSharedPreferences("idList", 0);
        SharedPreferences.Editor removeAll = accountsPrefs.edit();
        removeAll.clear();
        removeAll.apply();

        final ArrayList<CarInfo> allCars = new ArrayList<>();
        final Map<String, String> idsToBeRemoved = null;
        final int[] selected = {0};

        String getCars = runSuWithCmd(
                path + "/sqlite3 /data/data/com.google.android.projection.gearhead/databases/carservicedata.db " +
                        "'SELECT manufacturer,model FROM allowedcars;'").getInputStreamLog();


        for (String str : getCars.split(Objects.requireNonNull(System.getProperty("line.separator")))) {
                allCars.add(new CarInfo(str, false));
        }



        String getIds = runSuWithCmd(
                path + "/sqlite3 /data/data/com.google.android.projection.gearhead/databases/carservicedata.db " +
                        "'SELECT vehicleidclient FROM allowedcars;'").getInputStreamLog();

        Log.v("IDTROVATI", getIds);

        int i = -1;

        for (String str2 : getIds.split(Objects.requireNonNull(System.getProperty("line.separator")))) {
            i++;
                allCars.get(i).setId(str2);
                Log.v("IDMESSO", allCars.get(i).getId());
        }

        rvAdapter = new CarAdapter(allCars);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.choose_cars);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.argb(255, 255, 0, 0)));
        fab.setImageResource(R.drawable.trashcan);
        fab.hide();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog dialog = ProgressDialog.show(CarRemover.this, "",
                        getString(R.string.tweak_loading), true);
                final StringBuilder finalCommand = new StringBuilder();

                Map<String, ?> allEntries = accountsPrefs.getAll();

                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        finalCommand.append("DELETE FROM allowedcars WHERE vehicleidclient=\"");
                        finalCommand.append(entry.getKey());
                        finalCommand.append("\";");
                        finalCommand.append(System.getProperty("line.separator"));
                }

                Log.v("Comando finale", String.valueOf(finalCommand));

                runOnUiThread(new Thread() {
                    @Override
                    public void run() {
                        String path = getApplicationInfo().dataDir;

                        Log.v("CarRemover", runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.projection.gearhead/databases/carservicedata.db " + "'" +
                                        finalCommand + "'"
                        ).getStreamLogsWithLabels());

                        Toast.makeText(CarRemover.this, getString(R.string.removed_app_action), Toast.LENGTH_LONG);

                        dialog.dismiss();
                        finish();

                    }
                });

            }
        });


        final RecyclerView recyclerView = findViewById(R.id.apps_info);
        recyclerView.setHasFixedSize(true);


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