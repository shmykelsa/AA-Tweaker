package sksa.aa.tweaker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rd.PageIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        copyAssets();
        SharedPreferences mysharedpreferences = getPreferences(MODE_PRIVATE);
        final String path = getApplicationInfo().dataDir;
        loadStatus(mysharedpreferences, path);

        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewpager);
        CommonPageAdapter adapter = new CommonPageAdapter();
        adapter.insertViewId(R.id.page_one);
        adapter.insertViewId(R.id.page_two);

        viewPager.setAdapter(adapter);



        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Button toapp = findViewById(R.id.toapp_button);
        toapp.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, AppsList.class);
                        startActivity(intent);
                    }
                }
        );

        Button rebootbutton = findViewById(R.id.reboot_button);
        final DialogFragment rebootDialog = new RebootDialog();
        rebootbutton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        rebootDialog.show(getSupportFragmentManager(), "RebootDialog");
                    }
                }
        );

        final Button rebootButton = findViewById(R.id.reboot_button);
        final Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(600);
        anim.setStartOffset(0);
        anim.setFillAfter(true);
        rebootButton.setAnimation(anim);

        final Boolean[] animationRun = {false};

        final Button nospeed = findViewById(R.id.nospeed);
        final ImageView nospeedimg = findViewById(R.id.speedhackstatus);
        if(load("aa_speed_hack")) {
            nospeed.setText("Re-enable " + getText(R.string.unlimited_scrolling_when_driving));
            nospeedimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            nospeedimg.setColorFilter(Color.argb(255,0,255,0));
        } else {
            nospeed.setText("Disable " + getText(R.string.unlimited_scrolling_when_driving));
            nospeedimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            nospeedimg.setColorFilter(Color.argb(255,255,0,0));
        }

        nospeed.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_speed_hack")){
                            revert("aa_speed_hack");
                            nospeed.setText("Re-enable " + getText(R.string.unlimited_scrolling_when_driving));
                            nospeedimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            nospeedimg.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchforspeed(view);
                            nospeedimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            nospeedimg.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button assistshort = findViewById(R.id.assistshort);
        final ImageView assisthackimg = findViewById(R.id.shortcutstatus);
        if(load("assist_short")) {
            assistshort.setText("Disable " + getText(R.string.enable_assistant_shortcuts));
            assisthackimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            assisthackimg.setColorFilter(Color.argb(255,0,255,0));
        } else {
            assistshort.setText("Enable " + getText(R.string.enable_assistant_shortcuts));
            assisthackimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            assisthackimg.setColorFilter(Color.argb(255,255,0,0));
        }


        assistshort.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("assist_short")){
                            revert("assist_short");
                            assistshort.setText("Enable " + getText(R.string.enable_assistant_shortcuts));
                            assisthackimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            assisthackimg.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchforassistshort(view);
                            assistshort.setText("Disable " + getText(R.string.enable_assistant_shortcuts));
                            assisthackimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            assisthackimg.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button taplimitat = findViewById(R.id.taplimit);
        final ImageView taplimitstatus = findViewById(R.id.sixtapstatus);
        if(load("aa_six_tap")) {
            taplimitat.setText("Re-enable " + getText(R.string.disable_speed_limitations));
            taplimitstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            taplimitstatus.setColorFilter(Color.argb(255,0,255,0));
        } else {
            taplimitat.setText("Disable " + getText(R.string.disable_speed_limitations));
            taplimitstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            taplimitstatus.setColorFilter(Color.argb(255,255,0,0));
        }

        taplimitat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_six_tap")){
                            revert("aa_six_tap");
                            taplimitat.setText("Re-enable " + getText(R.string.disable_speed_limitations));
                            taplimitstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            taplimitstatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchfortouchlimit(view);
                            taplimitstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            taplimitstatus.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button startupnav = findViewById(R.id.startup);
        final ImageView navstatus = findViewById(R.id.navstatus);
        if(load("aa_startup_policy")) {
            startupnav.setText("Re-enable " + getText(R.string.navigation_at_start));
            navstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            navstatus.setColorFilter(Color.argb(255,0,255,0));
        } else {
            startupnav.setText("Disable " + getText(R.string.navigation_at_start));
            navstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            navstatus.setColorFilter(Color.argb(255,255,0,0));
        }
        startupnav.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_startup_policy")){
                            revert("aa_startup_policy");
                            startupnav.setText("Re-enable " + getText(R.string.navigation_at_start));
                            navstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            navstatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            navpatch(view);
                            navstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            navstatus.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button patchapps = findViewById(R.id.patchapps);
        final ImageView patchappstatus = findViewById(R.id.patchedappstatus);

        if(load("aa_patched_apps") || load("after_delete")) {
            patchapps.setText("Unpatch " + getText(R.string.patch_custom_apps));
            patchappstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            patchappstatus.setColorFilter(Color.argb(255,0,255,0));
        } else {
            patchapps.setText("Patch " + getText(R.string.patch_custom_apps));
            patchappstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            patchappstatus.setColorFilter(Color.argb(255,255,0,0));
        }

        patchapps.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_patched_apps")){
                            revert("aa_patched_apps");
                            patchapps.setText("Patch " + getText(R.string.patch_custom_apps));
                            patchappstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            patchappstatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchforapps(view);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button assistanim = findViewById(R.id.assistanim);
        final ImageView assistanimstatus = findViewById(R.id.assistanimstatus);
        if(load("aa_assistant_rail")) {
            assistanim.setText("Disable " + getText(R.string.enable_assistant_animation_in_navbar));
            assistanimstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            assistanimstatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            assistanim.setText("Enable " + getText(R.string.enable_assistant_animation_in_navbar));
            assistanimstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            assistanimstatus.setColorFilter(Color.argb(255,255,0,0));
        }

        assistanim.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_assistant_rail")){
                            revert("aa_assistant_rail");
                            assistanim.setText("Enable " + getText(R.string.enable_assistant_animation_in_navbar));
                            assistanimstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            assistanimstatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchrailassistant(view);
                            assistanimstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            assistanimstatus.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button batteryoutline = findViewById(R.id.battoutline);
        final ImageView batterystatus = findViewById(R.id.batterystatus);
        if(load("aa_battery_outline")) {
            batteryoutline.setText("Re-enable " + getText(R.string.battery_outline_string));
            batterystatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            batterystatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            batteryoutline.setText("Disable " + getText(R.string.battery_outline_string));
            batterystatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            batterystatus.setColorFilter(Color.argb(255,255,0,0));
        }

        batteryoutline.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_battery_outline")){
                            revert("aa_battery_outline");
                            batteryoutline.setText("Disable " + getText(R.string.battery_outline_string));
                            batterystatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            batterystatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            battOutline(view);
                            batteryoutline.setText("Re-Enable " + getText(R.string.battery_outline_string));
                            batterystatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            batterystatus.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button statusbaropaque = findViewById(R.id.statusbar_opaque);
        final ImageView opauqestatus = findViewById(R.id.statusbar_opaque_status);
        if(load("aa_sb_opaque")) {
            statusbaropaque.setText("Disable " + getText(R.string.statb_opaque_string));
            opauqestatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            opauqestatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            statusbaropaque.setText("Enable " + getText(R.string.statb_opaque_string));
            opauqestatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            opauqestatus.setColorFilter(Color.argb(255,255,0,0));
        }

        statusbaropaque.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_sb_opaque")){
                            revert("aa_sb_opaque");
                            statusbaropaque.setText("Enable " + getText(R.string.statb_opaque_string));
                            opauqestatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            opauqestatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            opaqueStatusBar(view);
                            statusbaropaque.setText("Disable " + getText(R.string.statb_opaque_string));
                            opauqestatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            opauqestatus.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button forceNoWideScreen = findViewById(R.id.force__no_ws_button);
        final ImageView forceNoWideScreenStatus = findViewById(R.id.force_no_ws_status);

        final Button forceWideScreenButton = findViewById(R.id.force_ws_button);
        final ImageView forceWideScreenStatus = findViewById(R.id.force_ws_status);

        if(load("force_ws")) {
            forceWideScreenButton.setText("Disable " + getText(R.string.force_widescreen_text));
            forceWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            forceWideScreenStatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            forceWideScreenButton.setText("Enable " + getText(R.string.force_widescreen_text));
            forceWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            forceWideScreenStatus.setColorFilter(Color.argb(255,255,0,0));
        }

        forceWideScreenButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("force_ws")){
                            revert("force_ws");
                            forceWideScreenButton.setText("Enable " + getText(R.string.force_widescreen_text));
                            forceWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            forceWideScreenStatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            forceWideScreen(view, 470);
                            forceWideScreenButton.setText("Disable " + getText(R.string.force_widescreen_text));
                            forceWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            forceWideScreenStatus.setColorFilter(Color.argb(255,255,255,0));
                            save(false,"force_no_ws");
                            forceNoWideScreen.setText("Force Disable " + getText(R.string.base_no_ws));
                            forceNoWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            forceNoWideScreenStatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });


        if(load("force_no_ws")) {
            forceNoWideScreen.setText("Reset " + getText(R.string.base_no_ws));
            forceNoWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            forceNoWideScreenStatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            forceNoWideScreen.setText("Force Disable " + getText(R.string.base_no_ws));
            forceNoWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            forceNoWideScreenStatus.setColorFilter(Color.argb(255,255,0,0));
        }

        forceNoWideScreen.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("force_no_ws")){
                            revert("force_no_ws");
                            forceNoWideScreen.setText("Force Disable " + getText(R.string.base_no_ws));
                            forceNoWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            forceNoWideScreenStatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            forceWideScreen(view, 1921);
                            forceNoWideScreen.setText("Reset " + getText(R.string.base_no_ws));
                            forceNoWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            forceNoWideScreenStatus.setColorFilter(Color.argb(255,255,255,0));
                            save(false, "force_ws");
                            forceWideScreenButton.setText("Disable " + getText(R.string.force_widescreen_text));
                            forceWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            forceWideScreenStatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });


        final Button huntrottling = findViewById(R.id.hunthrottlingbutton);
        final int[] scrollbarStatus = {0};
        final TextView displayValue = findViewById(R.id.seekbar_text);
        final SeekBar hunSeekbar = findViewById(R.id.hun_ms_value);
        hunSeekbar.setProgress(8000);
        displayValue.setText(hunSeekbar.getProgress() + "ms");
        hunSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int)Math.round(progress/100))*100;
                seekBar.setProgress(progress);
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                huntrottling.setText("Set " + getText(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress()+ " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                huntrottling.setText("Set " + getText(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress()+ " ms");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                scrollbarStatus[0] = hunSeekbar.getProgress();
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                huntrottling.setText("Set " + getText(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress()+ " ms");
            }
        });


        final ImageView hunstatus = findViewById(R.id.huntrottlingstatus);
        if(load("aa_hun_ms")) {
            huntrottling.setText("reset " + getText(R.string.set_notification_duration_to) + " default");
            hunstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            hunstatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            huntrottling.setText("Set " + getText(R.string.set_notification_duration_to));
            hunstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            hunstatus.setColorFilter(Color.argb(255,255,0,0));
        }

        huntrottling.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_hun_ms") && hunSeekbar.getProgress() == 8000){
                            revert("aa_hun_ms");
                            huntrottling.setText("Set " + getText(R.string.set_notification_duration_to));
                            hunstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            hunSeekbar.setProgress(8000);
                            hunstatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            setHunDuration(view, scrollbarStatus[0]);
                            huntrottling.setText("reset " + getText(R.string.set_notification_duration_to) + " default");
                            hunstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            hunstatus.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

    }



    private void revert(final String toRevert) {

        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Reverting the hack  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS " + toRevert + ";'\n"
                ).getStreamLogsWithLabels());
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DELETE FROM FlagOverrides;'\n" //make sure a good clean is done after dropping the trigger
                ).getStreamLogsWithLabels());
                save(false, toRevert);
            }


        }.start();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copy:
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                TextView textView = findViewById(R.id.logs);
                ClipData clip = ClipData.newPlainText("logs", textView.getText());
                clipboard.setPrimaryClip(clip);
            break;

            case R.id.about:
                DialogFragment aboutDialog = new AboutDialog();
                aboutDialog.show(getSupportFragmentManager(), "AboutDialog");

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void save(final boolean isChecked, String key) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

     public boolean load(String key) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }



    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu, menu );
        return true;
    }


    public void patchforapps(final View view) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        SharedPreferences appsListPref = getApplicationContext().getSharedPreferences("appsListPref", 0);
        Map<String, ?> allEntries = appsListPref.getAll();
        if (allEntries.isEmpty()) {
            Intent intent = new Intent(this, AppsList.class);
            this.startActivity(intent);
            Toast.makeText(getApplicationContext(), "Choose apps to whitelist.", Toast.LENGTH_LONG).show();
        } else {
            logs.append("--  Apps which will be added to whitelist: --\n");
            String whiteListString = "";
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                logs.append("\t\t- " + entry.getValue() + " (" + entry.getKey() + ")\n");
                whiteListString += "," + entry.getKey();
            }

            whiteListString = whiteListString.replaceFirst(",", "");
            final String whiteListStringFinal = whiteListString;

            new Thread() {
                @Override
                public void run() {
                    String path = getApplicationInfo().dataDir;
                    boolean suitableMethodFound = true;
                    copyAssets();

                    appendText(logs, "\n\n-- Drop Triggers  --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DROP TRIGGER IF EXISTS aa_patched_apps;'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, "\n\n--  DELETE old Flags  --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name=\"app_white_list\";'"
                    ).getStreamLogsWithLabels());

                    if (runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT 1 FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"'").getInputStreamLog().equals("1")) {

                        appendText(logs, "\n\n--  run SQL method #1  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER after_delete AFTER DELETE\n" +
                                        "ON Flags\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "END;'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, "\n--  end SQL method #1  --");

                    } else if (runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT 1 FROM Packages WHERE packageName=\"com.google.android.gms.car\"'").getInputStreamLog().equals("1")) {

                        appendText(logs, "\n\n--  run SQL method #2  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER aa_patched_apps AFTER DELETE\n" +
                                        "ON Flags\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "END;'"
                        ).getStreamLogsWithLabels());
                        appendText(logs, "\n--  end SQL method #2  --");

                    } else if (runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car#car\"'").getInputStreamLog().equals("1")) {

                        appendText(logs, "\n\n--  run SQL method #3  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER after_delete AFTER DELETE\n" +
                                        "ON Flags\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "END;'"
                        ).getStreamLogsWithLabels());
                        appendText(logs, "\n--  end SQL method #3  --");

                    } else if (runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"'").getInputStreamLog().equals("1")) {

                        appendText(logs, "\n\n--  run SQL method #4  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER aa_patched_apps AFTER DELETE\n" +
                                        "ON Flags\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "END;'"
                        ).getStreamLogsWithLabels());
                        appendText(logs, "\n--  end SQL method #4  --");



                    } else {
                        suitableMethodFound = false;
                        appendText(logs, "\n\n--  Suitable method NOT found!  --");
                    }

                    // Check Start
                    if (suitableMethodFound) {
                        StreamLogs checkStep1 = runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'SELECT * FROM Flags WHERE name=\"app_white_list\";'"
                        );
                        String[] checkStep1Sorted = checkStep1.getInputStreamLog().split("\n");
                        Arrays.sort(checkStep1Sorted);

                        String checkStep1SortedToString = "";
                        for (String s : checkStep1Sorted) {
                            checkStep1SortedToString += "\n" + s;
                        }
                        checkStep1SortedToString.replaceFirst("\n", "");
                        checkStep1.setInputStreamLog(checkStep1SortedToString);

                        appendText(logs, "\n\n--  Check (1/3)  --" + checkStep1.getStreamLogsWithLabels());

                        appendText(logs, "\n--  Check (2/3)  --" + runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'DELETE FROM Flags WHERE name=\"app_white_list\";'"
                        ).getStreamLogsWithLabels());

                        StreamLogs checkStep3 = runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'SELECT * FROM Flags WHERE name=\"app_white_list\";'"
                        );
                        String[] checkStep3Sorted = checkStep3.getInputStreamLog().split("\n");
                        Arrays.sort(checkStep3Sorted);

                        String checkStep3SortedToString = "";
                        for (String s : checkStep3Sorted) {
                            checkStep3SortedToString += "\n" + s;
                        }
                        checkStep3SortedToString.replaceFirst("\n", "");
                        checkStep3.setInputStreamLog(checkStep3SortedToString);

                        appendText(logs, "\n--  Check (3/3)  --" + checkStep3.getStreamLogsWithLabels());

                        if (checkStep1.getInputStreamLog().length() == checkStep3.getInputStreamLog().length()) {
                            appendText(logs, "\n\n--  Check seems OK :)  --");
                        } else {
                            appendText(logs, "\n\n--  Check NOT OK.  --");
                            appendText(logs, "\n     Length before delete and after was not equal.");
                            appendText(logs, "\n        Before: " + checkStep1.getInputStreamLog().length());
                            appendText(logs, "\n        After:  " + checkStep3.getInputStreamLog().length());
                        }

                        save(true, "aa_patched_apps");
                    }
                    // Check End
                }
            }.start();
        }
    }

    public void patchforassistshort(final View view) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

            new Thread() {
                @Override
                public void run() {
                    String path = getApplicationInfo().dataDir;
                    boolean suitableMethodFound = true;
                    copyAssets();

                    appendText(logs, "\n\n-- Drop Triggers  --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DROP TRIGGER IF EXISTS ASSIST_SHORT;'"
                    ).getStreamLogsWithLabels());

                    if (runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'\n").getInputStreamLog().equals("1")) {

                        appendText(logs, "\n\n--  run SQL method   --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\",\"\",1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",\"\",1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1) ,1,1);" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1) ,1,1);\n'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER assist_short AFTER DELETE\n" +
                                        "ON FlagOverrides\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\",\"\",1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",\"\",1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1) ,1,1);\n" +
                                "END;'\n"
                        ).getStreamLogsWithLabels());
                        appendText(logs, "\n--  end SQL method   --");
                        save(true, "assist_short");
                    } else {
                        suitableMethodFound = false;
                        appendText(logs, "\n\n--  Suitable method NOT found!  --");
                    }


                    
                }
            }.start();
        }

    public void patchrailassistant(final View view) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_assistant_rail;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1) ,1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1) ,1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1) ,1,1);'\n"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_assistant_rail AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1) ,1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1) ,1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1) ,1,1);\n" +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_assistant_rail");

                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }



            }
        }.start();
    }

    public void patchforspeed(final View view) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_speed_hack;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'\n").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",\"\",99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",\"\",0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);'\n"

                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_speed_hack AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",\"\",99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",\"\",0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +

                                    "END;'"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_speed_hack");
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }



            }
        }.start();
    }

    public void patchfortouchlimit(final View view) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_six_tap;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\", \"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",\"\",0,1);\n" +


                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);'\n"

                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_six_tap AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",\"\",0,1);\n" +


                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                            "END;'"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_six_tap");
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();
    }

    public void navpatch(View view) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_startup_policy;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM FLAGS WHERE name=\"SystemUi__startup_app_policy\";"+
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", \"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                                    path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_startup_policy AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", \"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_startup_policy");
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();


    }

    public void battOutline(View view) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_battery_outline;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name=\"BatterySaver__icon_outline_enabled\";"+
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", \"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_battery_outline AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", \"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_battery_outline");
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();


    }

    public void opaqueStatusBar (View view) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_sb_opaque;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name=\"Boardwalk_status_bar_force_opaque\";"+
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Boardwalk_status_bar_force_opaque\", \"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Boardwalk_status_bar_force_opaque\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Boardwalk_status_bar_force_opaque\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Boardwalk_status_bar_force_opaque\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_sb_opaque AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Boardwalk_status_bar_force_opaque\", \"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Boardwalk_status_bar_force_opaque\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Boardwalk_status_bar_force_opaque\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Boardwalk_status_bar_force_opaque\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_sb_opaque");
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();

    }

    public void setHunDuration (View view, final int value) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_hun_ms;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name=\"SystemUi__hun_default_heads_up_timeout_ms\";"+
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__hun_default_heads_up_timeout_ms\", \"\"," + value + ",1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__hun_default_heads_up_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),\" + value + \",1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__hun_default_heads_up_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),\" + value + \",1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__hun_default_heads_up_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),\" + value + \",1);\n'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_hun_ms AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__hun_default_heads_up_timeout_ms\", \"\"," + value + ",1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__hun_default_heads_up_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),\" + value + \",1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__hun_default_heads_up_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),\" + value + \",1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__hun_default_heads_up_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),\" + value + \",1);\n" +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_hun_ms");
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();

    }

    public void forceWideScreen (View view, final int value) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_ws; DROP TRIGGER IF EXISTS aa_no_ws;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__widescreen_breakpoint_dp\", \"\"," + value + ",1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__widescreen_breakpoint_dp\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),\" + value + \",1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__widescreen_breakpoint_dp\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),\" + value + \",1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__widescreen_breakpoint_dp\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),\" + value + \",1);\n'"
                    ).getStreamLogsWithLabels());

                    String decideWhat = new String();
                    switch (value) {
                        case 470: decideWhat = "force_ws";
                        case 1921: decideWhat = "force_no_ws";
                    }

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER " + decideWhat + " AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__widescreen_breakpoint_dp\", \"\"," + value + ",1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__widescreen_breakpoint_dp\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),\" + value + \",1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__widescreen_breakpoint_dp\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),\" + value + \",1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__widescreen_breakpoint_dp\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),\" + value + \",1);\n" +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "force_ws");
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();

    }


    public static StreamLogs runSuWithCmd(String cmd) {
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        InputStream errorStream = null;

        StreamLogs streamLogs = new StreamLogs();
        streamLogs.setOutputStreamLog(cmd);

        try{
            Process su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            inputStream = su.getInputStream();
            errorStream = su.getErrorStream();
            outputStream.writeBytes(cmd + "\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();

            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            streamLogs.setInputStreamLog(readFully(inputStream));
            streamLogs.setErrorStreamLog(readFully(errorStream));
        } catch (IOException e){
            e.printStackTrace();
        }

        return streamLogs;
    }



    public static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }

    private void copyAssets() {
        String path = getApplicationInfo().dataDir;
        final TextView logs = findViewById(R.id.logs);
        File file = new File(path, "sqlite3");
        if (!file.exists()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.v("AATW", "\n--  Copy sqlite3 to data directory  --");
                }
            });
            InputStream in;
            OutputStream out;
            try {
                in = this.getResources().openRawResource(R.raw.sqlite3);

                String outDir = getApplicationInfo().dataDir;

                File outFile = new File(outDir, "sqlite3");

                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch(IOException e) {
                Log.e("AATW", "Failed to copy asset file: sqlite3", e);
            }
        }
        Log.v("AATW", runSuWithCmd("chmod 775 " + path + "/sqlite3").getStreamLogsWithLabels());
    }



    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private void appendText(final TextView textView, final String s){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(s);
            }
        });
    }

    public void loadStatus(SharedPreferences mysharedpreferences, final String path) {

        final Map<String, ?> mymap = mysharedpreferences.getAll();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for ( Map.Entry <String, ?> entry : mymap.entrySet()) {
                    final String key = entry.getKey().toString();

                    if (runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT * FROM sqlite_master WHERE name=\""+ key + "\";'").getInputStreamLog().toString().isEmpty()) {
                        save(false, key);
                        Log.v("AATWEAKER", "RAMO SI" + key );
                    } else {
                        save (true, key);
                        Log.v("AATWEAKER", "RAMO NO");
                    }
                }
            }
        });

    }



}
