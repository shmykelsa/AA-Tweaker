package sksa.aa.tweaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    public static String appDirectory = new String();

    private static Context mContext;
    private ImageView noSpeedRestrictionsStatus;
    private ImageView assistantShortcutsStatus;
    private ImageView taplimitstatus;
    private ImageView navstatus;
    private ImageView patchappstatus;
    private ImageView assistanimstatus;
    private ImageView batteryOutlineStatus;
    private ImageView opaqueStatus;
    private ImageView forceWideScreenStatus;
    private ImageView messagesHunStatus;
    private ImageView mediaHunStatus;
    private ImageView calendarTweakStatus;
    private ImageView btstatus;
    private ImageView messagesTweakStatus;
    private ImageView mdstatus;
    private ImageView batteryWarningStatus;
    private ImageView activateWallpapersStatus;
    private ImageView oldDarkModeStatus;
    private ImageView telemetryStatus;
    private ImageView mediaTabsStatus;
    private ImageView forceNoWideScreenStatus;
    private Button rebootButton;
    private Button nospeed;
    private Button assistshort;
    private Button taplimitat;
    private Button startupnav;
    private Button patchapps;
    private Button assistanim;
    private Button batteryoutline;
    private Button forceNoWideScreen;
    private Button statusbaropaque;
    private Button forceWideScreenButton;
    private Button messagesHunThrottling;
    private Button mediathrottlingbutton;
    private Button moreCalendarButton;
    private Button bluetoothoff;
    private Button messagesButton;
    private Button mdbutton;
    private Button batteryWarning;
    private Button activateWallpapersButton;
    private Button oldDarkMode;
    private Button disableTelemetryButton;
    private Button activateMediaTabs;

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final String path = getApplicationInfo().dataDir;
        appDirectory = path;
        loadStatus(path);
        String CountUsers = runSuWithCmd(
                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                        "'SELECT COUNT(DISTINCT USER) FROM Flags WHERE user !=\"\";'").getInputStreamLog();
        final int UserCount = Integer.parseInt(CountUsers);

        setContentView(R.layout.activity_main);

        if (UserCount == 0) {
            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(getString(R.string.warning_title));
            alertDialog.setMessage(getString(R.string.no_accounts_warning));

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "GITHUB", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/shmykelsa/AA-Tweaker/issues/new")));
                }
            });

            alertDialog.show();
        }

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

        rebootButton = findViewById(R.id.reboot_button);
        final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.reboot_button_anim);


        final Boolean[] animationRun = {false};
        final TextView upperTextView = findViewById(R.id.legend);
        upperTextView.setText(R.string.main_string);
        final AlphaAnimation legendAnim;
        legendAnim = new AlphaAnimation(1.0f, 0.0f);
        legendAnim.setDuration(100);
        legendAnim.setRepeatCount(1);
        legendAnim.setRepeatMode(Animation.REVERSE);
        legendAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                if (upperTextView.getText().toString().equals(getString(R.string.legend))){
                    upperTextView.setText(R.string.main_string);
                } else {
                    upperTextView.setText(R.string.legend);
                }
            }
        });

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // use runOnUiThread(Runnable action)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upperTextView.startAnimation(legendAnim);
                    }
                });
            }
        }, 12000, 12000);

        nospeed = findViewById(R.id.nospeed);
        noSpeedRestrictionsStatus = findViewById(R.id.speedhackstatus);
        if(load("aa_speed_hack")) {
            nospeed.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
            changeStatus(noSpeedRestrictionsStatus, 2, false);
        } else {
            nospeed.setText(getString(R.string.disable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
            changeStatus(noSpeedRestrictionsStatus, 0, false);
        }

        nospeed.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_speed_hack")){
                            revert("aa_speed_hack");
                            nospeed.setText(getString(R.string.disable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
                            changeStatus(noSpeedRestrictionsStatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchforspeed(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        nospeed.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_nospeed));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_nospeed));

                dialog.setContentView(view);


                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);
                dialog.show();

                return true;
            }
        });

        assistshort = findViewById(R.id.assistshort);
        assistantShortcutsStatus = findViewById(R.id.shortcutstatus);
        if(load("assist_short")) {
            assistshort.setText(getString(R.string.disable_tweak_string) + getString(R.string.enable_assistant_shortcuts));
            changeStatus(assistantShortcutsStatus, 2, false);
        } else {
            assistshort.setText(getString(R.string.enable_tweak_string) + getString(R.string.enable_assistant_shortcuts));
            changeStatus(assistantShortcutsStatus, 0, false);

        }

        assistshort.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_shortcuts));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                ImageView img2 = view.findViewById(R.id.tutorialimage2);
                ImageView img3 = view.findViewById(R.id.tutorialimage3);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_shortcuts_1));
                img2.setImageDrawable(getDrawable(R.drawable.tutorial_shortcuts_2));
                img3.setImageDrawable(getDrawable(R.drawable.tutorial_shortcuts_3));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 880);

                return true;
            }
        });


        assistshort.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("assist_short")){
                            revert("assist_short");
                            assistshort.setText(getString(R.string.enable_tweak_string) + getString(R.string.enable_assistant_shortcuts));
                            changeStatus(assistantShortcutsStatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchforassistshort(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        taplimitat = findViewById(R.id.taplimit);
        taplimitstatus = findViewById(R.id.sixtapstatus);
        if(load("aa_six_tap")) {
            taplimitat.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.disable_speed_limitations));
            changeStatus(taplimitstatus, 2, false);

        } else {
            taplimitat.setText(getString(R.string.disable_tweak_string) + getString(R.string.disable_speed_limitations));
            changeStatus(taplimitstatus, 0, false);

        }

        taplimitat.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_sixtap));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_sixtap));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);

                return true;
            }
        });

        taplimitat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_six_tap")){
                            revert("aa_six_tap");
                            taplimitat.setText(getString(R.string.disable_tweak_string) + getString(R.string.disable_speed_limitations));
                            changeStatus(taplimitstatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchfortouchlimit(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        startupnav = findViewById(R.id.startup);
        navstatus = findViewById(R.id.navstatus);
        if(load("aa_startup_policy")) {
            startupnav.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.navigation_at_start));
            changeStatus(navstatus, 2, false);
        } else {
            startupnav.setText(getString(R.string.disable_tweak_string) + getString(R.string.navigation_at_start));
            changeStatus(navstatus, 0, false);
        }
        startupnav.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_startup_policy")){
                            revert("aa_startup_policy");
                            revert("aa_startup_policy_cleanup");
                            startupnav.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.navigation_at_start));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            navpatch(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        startupnav.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_startup));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);

                return true;
            }
        });

        patchapps = findViewById(R.id.patchapps);
        patchappstatus = findViewById(R.id.patchedappstatus);


        if(load("aa_patched_apps") && load("aa_patched_apps_fix") ) {
            patchapps.setText(getString(R.string.unpatch) + getString(R.string.patch_custom_apps));
            changeStatus(patchappstatus, 2, false);
        } else {
            patchapps.setText(getString(R.string.patch_app) + getString(R.string.patch_custom_apps));
            changeStatus(patchappstatus, 0, false);
        }

        patchapps.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_patched_apps") || load("after_delete")){
                            revert ("after_delete");
                            revert("aa_patched_apps");
                            revert("aa_patched_apps_fix");
                            patchapps.setText(getString(R.string.patch_app) + getString(R.string.patch_custom_apps));
                            changeStatus(patchappstatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);


                                animationRun[0] = true;
                            }
                        }
                        else {
                            SharedPreferences appsListPref = getApplicationContext().getSharedPreferences("appsListPref", 0);
                            Map<String, ?> allEntries = appsListPref.getAll();
                            if (allEntries.isEmpty()) {
                                Intent intent = new Intent(MainActivity.this, AppsList.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), getString(R.string.choose_apps_warning), Toast.LENGTH_LONG).show();
                            } else {
                                patchforapps(view, UserCount);
                                if (!animationRun[0]) {
                                    rebootButton.setVisibility(View.VISIBLE);
                                    rebootButton.startAnimation(anim);
                                    animationRun[0] = true;
                                }
                            }
                        }
                    }
                });

        patchapps.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_patchapps));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);

                return true;
            }
        });

        assistanim = findViewById(R.id.assistanim);
        assistanimstatus = findViewById(R.id.assistanimstatus);
        if(load("aa_assistant_rail")) {
            assistanim.setText(getString(R.string.disable_tweak_string) + getString(R.string.enable_assistant_animation_in_navbar));
            changeStatus(assistanimstatus, 2, false);

        } else {
            assistanim.setText(getString(R.string.enable_tweak_string) + getString(R.string.enable_assistant_animation_in_navbar));
            changeStatus(assistanimstatus, 0, false);
        }

        assistanim.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_assistant_rail")){
                            revert("aa_assistant_rail");
                            assistanim.setText(getString(R.string.enable_tweak_string) + getString(R.string.enable_assistant_animation_in_navbar));
                            changeStatus(assistanimstatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchrailassistant(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        assistanim.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_animation));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_animation));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 600);

                return true;
            }
        });

        batteryoutline = findViewById(R.id.battoutline);
        batteryOutlineStatus = findViewById(R.id.batterystatus);
        if(load("aa_battery_outline")) {
            batteryoutline.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_outline_string));
            changeStatus(batteryOutlineStatus, 2, false);

        } else {
            batteryoutline.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_outline_string));
            changeStatus(batteryOutlineStatus, 0, false);
        }

        batteryoutline.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_battery_outline")){
                            revert("aa_battery_outline");
                            batteryoutline.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_outline_string));
                            changeStatus(batteryOutlineStatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            battOutline(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        batteryoutline.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_battery_outline));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_outline));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);

                return true;
            }
        });

        statusbaropaque = findViewById(R.id.statusbar_opaque);
        opaqueStatus = findViewById(R.id.statusbar_opaque_status);
        if(load("aa_sb_opaque")) {
            statusbaropaque.setText(getString(R.string.disable_tweak_string) + getString(R.string.statb_opaque_string));
            changeStatus(opaqueStatus, 2, false);

        } else {
            statusbaropaque.setText(getString(R.string.enable_tweak_string) + getString(R.string.statb_opaque_string));
            changeStatus(opaqueStatus, 0, false);
        }

        statusbaropaque.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_sb_opaque")){
                            revert("aa_sb_opaque");
                            statusbaropaque.setText(getString(R.string.enable_tweak_string) + getString(R.string.statb_opaque_string));
                            changeStatus(opaqueStatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            opaqueStatusBar(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        statusbaropaque.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_statusbar_opaque));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);

                return true;
            }
        });

        forceNoWideScreen = findViewById(R.id.force__no_ws_button);
        forceNoWideScreenStatus = findViewById(R.id.force_no_ws_status);

        forceWideScreenButton = findViewById(R.id.force_ws_button);
        forceWideScreenStatus = findViewById(R.id.force_ws_status);

        if(load("force_ws")) {
            forceWideScreenButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.force_widescreen_text));
            changeStatus(forceWideScreenStatus, 2, false);
        } else {
            forceWideScreenButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.force_widescreen_text));
            changeStatus(forceWideScreenStatus, 0, false);
        }

        forceWideScreenButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("force_ws")){
                            revert("force_ws");
                            forceWideScreenButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.force_widescreen_text));
                            changeStatus(forceWideScreenStatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            forceWideScreen(view, 470, UserCount);
                            forceWideScreenButton.setText(getString(R.string.disable_tweak_string)+ getString(R.string.force_widescreen_text));
                            save(true, "force_ws");
                            if (load("force_no_ws")) {
                                Toast.makeText(getApplicationContext(), getString(R.string.force_disable_widescreen_warning), Toast.LENGTH_LONG).show();
                                save(false,"force_no_ws");
                                forceNoWideScreen.setText(getString(R.string.force_disable_tweak) + getString(R.string.base_no_ws));
                                changeStatus(forceNoWideScreenStatus, 0, true);
                            }
                            changeStatus(forceWideScreenStatus, 2, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        forceWideScreenButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_widescreen));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_widescreen));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);

                return true;
            }
        });


        if(load("force_no_ws")) {
            forceNoWideScreen.setText(getString(R.string.reset_tweak) + getString(R.string.base_no_ws));
            changeStatus(forceNoWideScreenStatus, 2, false);

        } else {
            forceNoWideScreen.setText(getString(R.string.force_disable_tweak) + getString(R.string.base_no_ws));
            changeStatus(forceNoWideScreenStatus, 0, false);
        }

        forceNoWideScreen.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("force_no_ws")){
                            revert("force_no_ws");
                            forceNoWideScreen.setText(getString(R.string.force_disable_tweak) + getString(R.string.base_no_ws));
                            changeStatus(forceNoWideScreenStatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            forceWideScreen(view, 3000, UserCount);
                            forceNoWideScreen.setText(getString(R.string.reset_tweak) + getString(R.string.base_no_ws));
                            changeStatus(forceNoWideScreenStatus, 0, true);
                            save(true, "force_no_ws");
                            if (load ("force_ws")) {
                                save(false, "force_ws");
                                Toast.makeText(getApplicationContext(), R.string.force_widescreen_warning, Toast.LENGTH_LONG).show();
                                forceWideScreenButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.force_widescreen_text));
                                changeStatus(forceWideScreenStatus, 1, true);
                            }
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        forceNoWideScreen.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_no_widescreen));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_nowidescreen));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);

                return true;
            }
        });


        messagesHunThrottling = findViewById(R.id.hunthrottlingbutton);
        final int[] messagesHunScrollbarValue = {0};
        final TextView displayValue = findViewById(R.id.seekbar_text);
        final SeekBar hunSeekbar = findViewById(R.id.messages_hun_seekbar);
        hunSeekbar.setProgress(8000);
        displayValue.setText(hunSeekbar.getProgress() + "ms");
        hunSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int)Math.round(progress/100))*100;
                seekBar.setProgress(progress);
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                messagesHunThrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress()+ " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                messagesHunThrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress()+ " ms");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                messagesHunScrollbarValue[0] = hunSeekbar.getProgress();
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                if (hunSeekbar.getProgress() == 8000) {
                    messagesHunThrottling.setText(getString(R.string.reset_tweak) + getString(R.string.set_notification_duration_to) + getString(R.string.default_string));
                } else {
                    messagesHunThrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress()+ " ms");
                }
            }
        });


        messagesHunStatus = findViewById(R.id.huntrottlingstatus);
        final TextView currentlySetHun = findViewById(R.id.notification_currently_set);
        if(load("aa_hun_ms")) {
            messagesHunThrottling.setText(getString(R.string.reset_tweak) + getString(R.string.set_notification_duration_to) + getString(R.string.default_string));
            changeStatus(messagesHunStatus, 2, false);
            currentlySetHun.setText(getString(R.string.currently_set) + loadValue("messages_hun_value"));
        } else {
            messagesHunThrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to));
            changeStatus(messagesHunStatus, 0, false);
        }

        messagesHunThrottling.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_hun_ms")){
                            if (hunSeekbar.getProgress() == 8000) {
                                revert("aa_hun_ms");
                                changeStatus(messagesHunStatus, 0, true);
                                currentlySetHun.setText("");
                            } else {
                                setHunDuration(view, hunSeekbar.getProgress(), UserCount);
                                currentlySetHun.setText(getString(R.string.currently_set) + hunSeekbar.getProgress());
                            }
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            setHunDuration(view, hunSeekbar.getProgress(), UserCount);
                            currentlySetHun.setText(getString(R.string.currently_set) + hunSeekbar.getProgress());
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        messagesHunThrottling.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);

                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_hun));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_hun));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);

                return true;
            }
        });

        mediathrottlingbutton = findViewById(R.id.media_throttling_button);
        final int[] secondScrollBarStatus = {0};
        final TextView secondDisplayValue = findViewById(R.id.second_seekbar_text);
        final SeekBar mediaSeekbar = findViewById(R.id.media_hun_value);
        mediaSeekbar.setProgress(8000);
        secondDisplayValue.setText(mediaSeekbar.getProgress() + "ms");
        mediaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int)Math.round(progress/1000))*1000;
                mediaSeekbar.setProgress(progress);
                secondDisplayValue.setText(mediaSeekbar.getProgress() + "ms");
                mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to) + " " + mediaSeekbar.getProgress()+ " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                secondDisplayValue.setText(mediaSeekbar.getProgress() + "ms");
                mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to) + " " + mediaSeekbar.getProgress()+ " ms");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                secondScrollBarStatus[0] = mediaSeekbar.getProgress();
                secondDisplayValue.setText(mediaSeekbar.getProgress() + "ms");
                if (hunSeekbar.getProgress() == 8000) {
                    mediathrottlingbutton.setText(getString(R.string.reset_tweak) + getString(R.string.media_notification_duration_to) + getString(R.string.default_string));
                } else {
                    mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to) + " " + mediaSeekbar.getProgress()+ " ms");
                }
            }
        });

        final TextView currentlySetMediaHun = findViewById(R.id.media_notification_currently_set);
        mediaHunStatus = findViewById(R.id.media_trhrottling_status);
        if(load("aa_media_hun")) {
            mediathrottlingbutton.setText(getString(R.string.reset_tweak) + getString(R.string.media_notification_duration_to) + getString(R.string.default_string));
            changeStatus(mediaHunStatus, 2, false);
            currentlySetMediaHun.setText(getString(R.string.currently_set) + loadValue("media_hun_value"));
        } else {
            mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to));
            changeStatus(mediaHunStatus, 0, false);
        }

        mediathrottlingbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_media_hun")){
                            if (mediaSeekbar.getProgress()==8000) {
                                revert("aa_media_hun");
                                changeStatus(mediaHunStatus, 0, true);
                                currentlySetMediaHun.setText("");
                            } else {
                                setMediaHunDuration(view, mediaSeekbar.getProgress(), UserCount);
                                currentlySetMediaHun.setText(getString(R.string.currently_set) + mediaSeekbar.getProgress());
                            }
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            setMediaHunDuration(view, mediaSeekbar.getProgress(), UserCount);
                            currentlySetMediaHun.setText(getString(R.string.currently_set) + mediaSeekbar.getProgress());
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        mediathrottlingbutton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_media_hun));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_media_hun));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);

                return true;
            }
        });


        moreCalendarButton = findViewById(R.id.calendar_more_events_button);
        final int[] calendarSeekbarStatus = {0};
        final TextView calendarSeekbarTextView = findViewById(R.id.calendar_days_seekbar_text);
        final SeekBar calendarSeekbar = findViewById(R.id.calendar_days_seekbar);
        calendarSeekbar.setProgress(1);
        calendarSeekbarTextView.setText("1");
        calendarSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                calendarSeekbar.setProgress(progress);
                calendarSeekbarTextView.setText(calendarSeekbar.getProgress() + "");
                if (progress == 1 || progress == 0) {
                    moreCalendarButton.setText(getString(R.string.calendar_tweak_single, calendarSeekbar.getProgress()));
                } else {
                    moreCalendarButton.setText(getString(R.string.calendar_tweak, calendarSeekbar.getProgress()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                calendarSeekbarTextView.setText(calendarSeekbar.getProgress() + "");
                moreCalendarButton.setText(getString(R.string.calendar_tweak, calendarSeekbar.getProgress()));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                calendarSeekbarStatus[0] = calendarSeekbar.getProgress();
                calendarSeekbarTextView.setText(calendarSeekbar.getProgress() + "");
            }
        });

        final TextView currentlySetAgendaDays = findViewById(R.id.calendar_days_currently_set);
        calendarTweakStatus = findViewById(R.id.calendar_more_events_status);
        if(load("calendar_aa_tweak")) {
            moreCalendarButton.setText(getString(R.string.calendar_tweak_single, calendarSeekbar.getProgress()));
            changeStatus(calendarTweakStatus, 2, false);
            currentlySetAgendaDays.setText(getString(R.string.currently_set) + loadValue("agenda_value"));
        } else {
            moreCalendarButton.setText(getString(R.string.calendar_tweak_single, calendarSeekbar.getProgress()));
            changeStatus(calendarTweakStatus, 0, false);
        }

        moreCalendarButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("calendar_aa_tweak")){
                            if (calendarSeekbar.getProgress() == 1) {
                                revert("calendar_aa_tweak");
                                changeStatus(calendarTweakStatus, 0, true);
                                currentlySetAgendaDays.setText("");
                            } else {
                                setCalendarEvents(view, calendarSeekbar.getProgress(), UserCount);
                                currentlySetAgendaDays.setText(getString(R.string.currently_set) + calendarSeekbar.getProgress());
                            }
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            setCalendarEvents(view, calendarSeekbar.getProgress(), UserCount);
                            currentlySetAgendaDays.setText(getString(R.string.currently_set) + calendarSeekbar.getProgress());
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        moreCalendarButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_calendar_tweak));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_agenda));


                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);

                return true;
            }
        });


        bluetoothoff = findViewById(R.id.bluetooth_disable_button);
        btstatus = findViewById(R.id.bt_disable_status);
        if(load("bluetooth_pairing_off")) {
            bluetoothoff.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.bluetooth_auto_connect));
            changeStatus(btstatus, 2, false);
        } else {
            bluetoothoff.setText(getString(R.string.disable_tweak_string) + getString(R.string.bluetooth_auto_connect));
            changeStatus(btstatus, 0, false);

        }

        bluetoothoff.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("bluetooth_pairing_off")){
                            revert("bluetooth_pairing_off");
                            bluetoothoff.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.bluetooth_auto_connect));
                            changeStatus(btstatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            forceNoBt(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        bluetoothoff.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_bluetooth));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);

                return true;
            }
        });

        messagesButton = findViewById(R.id.messaging_app_unlock_button);
        messagesTweakStatus = findViewById(R.id.messaging_tweak_status);
        if(load("aa_messaging_apps")) {
            messagesButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.messages_tweak_string));
            changeStatus(messagesTweakStatus, 2, false);
        } else {
            messagesButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.messages_tweak_string));
            changeStatus(messagesTweakStatus, 0, false);
        }

        messagesButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_messaging_apps")){
                            revert("aa_messaging_apps");
                            messagesButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.messages_tweak_string));
                            changeStatus(messagesTweakStatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            messagesTweak(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        messagesButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_messages_tweak));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_messaging_1));

                ImageView img2 = view.findViewById(R.id.tutorialimage2);
                img2.setImageDrawable(getDrawable(R.drawable.tutorial_messaging_2));

                ImageView img3 = view.findViewById(R.id.tutorialimage3);
                img3.setImageDrawable(getDrawable(R.drawable.tutorial_messaging_3));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);

                return true;
            }
        });

        mdbutton = findViewById(R.id.multi_display_button);
        mdstatus = findViewById(R.id.multi_display_status);
        if(load("multi_display")) {
            mdbutton.setText(getString(R.string.disable_tweak_string) + getString(R.string.multi_display_string));
            changeStatus(mdstatus, 2, false);
        } else {
            mdbutton.setText(getString(R.string.enable_tweak_string) + getString(R.string.multi_display_string));
            changeStatus(mdstatus, 0, false);
        }

        mdbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("multi_display")){
                            revert("multi_display");
                            mdbutton.setText(getString(R.string.enable_tweak_string) + getString(R.string.multi_display_string));
                            changeStatus(mdstatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            multiDisplay(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        mdbutton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_multidisplay));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_md1));

                ImageView img2 = view.findViewById(R.id.tutorialimage2);
                img2.setImageDrawable(getDrawable(R.drawable.tutorial_md2));

                ImageView img3 = view.findViewById(R.id.tutorialimage3);
                img3.setImageDrawable(getDrawable(R.drawable.tutorial_md3));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);
                return true;
            }
        });

        batteryWarning = findViewById(R.id.battery_warning_button);
        batteryWarningStatus = findViewById(R.id.battery_warning_status);
        if(load("battery_saver_warning")) {
            batteryWarning.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_warning));
            changeStatus(batteryWarningStatus, 2, false);
        } else {
            batteryWarning.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_warning));
            changeStatus(batteryWarningStatus, 0, false);
        }

        batteryWarning.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("battery_saver_warning")){
                            revert("battery_saver_warning");
                            batteryWarning.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_warning));
                            changeStatus(batteryWarningStatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            disableBatteryWarning(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        batteryWarning.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_battery_saver_warning));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_battery_saver));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);
                return true;
            }
        });

        activateWallpapersButton = findViewById(R.id.custom_wallpapers_button);
        activateWallpapersStatus = findViewById(R.id.custom_wallpapers_status);
        if(load("aa_wallpapers")) {
            activateWallpapersButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.custom_wallpapers));
            changeStatus(activateWallpapersStatus, 2, false);
        } else {
            activateWallpapersButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.custom_wallpapers));
            changeStatus(activateWallpapersStatus, 0, false);
        }

        activateWallpapersButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_wallpapers")){
                            revert("aa_wallpapers");
                            activateWallpapersButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.custom_wallpapers));
                            changeStatus(activateWallpapersStatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            activateWallpapers(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        activateWallpapersButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_wallpapers));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);
                img1.setImageDrawable(getDrawable(R.drawable.tutorial_wallpapers_3));

                ImageView img2 = view.findViewById(R.id.tutorialimage2);
                img2.setImageDrawable(getDrawable(R.drawable.tutorial_wallpapers_1));

                ImageView img3 = view.findViewById(R.id.tutorialimage3);
                img3.setImageDrawable(getDrawable(R.drawable.tutorial_wallpapers_2));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);
                return true;
            }
        });

        oldDarkMode = findViewById(R.id.dark_mode_tweak_button);
        oldDarkModeStatus = findViewById(R.id.dark_mode_status);
        if(load("aa_night_mode_revert")) {
            oldDarkMode.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.dark_mode_tweak));
            changeStatus(oldDarkModeStatus, 2, false);
        } else {
            oldDarkMode.setText(getString(R.string.disable_tweak_string) + getString(R.string.dark_mode_tweak));
            changeStatus(oldDarkModeStatus, 0, false);
        }

        oldDarkMode.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_night_mode_revert")){
                            revert("aa_night_mode_revert");
                            oldDarkMode.setText(getString(R.string.disable_tweak_string) + getString(R.string.dark_mode_tweak));
                            changeStatus(oldDarkModeStatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            oldDarkMode(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        oldDarkMode.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_dark_mode));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);
                return true;
            }
        });


        disableTelemetryButton = findViewById(R.id.telemetry_disable_tweak);
        telemetryStatus = findViewById(R.id.telemetry_disable_status);
        if(load("kill_telemetry")) {
            disableTelemetryButton.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.telemetry_string));
            changeStatus(telemetryStatus, 2, false);
        } else {
            disableTelemetryButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.telemetry_string));
            changeStatus(telemetryStatus, 0, false);
        }

        disableTelemetryButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("kill_telemetry")){
                            revert("kill_telemetry");
                            disableTelemetryButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.telemetry_string));
                            changeStatus(telemetryStatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            disableTelemetry(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        disableTelemetryButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_telemetry));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);
                return true;
            }
        });

        activateMediaTabs = findViewById(R.id.media_tabs_tweak);
        mediaTabsStatus = findViewById(R.id.media_tabs_status);
        if(load("aa_media_tabs")) {
            activateMediaTabs.setText(getString(R.string.disable_tweak_string) + getString(R.string.media_tabs_string));
            changeStatus(mediaTabsStatus, 2, false);
        } else {
            activateMediaTabs.setText(getString(R.string.enable_tweak_string) + getString(R.string.media_tabs_string));
            changeStatus(mediaTabsStatus, 0, false);
        }

        activateMediaTabs.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_media_tabs")){
                            revert("aa_media_tabs");
                            activateMediaTabs.setText(getString(R.string.enable_tweak_string) + getString(R.string.media_tabs_string));
                            changeStatus(mediaTabsStatus, 0, true);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchMediaTabs(view, UserCount);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        activateMediaTabs.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate( R.layout.dialog_layout, null);


                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_media_tabs));

                ImageView tutorialimg = view.findViewById(R.id.tutorialimage1);
                tutorialimg.setImageResource(R.drawable.tutorial_tabs_media);

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 800);
                return true;
            }
        });



    }


    private void revert(final String toRevert) {

        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.version);
        item.setTitle("V." + BuildConfig.VERSION_NAME);
        return super.onPrepareOptionsMenu(menu);
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
                break;

            case R.id.revert_everything:
                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                builder.setMessage(getString(R.string.revert_everything_dialog))
                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getAndRemoveOptionsSelected();
                            }
                        })
                        .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.setCancelable(true);
                android.support.v7.app.AlertDialog Alert1 = builder.create();
                Alert1.show();
                break;
            case R.id.aa_settings:
                String packageName = "com.google.android.projection.gearhead";
                openApp(getApplicationContext(), packageName);

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void save(final boolean isChecked, String key) {
        SharedPreferences sharedPreferences = getPreferences(getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

    public void saveValue(final int value, String key) {
        SharedPreferences sharedPreferences = getPreferences(getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

     public boolean load(String key) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public int loadValue(String key) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu, menu );
        return true;
    }


    public void patchforapps(final View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);

        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);


        SharedPreferences appsListPref = getApplicationContext().getSharedPreferences("appsListPref", 0);
        Map<String, ?> allEntries = appsListPref.getAll();
            logs.append("--  Apps which will be added to whitelist: --\n");
            String whiteListString = "";
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                logs.append("\t\t- " + entry.getValue() + " (" + entry.getKey() + ")\n");
                whiteListString += "," + entry.getKey();
            }

            whiteListString = whiteListString.replaceFirst(",", "");
            final String whiteListStringFinal = whiteListString;
            final StringBuilder finalCommand = new StringBuilder();

            for (int i = 0; i<=(usercount-1) ; i ++) {
                finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"should_bypass_validation\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
                finalCommand.append(i);
                finalCommand.append(",1) ,1,1);");
            }

            new Thread() {
                @Override
                public void run() {
                    String path = getApplicationInfo().dataDir;
                    boolean suitableMethodFound = true;
                    

                    appendText(logs, "\n\n-- Drop Triggers  --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DROP TRIGGER IF EXISTS aa_patched_apps; DROP TRIGGER IF EXISTS after_delete;" +
                                    "DROP TRIGGER IF EXISTS aa_patched_apps_fix;'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, "\n\n--  DELETE old Flags  --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name=\"app_white_list\";'\n"
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
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);" +
                                        "'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER aa_patched_apps AFTER DELETE\n" +
                                        "ON Flags\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "END;\n'"

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
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);" +
                                        "'"
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
                                        "END;\n" +
                                        "'\n"
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
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);" +
                                        "'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER aa_patched_apps AFTER DELETE\n" +
                                        "ON Flags\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "END;'\n"
                        ).getStreamLogsWithLabels());
                        appendText(logs, "\n--  end SQL method #3  --");

                    } else if (runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"'").getInputStreamLog().equals("1")) {

                        appendText(logs, "\n\n--  run SQL method #4  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);" +
                                        "'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER aa_patched_apps AFTER DELETE\n" +
                                        "ON Flags\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "END;'\n"
                        ).getStreamLogsWithLabels());
                        appendText(logs, "\n--  end SQL method #4  --");

                    } else {
                        suitableMethodFound = false;
                        appendText(logs, "\n\n--  Suitable method NOT found!  --");
                    }

                    // Check Start
                    if (suitableMethodFound) {

                        appendText(logs, "\n\n--  new fix method   --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db '" +
                                        finalCommand + "'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER aa_patched_apps_fix AFTER DELETE\n" +
                                        "ON FlagOverrides\n" +
                                        "BEGIN\n" + finalCommand + "END;'\n"
                        ).getStreamLogsWithLabels());

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
                            patchapps.setText(getString(R.string.unpatch) + getString(R.string.patch_custom_apps));
                            save(true, "aa_patched_apps");
                            changeStatus(patchappstatus, 1, false);
                        } else {
                            appendText(logs, "\n\n--  Check NOT OK.  --");
                            appendText(logs, "\n     Length before delete and after was not equal.");
                            appendText(logs, "\n        Before: " + checkStep1.getInputStreamLog().length());
                            appendText(logs, "\n        After:  " + checkStep3.getInputStreamLog().length());
                        }
                    }
                    dialog.dismiss();
                    // Check End
                }
            }.start();

    }

    public void patchforassistshort(final View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

            new Thread() {
                @Override
                public void run() {
                    String path = getApplicationInfo().dataDir;
                    boolean suitableMethodFound = true;
                    

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
                                        "'" + finalCommand + "'").getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER assist_short AFTER DELETE\n" +
                                        "ON FlagOverrides\n" +
                                        "BEGIN\n" +
                                        finalCommand +
                                        "END;'\n"
                        ).getStreamLogsWithLabels());
                        appendText(logs, "\n--  end SQL method   --");
                        save(true, "assist_short");
                        changeStatus(assistantShortcutsStatus, 1, false);
                        assistshort.setText(getString(R.string.disable_tweak_string) + getString(R.string.enable_assistant_shortcuts));
                    } else {
                        suitableMethodFound = false;
                        appendText(logs, "\n\n--  Suitable method NOT found!  --");
                    }

                    dialog.dismiss();

                }
            }.start();
        }

    public void patchrailassistant(final View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                

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
                                    "'" + finalCommand + "'").getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_assistant_rail AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    finalCommand+
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_assistant_rail");
                    assistanim.setText(getString(R.string.disable_tweak_string) + getString(R.string.enable_assistant_animation_in_navbar));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }



            }
        }.start();
    }

    public void patchforspeed(final View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,99999999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                

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
                                    "'" + finalCommand + "'").getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_speed_hack AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    finalCommand +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_speed_hack");
                    changeStatus(noSpeedRestrictionsStatus, 1, false);
                    nospeed.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();
    }

    public void multiDisplay(final View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__enabled\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__multi_region_new_widescreen_activities_enabled\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__require_bfr\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"EnhancedNavigationMetadata__enabled\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"EnhancedNavigationMetadata__verify_turn_side_when_disabled\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__clustersim_enabled\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__gal_munger_enabled\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__multi_region_enabled\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__cluster_launcher_enabled\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS multi_display;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'\n").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'" + finalCommand + "'").getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER multi_display AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    finalCommand +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "multi_display");
                    changeStatus(mdstatus, 1, false);
                    mdbutton.setText(getString(R.string.disable_tweak_string) + getString(R.string.multi_display_string));

                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();
    }

    public void patchfortouchlimit(final View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);


        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                



                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_six_tap;'"
                ).getStreamLogsWithLabels());

                if (!runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT COUNT(DISTINCT user) FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\";'").getInputStreamLog().equals("0")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'" + finalCommand + "'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_six_tap AFTER DELETE\n" +
                                    "ON FlagOverrides\n BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_six_tap");
                    changeStatus(taplimitstatus, 1, false);
                    taplimitat.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.disable_speed_limitations));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();

            }
        }.start();
    }

    public void navpatch(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_startup_policy; DROP TRIGGER IF EXISTS aa_startup_policy_cleanup'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM FLAGS WHERE packageName=\"com.google.android.projection.gearhead\" AND name LIKE \"SystemUi__start%\";\n"+
                                    finalCommand + "'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                                    path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_startup_policy AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;\n" +
                                            "CREATE TRIGGER aa_startup_policy_cleanup AFTER INSERT\n" +
                                            "ON Flags\n" +
                                            "BEGIN\n" + "DELETE FROM FLAGS WHERE packageName=\"com.google.android.projection.gearhead\" AND name LIKE \"SystemUi__start%\";\n" +
                                            "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_startup_policy");
                    changeStatus(navstatus, 1, false);
                    startupnav.setText(getString(R.string.disable_tweak_string) + getString(R.string.navigation_at_start));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();


    }

    public void disableBatteryWarning(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__warning_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__on_at_start_warning_delay_ms\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__switched_on_warning_delay_ms\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS battery_saver_warning;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name=\"BatterySaver__warning_enabled\";\n" +
                                    "DELETE FROM Flags WHERE name=\"BatterySaver__switched_on_warning_delay_ms\";\n" +
                                    "DELETE FROM Flags WHERE name=\"BatterySaver__on_at_start_warning_delay_ms\";\n" + finalCommand + "'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER battery_saver_warning AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "battery_saver_warning");
                    changeStatus(batteryWarningStatus, 1, false);
                    batteryWarning.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_warning));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();


    }

    public void battOutline(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
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
                                    "'DELETE FROM Flags WHERE name=\"BatterySaver__icon_outline_enabled\";\n"+ finalCommand + "'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_battery_outline AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_battery_outline");
                    changeStatus(batteryOutlineStatus, 1, false);
                    batteryoutline.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_outline_string));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();


    }

    public void opaqueStatusBar (View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Boardwalk__status_bar_force_opaque\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                

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
                                    "'DELETE FROM Flags WHERE name=\"Boardwalk__status_bar_force_opaque\";\n"+ finalCommand + "'").getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_sb_opaque AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_sb_opaque");
                    changeStatus(opaqueStatus, 1, false);
                    statusbaropaque.setText(getString(R.string.disable_tweak_string) + getString(R.string.statb_opaque_string));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();

    }

    public void forceNoBt (View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"BluetoothPairing__car_bluetooth_service_disable\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"BluetoothPairing__car_bluetooth_service_skip_pairing\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS bluetooth_pairing_off;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name=\"BluetoothPairing__car_bluetooth_service_disable\";\n" +
                                    "DELETE FROM Flags WHERE name=\"BluetoothPairing__car_bluetooth_service_skip_pairing\";\n" +
                                    finalCommand + "'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER bluetooth_pairing_off AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "bluetooth_pairing_off");
                    bluetoothoff.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.bluetooth_auto_connect));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();

    }

    public void oldDarkMode (View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"IndependentNightModeFeature__enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;


                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_night_mode_revert;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name=\"IndependentNightModeFeature__enabled\";\n" +
                                    finalCommand + "'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_night_mode_revert AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_night_mode_revert");
                    changeStatus(oldDarkModeStatus, 1, false);
                    oldDarkMode.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.dark_mode_tweak));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();

    }

    public void disableTelemetry (View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarEventLoggerRefactorFeature__convert_car_setup_analytics_telemetry\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarServiceTelemetry__enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarServiceTelemetry__is_wifi_kbps_logging_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarServiceTelemetry__log_battery_temperature\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarServiceTelemetry__wifi_latency_log_frequency_ms\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,99999999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.gms.car\",0,\"ConnectivityLogging__heartbeat_interval_ms\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,99999999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"TelemetryDriveIdFeature__enable_log_event_validation\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"TelemetryDriveIdFeature__enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"UsbStatusLoggingFeature__monitor_usb_ping_telemetry_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"TelemetryDriveIdForGearheadFeature__enable_frx_setup_logging_via_gearhead\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.gms.car\",0,\"AudioStatsLoggingFeature__audio_stats_logging_period_milliseconds\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,99999999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"FrameworkMediaStatsLoggingFeature__is_media_stats_queue_time_logging_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__num_background_threads\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__include_extra_events\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__enable_heartbeat\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"WifiChannelLogging__enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__session_info_dump_size\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BluetoothMetadataLogger__enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarEventLoggerRefactorFeature__convert_car_analytics_telemetry\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Bugfix__sensitive_permissions_extra_logging\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__log_bluetooth_rssi\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__save_log_when_usb_starts\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__skip_retroactive_usb_logging\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"InternetConnectivityLogging__enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Telemetry__local_logging\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"WirelessProjectionInGearhead__wireless_wifi_additional_start_logging\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__r_telemetry_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"AssistantSilenceDiagnostics__enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"TelemetryDriveIdForGearheadFeature__enable_continuous_telemetry_binding\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"TelemetryDriveIdForGearheadFeature__enable_frx_setup_logging_via_gearhead\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"TelemetryDriveIdForGearheadFeature__enable_telemetry_impl_conversion\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__long_session_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__short_session_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__session_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__use_realtime_if_invalid\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;


                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS kill_telemetry;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name LIKE \"%telemetry%\" AND packageName=\"com.google.android.projection.gearhead\";\n" +
                                    "DELETE FROM Flags WHERE name LIKE \"%telemetry%\" AND packageName=\"com.google.android.gms.car\";" +
                                    finalCommand + "'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER kill_telemetry AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "kill_telemetry");
                    changeStatus(telemetryStatus, 1, false);
                    disableTelemetryButton.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.telemetry_string));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();

    }

    public void setHunDuration (View view, final int value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__hun_default_heads_up_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + value + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                

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
                                    "'DELETE FROM Flags WHERE name=\"SystemUi__hun_default_heads_up_timeout_ms\";\n"+ finalCommand + "'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_hun_ms AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_hun_ms");
                    changeStatus(messagesHunStatus, 1, false);
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();

    }

    public void setMediaHunDuration (View view, final int value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__media_hun_in_rail_widget_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + value + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_media_hun;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name=\"SystemUi__media_hun_in_rail_widget_timeout_ms\";\n"+ finalCommand + "'"
                                   ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_media_hun AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_media_hun");
                    changeStatus(mediaHunStatus, 1, false);
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();

    }

    private void setCalendarEvents(View view, int value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"McFly__num_days_in_agenda_view\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + value + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;


                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS calendar_aa_tweak;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name=\"McFly__num_days_in_agenda_view\";\n"+ finalCommand + "'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER calendar_aa_tweak AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "calendar_aa_tweak");
                    changeStatus(calendarTweakStatus, 1, false);
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();

    }

    public void forceWideScreen (View view, final int value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);
        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__widescreen_breakpoint_dp\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + value + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS force_ws;\n DROP TRIGGER IF EXISTS force_no_ws;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'" + finalCommand + "'").getStreamLogsWithLabels());

                    String decideWhat = new String();

                    switch (value) {
                        case 470: { decideWhat = "force_ws"; break; }
                        case 3000: { decideWhat = "force_no_ws"; break; }
                    }

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER " + decideWhat + " AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, decideWhat);
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();

    }

    public void activateWallpapers (View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CustomWallpaper__enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;



                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_wallpapers;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'" +
                                    finalCommand + "'\n"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_wallpapers AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_wallpapers");
                    changeStatus(activateWallpapersStatus, 1, false);
                    activateWallpapersButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.custom_wallpapers));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();

    }

    public void messagesTweak (View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteLite__notification_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteLite__sms_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"NotificationClientAbstraction__enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__launcher_notification_badge_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;


                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_messaging_apps;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'" +
                                    finalCommand + "'\n"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_messaging_apps AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_messaging_apps");
                    changeStatus(messagesTweakStatus, 1, false);
                    messagesButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.messages_tweak_string));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
            }
        }.start();

    }

    public void patchMediaTabs (View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);
        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Tabbouleh__tabs_media_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Tabbouleh__media_browse_back_to_top_level_button_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Tabbouleh__tabs_media_sticky_tab_enabled\", (SELECT DISTINCT user FROM Flags WHERE user != \"\"LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;


                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_media_tabs;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'" +
                                    finalCommand + "'\n"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_media_tabs AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_media_tabs");
                    changeStatus(mediaTabsStatus, 1, false);
                    activateMediaTabs.setText(getString(R.string.disable_tweak_string) + getString(R.string.media_tabs_string));

                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }
                dialog.dismiss();
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


    private void appendText(final TextView textView, final String s){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(s);
            }
        });
    }

    public void loadStatus(final String path) {

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.loading), true);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String get_names = runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"FlagOverrides\";" +
                                "SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\" AND name=\"after_delete\";" +
                                "SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\" AND name=\"aa_patched_apps\";'").getInputStreamLog();
                String[] lines = get_names.split(System.getProperty("line.separator"));
                for (int i = 0; i < lines.length; i++) {
                    save(true, lines[i]);
                }
                if (load("aa_hun_ms")) {
                    saveValue(Integer.parseInt(runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                            "'SELECT DISTINCT intVal FROM FlagOverrides WHERE name=\"SystemUi__hun_default_heads_up_timeout_ms\";'").getInputStreamLog()), "messages_hun_value");
                }
                if (load("aa_media_hun")) {
                    saveValue(Integer.parseInt(runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT DISTINCT intVal FROM FlagOverrides WHERE name=\"SystemUi__media_hun_in_rail_widget_timeout_ms\";'").getInputStreamLog()), "media_hun_value");
                }
                if (load("calendar_aa_tweak")) {
                    saveValue(Integer.parseInt(runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT DISTINCT intVal FROM FlagOverrides WHERE name=\"McFly__num_days_in_agenda_view\";'").getInputStreamLog()), "agenda_value");
                }
                dialog.dismiss();
            }
        });

    }

    public void getAndRemoveOptionsSelected() {
        final TextView log = findViewById(R.id.logs);
        final String[] allTriggerString = {new String()};
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.loading), true);
        new Thread() {
            @Override
            public void run() {

                String path = appDirectory;
                allTriggerString[0] = path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'";
                String get_names = runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"FlagOverrides\";" +
                                "SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\" AND name=\"after_delete\";" +
                                "SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\" AND name=\"aa_startup_policy_cleanup\";" +
                                "SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\" AND name=\"aa_patched_apps\";'").getInputStreamLog();
                appendText(log, get_names);
                String[] lines = get_names.split(System.getProperty("line.separator"));
                for (int i = 0; i < lines.length; i++) {
                    appendText(log, runSuWithCmd(path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'DROP TRIGGER IF EXISTS \"" + lines[i] + "\";'").getOutputStreamLog());
                }
                runSuWithCmd(path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'DELETE FROM FlagOverrides;'");
                dialog.dismiss();
            }

        }.start();

        return;
    }

    public static void openApp(Context context, String packageName) {
        if (isAppInstalled(context, packageName))
            if (isAppEnabled(context, packageName)) {
                PackageManager pm = context.getPackageManager();
                Intent launchIntent = new Intent( "com.google.android.projection.gearhead.SETTINGS");
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
            }
            else Toast.makeText(context, context.getString(R.string.not_enabled_warning), Toast.LENGTH_SHORT).show();
        else Toast.makeText(context, context.getString(R.string.not_installed_warning), Toast.LENGTH_SHORT).show();
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }

    private static boolean isAppEnabled(Context context, String packageName) {
        Boolean appStatus = false;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (ai != null) {
                appStatus = ai.enabled;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appStatus;
    }

    private void changeStatus (ImageView resource, int status, boolean doAnimation) {
        final RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(400);
        rotate.setInterpolator(new LinearInterpolator());
        switch (status) {
            case 2: {
                resource.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                resource.setColorFilter(Color.argb(255, 0, 255, 0));
                break;
            }
            case 0: {
                resource.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                resource.setColorFilter(Color.argb(255, 255, 0, 0));
                break;
            }
            case 1: {
                resource.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                resource.setColorFilter(Color.argb(255, 255, 255, 0));
                break;
            }
        }
        if (doAnimation) {
            resource.startAnimation(rotate);
        }
    }

}
