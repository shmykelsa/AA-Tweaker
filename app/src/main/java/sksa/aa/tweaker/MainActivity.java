package sksa.aa.tweaker;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

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
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jpaste.exceptions.PasteException;
import org.jpaste.pastebin.Pastebin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sksa.aa.tweaker.CarRemoverActivity.CarRemover;
import sksa.aa.tweaker.Utils.BottomDialog;
import sksa.aa.tweaker.Utils.PhenotypeDb;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    public static String appDirectory = new String();

    boolean suitableMethodFound;

    private boolean temp;

    private static Context mContext;
    private ImageView noSpeedRestrictionsStatus;
    private ImageView taplimitstatus;
    private ImageView navstatus;
    private ImageView patchappstatus;
    private ImageView messageAutoReadStatus;
    private ImageView batteryOutlineStatus;
    private ImageView forceWideScreenStatus;
    private ImageView forcePortraitStatus;
    private ImageView messagesHunStatus;
    private ImageView mediaHunStatus;
    private ImageView intertialScrollStatus;
    private ImageView btstatus;
    private ImageView mdstatus;
    private ImageView batteryWarningStatus;
    private ImageView verticalBarStatus;
    private ImageView telemetryStatus;
    private ImageView forceNoWideScreenStatus;
    private ImageView usbBitrateStatus;
    private ImageView wifiBitrateStatus;
    private ImageView newSeekbarTweakStatus;
    private ImageView coolwalkTweakStatus;
    private ImageView nocoolwalkTweakStatus;
    private ImageView assistantTipsTweakStatus;
    private ImageView declineSmsTweakStatus;
    private ImageView uxprototypeTweakStatus;
    private ImageView materialYouTweakStatus;
    private TextView currentlySetHun;
    private TextView currentlySetMediaHun;
    private TextView currentlySetUSBSeekbar;
    private TextView currentlySetWiFiSeekbar;
    private Button rebootButton;
    private Button nospeed;
    private Button taplimitat;
    private Button coolwalkDayNightTweak;
    private Button patchapps;
    private Button messageAutoReadTweak;
    private Button batteryoutline;
    private Button forceNoWideScreen;
    private Button forceWideScreenButton;
    private Button forcePortrait;
    private Button messagesHunThrottling;
    private Button mediathrottlingbutton;
    private Button intertialScrollButton;
    private Button bluetoothoff;
    private Button mdbutton;
    private Button batteryWarning;
    private Button verticalBarTweakButton;
    private Button disableTelemetryButton;
    private Button tweakUSBBitrateButton;
    private Button tweakWiFiBitrateButton;
    private Button newSeekbarTweakButton;
    private Button coolwalkTweak;
    private Button nocoolwalkTweak;
    private Button deleteCarMode;
    private Button assistantTipsButton;
    private Button declineSmsTweak;
    private Button uxprototypeButton;
    private Button materialYouButton;
    private boolean animationRun;
    private boolean  urlprototype;


    ProgressDialog progress;

    SharedPreferences accountsPrefs;
    private URL url;



    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        Bundle extras = new Bundle()    ;

        try {
            extras = getIntent().getExtras();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        final String path = getApplicationInfo().dataDir;
        appDirectory = path;
        loadStatus(path);



        if (extras != null && extras.getString("NewVersionName") != null) {

            BottomDialog bd;

            final BottomDialog builder2 = new BottomDialog.Builder(this)
                    .setTitle(R.string.new_version_available)
                    .setContent(getString(R.string.go_to_new_version, extras.getString("NewVersionName")))
                    .setPositiveBackgroundColor(R.color.colorPrimary)
                    .setPositiveText(R.string.go_to_download)
                    .setNegativeText(R.string.ignore_for_now)
                    .onPositive(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(@NonNull BottomDialog dialog) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/shmykelsa/AA-Tweaker/releases/")));
                        }
                    })
                    .onNegative(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(@NonNull BottomDialog dialog) {

                        }
                    })
                    .setBackgroundColor(R.color.centercolor).build();

            builder2.show();
        }









        setContentView(R.layout.activity_main);

        ImageView revertNotificationDuration = findViewById(R.id.revert_hun_throttling);
        ImageView revertMediaNotificationDuration = findViewById(R.id.revert_media_hun);
        ImageView revertWifiBitrate = findViewById(R.id.revert_bitrate_wifi);
        ImageView revertUsbBitrate = findViewById(R.id.revert_bitrate_usb);


        ViewPager viewPager = findViewById(R.id.viewpager);
        CommonPageAdapter adapter = new CommonPageAdapter();
        adapter.insertViewId(R.id.page_one);
        adapter.insertViewId(R.id.page_two);
        viewPager.setAdapter(adapter);


        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Button toapp = findViewById(R.id.toapp_button);
        toapp.setOnClickListener(
                new View.OnClickListener() {
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
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rebootDialog.show(getSupportFragmentManager(), "RebootDialog");
                    }
                }
        );

        rebootButton = findViewById(R.id.reboot_button);



        TextView logs = initiateLogsText();

        appendText(logs, runSuWithCmd(
                path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                        "'SELECT * FROM FlagOverrides;'"
        ).getStreamLogsWithLabels());
        appendText(logs, runSuWithCmd(
                path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                        "'SELECT * FROM sqlite_master WHERE type=\"trigger\";'"
        ).getStreamLogsWithLabels());


        animationRun = false;
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
                if (upperTextView.getText().toString().equals(getString(R.string.legend))) {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upperTextView.startAnimation(legendAnim);
                    }
                });
            }
        }, 12000, 12000);

/*        nospeed = findViewById(R.id.nospeed);
        noSpeedRestrictionsStatus = findViewById(R.id.speedhackstatus);
        if (load("aa_speed_hack")) {
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
                        if (load("aa_speed_hack")) {
                            revert("aa_speed_hack");
                            nospeed.setText(getString(R.string.disable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
                            changeStatus(noSpeedRestrictionsStatus, 0, true);
                            showRebootButton();
                        } else {
                            patchforspeed(UserCount);
                        }
                    }
                });

        setOnLongClickListener(nospeed, R.string.tutorial_nospeed, R.drawable.tutorial_nospeed);*/



        taplimitat = findViewById(R.id.taplimit);
        taplimitstatus = findViewById(R.id.sixtapstatus);
        if (load("aa_six_tap")) {
            taplimitat.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.disable_speed_limitations));
            changeStatus(taplimitstatus, 2, false);

        } else {
            taplimitat.setText(getString(R.string.disable_tweak_string) + getString(R.string.disable_speed_limitations));
            changeStatus(taplimitstatus, 0, false);

        }

        setOnLongClickListener(taplimitat, R.string.tutorial_sixtap, R.drawable.tutorial_sixtap);


        taplimitat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_six_tap")) {
                            revert("aa_six_tap");
                            taplimitat.setText(getString(R.string.disable_tweak_string) + getString(R.string.disable_speed_limitations));
                            changeStatus(taplimitstatus, 0, true);
                            showRebootButton();
                        } else {
                            patchfortouchlimit();
                        }
                    }
                });

        coolwalkDayNightTweak = findViewById(R.id.coolwalkdaynighttweak);
        navstatus = findViewById(R.id.coolwalkdaynightstatus);
        if (load("coolwalk_daynight_tweak")) {
            coolwalkDayNightTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.coolwalk_daynight_tweak));
            changeStatus(navstatus, 2, false);
        } else {
            coolwalkDayNightTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.coolwalk_daynight_tweak));
            changeStatus(navstatus, 0, false);
        }
        coolwalkDayNightTweak.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("coolwalk_daynight_tweak")) {
                            revert("coolwalk_daynight_tweak");
                            coolwalkDayNightTweak.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.coolwalk_daynight_tweak));
                            changeStatus(navstatus, 0, true);
                            showRebootButton();
                        } else {
                            coolwalkdaynightpatch();
                        }
                    }
                });

        setOnLongClickListener(coolwalkDayNightTweak, R.string.coolwalk_daynight_tutorial, R.drawable.tutorial_coolwalkdaynight);

        patchapps = findViewById(R.id.patchapps);
        patchappstatus = findViewById(R.id.patchedappstatus);


        if (load("aa_patched_apps")) {
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
                        if (load("aa_patched_apps")) {
                            revert("aa_patched_apps");
                            patchapps.setText(getString(R.string.patch_app) + getString(R.string.patch_custom_apps));
                            changeStatus(patchappstatus, 0, true);
                            showRebootButton();
                        } else {
                            SharedPreferences appsListPref = getApplicationContext().getSharedPreferences("appsListPref", 0);
                            Map<String, ?> allEntries = appsListPref.getAll();
                            if (allEntries.isEmpty()) {
                                Intent intent = new Intent(MainActivity.this, AppsList.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), getString(R.string.choose_apps_warning), Toast.LENGTH_LONG).show();
                            } else{
                                temp = true;
                                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(getString(R.string.warning_title));
                                builder.setMessage(getResources().getString(R.string.warning_patch_apps));
                                builder.setNeutralButton( getString(android.R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                temp = false;
                                                patchforapps();

                                            }
                                        });
                                builder.setNegativeButton( android.R.string.no
                                        ,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                builder.setCancelable(false);
                                builder.show();

                            }
                        }
                    }
                });

        setOnLongClickListener(patchapps, R.string.tutorial_patchapps);


        messageAutoReadTweak = findViewById(R.id.message_autoread_tweak_button);
        messageAutoReadStatus = findViewById(R.id.message_autoread_tweak_status);
        if (load("aa_message_autoread")) {
            messageAutoReadTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.message_auto_read));
            changeStatus(messageAutoReadStatus, 2, false);

        } else {
            messageAutoReadTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.message_auto_read));
            changeStatus(messageAutoReadStatus, 0, false);
        }

        messageAutoReadTweak.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_message_autoread")) {
                            revert("aa_message_autoread");
                            messageAutoReadTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.message_auto_read));
                            changeStatus(messageAutoReadStatus, 0, true);
                            showRebootButton();
                        } else {
                            messageAutoRead();
                        }
                    }
                });

        setOnLongClickListener(messageAutoReadTweak, R.string.tutorial_autoplay_message);


        uxprototypeButton = findViewById(R.id.uxprototypetweak);
        uxprototypeTweakStatus = findViewById(R.id.uxptototypestatus);
        if (load("uxprototype_tweak")) {
            uxprototypeButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.uxprototype_tweak));
            changeStatus(uxprototypeTweakStatus, 2, false);

        } else {
            uxprototypeButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.uxprototype_tweak));
            changeStatus(uxprototypeTweakStatus, 0, false);
        }

        uxprototypeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (load("uxprototype_tweak")) {
                            revert("uxprototype_tweak");
                            uxprototypeButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.uxprototype_tweak));
                            changeStatus(uxprototypeTweakStatus, 0, true);
                            showRebootButton();
                        } else {
                            final Dialog uxprototypeDialog;
                            uxprototypeDialog = new Dialog(MainActivity.this);
                            uxprototypeDialog.setContentView(R.layout.dialog_layout);
                            uxprototypeDialog.setCancelable(false);


                            WindowManager.LayoutParams lp = setDialogLayoutParams(uxprototypeDialog);

                            final EditText readURL = uxprototypeDialog.findViewById(R.id.textuxprototype);
                            readURL.setVisibility(View.VISIBLE);

                            TextView acceptButton =  uxprototypeDialog.findViewById(R.id.yes);
                            TextView cancelButton =  uxprototypeDialog.findViewById(R.id.no);

                            acceptButton.setVisibility(View.VISIBLE);
                            cancelButton.setVisibility(View.VISIBLE);
                            acceptButton.setOnClickListener(new View.OnClickListener() {

                                public void onClick(View v) {

                                        String url = readURL.getText().toString();
                                        if (!url.contains("http://") && !url.contains("https://")) {
                                           url =  "http://" + url;
                                        }


                                    try {
                                        uxprototypeTweak(new URL(readURL.getText().toString()));
                                        uxprototypeDialog.dismiss();
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }

                                    if (uxprototypeDialog.isShowing()) {
                                        Toast.makeText(uxprototypeDialog.getContext(), R.string.uxprototype_dialog, Toast.LENGTH_LONG).show();
                                    }


                                }
                            });
                            cancelButton.setOnClickListener(new View.OnClickListener() {

                                public void onClick(View v) {
                                    uxprototypeDialog.dismiss();
                                }
                            });
                            uxprototypeDialog.show();
                            uxprototypeDialog.getWindow().setAttributes(lp);

                        }
                    }


                });


        setOnLongClickListener(uxprototypeButton, R.string.uxprototype_tutorial);


        materialYouButton = findViewById(R.id.materialyoutweak);
        materialYouTweakStatus = findViewById(R.id.materialyoutweakstatus);
        if (load("aa_material_you")) {
            materialYouButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.materialyou_tweak));
            changeStatus(materialYouTweakStatus, 2, false);

        } else {
            materialYouButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.materialyou_tweak));
            changeStatus(materialYouTweakStatus, 0, false);
        }

        materialYouButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (load("uxprototype_tweak")) {
                            revert("uxprototype_tweak");
                            materialYouButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.materialyou_tweak));
                            changeStatus(materialYouTweakStatus, 0, true);
                            showRebootButton();
                        } else {
                            activateMaterialYou();
                        }
                    }


                });


        setOnLongClickListener(materialYouButton, R.string.tutorial_materialyou, R.drawable.tutorial_materialyou);



        batteryoutline = findViewById(R.id.battoutline);
        batteryOutlineStatus = findViewById(R.id.batterystatus);
        if (load("aa_battery_outline")) {
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
                        if (load("aa_battery_outline")) {
                            revert("aa_battery_outline");
                            batteryoutline.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_outline_string));
                            changeStatus(batteryOutlineStatus, 0, true);
                            showRebootButton();
                        } else {
                            battOutline();
                        }
                    }
                });

        setOnLongClickListener(batteryoutline, R.string.tutorial_battery_outline, R.drawable.tutorial_outline);


        forceNoWideScreen = findViewById(R.id.force__no_ws_button);
        forceNoWideScreenStatus = findViewById(R.id.force_no_ws_status);


        forceWideScreenButton = findViewById(R.id.force_ws_button);
        forceWideScreenStatus = findViewById(R.id.force_ws_status);

        forcePortrait = findViewById(R.id.force_portrait_button);
        forcePortraitStatus = findViewById(R.id.force_portrait_status);


        if (load("force_ws")) {
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
                        if (load("force_ws")) {
                            revert("force_ws");
                            forceWideScreenButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.force_widescreen_text));
                            changeStatus(forceWideScreenStatus, 0, true);
                            showRebootButton();
                        } else {
                            forceWideScreen(view, 470);
                            forceWideScreenButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.force_widescreen_text));
                            if (load("force_no_ws") || load ("force_portrait")) {
                                Toast.makeText(getApplicationContext(), getString(R.string.force_disable_widescreen_warning), Toast.LENGTH_LONG).show();
                                revert("force_no_ws");
                                revert("force_portrait");
                                save(false, "force_no_ws");
                                save(false, "force_portrait");
                            }
                        }
                    }
                });

        setOnLongClickListener(forceWideScreenButton, R.string.tutorial_widescreen, R.drawable.tutorial_widescreen);


        if (load("force_no_ws")) {
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
                        if (load("force_no_ws")) {
                            revert("force_no_ws");
                            forceNoWideScreen.setText(getString(R.string.force_disable_tweak) + getString(R.string.base_no_ws));
                            changeStatus(forceNoWideScreenStatus, 0, true);
                            showRebootButton();
                        } else {
                            forceWideScreen(view, 1920);
                            forceNoWideScreen.setText(getString(R.string.reset_tweak) + getString(R.string.base_no_ws));
                            if (load("force_portrait") || load ("force_ws")) {
                                revert("force_portrait");
                                revert("force_ws");
                                save(false, "force_portrait");
                                save(false, "force_ws");
                            }
                        }
                    }
                });

        setOnLongClickListener(forceNoWideScreen, R.string.tutorial_no_widescreen, R.drawable.tutorial_nowidescreen);

        if (load("force_portrait")) {
            forcePortrait.setText(getString(R.string.reset_tweak) + getString(R.string.portrait_layout));
            changeStatus(forcePortraitStatus, 2, false);
        } else {
            forcePortrait.setText(getString(R.string.enable_tweak_string) + getString(R.string.portrait_layout));
            changeStatus(forcePortraitStatus, 0, false);
        }

        forcePortrait.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("force_portrait")) {
                            revert("force_portrait");
                            forcePortrait.setText(getString(R.string.enable_tweak_string) + getString(R.string.portrait_layout));
                            changeStatus(forcePortraitStatus, 0, true);
                            showRebootButton();
                        } else {
                            forceWideScreen(view, 10);
                            forcePortrait.setText(getString(R.string.disable_tweak_string) + getString(R.string.portrait_layout));
                            if (load("force_no_ws") || load ("force_ws")) {
                                Toast.makeText(getApplicationContext(), getString(R.string.force_disable_widescreen_warning), Toast.LENGTH_LONG).show();
                                revert("force_no_ws");
                                revert("force_ws");
                                save(false, "force_no_ws");
                                save(false, "force_ws");
                            }
                        }
                    }
                });

        setOnLongClickListener(forcePortrait, R.string.tutorial_portrait, R.string.restricted_coolwalk, R.drawable.tutorial_portrait);

        messagesHunThrottling = findViewById(R.id.hunthrottlingbutton);
        final int[] messagesHunScrollbarValue = {0};
        final TextView displayValue = findViewById(R.id.seekbar_text);
        final SeekBar hunSeekbar = findViewById(R.id.messages_hun_seekbar);
        hunSeekbar.setProgress(8000);
        displayValue.setText(hunSeekbar.getProgress() + "ms");
        hunSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int) Math.round(progress / 100)) * 100;
                seekBar.setProgress(progress);
                messagesHunScrollbarValue[0] = hunSeekbar.getProgress();
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                if (hunSeekbar.getProgress() == 8000) {
                    messagesHunThrottling.setText(getString(R.string.reset_tweak) + getString(R.string.set_notification_duration_to) + getString(R.string.default_string));
                } else {
                    messagesHunThrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress() + " ms");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                messagesHunThrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress() + " ms");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                messagesHunScrollbarValue[0] = hunSeekbar.getProgress();
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                if (hunSeekbar.getProgress() == 8000) {
                    messagesHunThrottling.setText(getString(R.string.reset_tweak) + getString(R.string.set_notification_duration_to) + getString(R.string.default_string));
                } else {
                    messagesHunThrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress() + " ms");
                }
            }
        });

        revertNotificationDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hunSeekbar.setProgress(8000);
            }
        });


        messagesHunStatus = findViewById(R.id.huntrottlingstatus);

        currentlySetHun = findViewById(R.id.notification_currently_set);
        if (load("aa_hun_ms")) {
            messagesHunThrottling.setText(getString(R.string.reset_tweak) + getString(R.string.set_notification_duration_to) + getString(R.string.default_string));
            changeStatus(messagesHunStatus, 2, false);
            if (loadValue("messaging_hun_value") == 0) {
                try {
                    saveValue(Integer.parseInt(runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT DISTINCT intVal FROM Flags WHERE name=\"SystemUi__hun_default_heads_up_timeout_ms\";'").getInputStreamLog()), "messaging_hun_value");
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
            currentlySetHun.setText(getString(R.string.currently_set) + loadValue("messaging_hun_value"));
        } else {
            messagesHunThrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + "...");
            changeStatus(messagesHunStatus, 0, false);
        }



        messagesHunThrottling.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (hunSeekbar.getProgress() == 8000) {
                            if (load("aa_hun_ms")) {
                                revert("aa_hun_ms");
                                saveValue(0, "messaging_hun_value");
                                changeStatus(messagesHunStatus, 0, true);
                                currentlySetHun.setText("");
                                showRebootButton();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.choose_value_first), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            setHunDuration(view, hunSeekbar.getProgress());
                        }
                    }
                });

        setOnLongClickListener(messagesHunThrottling, R.string.tutorial_hun, R.drawable.tutorial_hun);

        mediathrottlingbutton = findViewById(R.id.media_throttling_button);
        final int[] secondScrollBarStatus = {0};
        final TextView secondDisplayValue = findViewById(R.id.second_seekbar_text);
        final SeekBar mediaSeekbar = findViewById(R.id.media_hun_seekbar);
        mediaSeekbar.setProgress(8000);
        secondDisplayValue.setText(mediaSeekbar.getProgress() + "ms");
        mediaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int) Math.round(progress / 1000)) * 1000;
                mediaSeekbar.setProgress(progress);
                secondDisplayValue.setText(mediaSeekbar.getProgress() + "ms");
                if (mediaSeekbar.getProgress() == 8000) {
                    mediathrottlingbutton.setText(getString(R.string.reset_tweak) + getString(R.string.media_notification_duration_to) + getString(R.string.default_string));
                } else {
                    mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to) + " " + mediaSeekbar.getProgress() + " ms");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                secondDisplayValue.setText(mediaSeekbar.getProgress() + "ms");
                mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to) + " " + mediaSeekbar.getProgress() + " ms");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                secondScrollBarStatus[0] = mediaSeekbar.getProgress();
                secondDisplayValue.setText(mediaSeekbar.getProgress() + "ms");
                if (mediaSeekbar.getProgress() == 8000) {
                    mediathrottlingbutton.setText(getString(R.string.reset_tweak) + getString(R.string.media_notification_duration_to) + getString(R.string.default_string));
                } else {
                    mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to) + " " + mediaSeekbar.getProgress() + " ms");
                }
            }
        });

        revertMediaNotificationDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaSeekbar.setProgress(8000);
            }
        });

        currentlySetMediaHun = findViewById(R.id.media_notification_currently_set);
        mediaHunStatus = findViewById(R.id.media_trhrottling_status);
        if (load("aa_media_hun")) {
            mediathrottlingbutton.setText(getString(R.string.reset_tweak) + getString(R.string.media_notification_duration_to) + getString(R.string.default_string));
            changeStatus(mediaHunStatus, 2, false);
            if (loadValue("media_hun_value") == 0) {
                try {
                    saveValue(Integer.parseInt(runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT DISTINCT intVal FROM Flags WHERE name=\"SystemUi__media_hun_in_rail_widget_timeout_ms\";'").getInputStreamLog()), "media_hun_value");
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
            currentlySetMediaHun.setText(getString(R.string.currently_set) + loadValue("media_hun_value"));
        } else {
            mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to) + "...");
            changeStatus(mediaHunStatus, 0, false);
        }

        mediathrottlingbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_media_hun")) {
                            if (mediaSeekbar.getProgress() == 8000) {
                                revert("aa_media_hun");
                                saveValue(0, "media_hun_value");
                                changeStatus(mediaHunStatus, 0, true);
                                currentlySetMediaHun.setText("");
                            } else {
                                setMediaHunDuration(view, mediaSeekbar.getProgress());
                            }
                            showRebootButton();
                        } else {
                            setMediaHunDuration(view, mediaSeekbar.getProgress());
                        }
                    }
                });

        setOnLongClickListener(mediathrottlingbutton, R.string.tutorial_media_hun, R.drawable.tutorial_media_hun);

        intertialScrollButton = findViewById(R.id.inertial_scroll_button);



        /*final int[] calendarSeekbarStatus = {0};
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

        revertCalendarDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarSeekbar.setProgress(1);
            }
        });*/


        intertialScrollStatus = findViewById(R.id.inertial_scroll_status);

        if (load("aa_inertial_scroll")) {
            changeStatus(intertialScrollStatus, 2, false);
            intertialScrollButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.inertial_scroll_tweak));
        } else {
            intertialScrollButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.inertial_scroll_tweak));
            changeStatus(intertialScrollStatus, 0, false);
        }

        intertialScrollButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                            if (load("aa_inertial_scroll")) {
                                revert("aa_inertial_scroll");
                                changeStatus(intertialScrollStatus, 0, true);
                                showRebootButton();
                            } else {
                            inertialScrollTweak();
                        }
                    }
                });

        setOnLongClickListener(intertialScrollButton, R.string.tutorial_inertial_scroll);

        bluetoothoff = findViewById(R.id.bluetooth_disable_button);
        btstatus = findViewById(R.id.bt_disable_status);
        if (load("bluetooth_pairing_off")) {
            bluetoothoff.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.bluetooth_auto_connect));
            changeStatus(btstatus, 2, false);
        } else {
            bluetoothoff.setText(getString(R.string.disable_tweak_string) + getString(R.string.bluetooth_auto_connect));
            changeStatus(btstatus, 0, false);

        }

        verticalBarStatus = findViewById(R.id.vertical_bar_tweak_status);

        verticalBarTweakButton = findViewById(R.id.vertical_bar_tweak);
        if (load("aa_vertical_bar")) {
            verticalBarTweakButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.vertical_bar_tweak));
            changeStatus(verticalBarStatus, 2, false);
        } else {
            verticalBarTweakButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.vertical_bar_tweak));
            changeStatus(verticalBarStatus, 0, false);
        }

        verticalBarTweakButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_vertical_bar")) {
                            revert("aa_vertical_bar");
                            changeStatus(verticalBarStatus, 0, true);
                            showRebootButton();
                        } else {
                            verticalBarTweak();
                        }
                    }
                });

        setOnLongClickListener(verticalBarTweakButton, R.string.tutorial_vertical_bar_tweak);

        bluetoothoff.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("bluetooth_pairing_off")) {
                            revert("bluetooth_pairing_off");
                            bluetoothoff.setText(getString(R.string.disable_tweak_string) + getString(R.string.bluetooth_auto_connect));
                            changeStatus(btstatus, 0, true);
                            showRebootButton();
                        } else {
                            forceNoBt();
                        }
                    }
                });

        setOnLongClickListener(bluetoothoff, R.string.tutorial_bluetooth);


        mdbutton = findViewById(R.id.multi_display_button);
        mdstatus = findViewById(R.id.multi_display_status);
        if (load("multi_display")) {
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
                        if (load("multi_display")) {
                            revert("multi_display");
                            mdbutton.setText(getString(R.string.enable_tweak_string) + getString(R.string.multi_display_string));
                            changeStatus(mdstatus, 0, true);
                            showRebootButton();
                        } else {
                            multiDisplay();
                        }
                    }
                });

        setOnLongClickListener(mdbutton, R.string.tutorial_multidisplay, R.drawable.tutorial_md1, R.drawable.tutorial_md2, R.drawable.tutorial_md3);

        batteryWarning = findViewById(R.id.battery_warning_button);
        batteryWarningStatus = findViewById(R.id.battery_warning_status);
        if (load("battery_saver_warning")) {
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
                        if (load("battery_saver_warning")) {
                            revert("battery_saver_warning");
                            batteryWarning.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_warning));
                            changeStatus(batteryWarningStatus, 0, true);
                            showRebootButton();
                        } else {
                            disableBatteryWarning();
                        }
                    }
                });

        setOnLongClickListener(batteryWarning, R.string.tutorial_battery_saver_warning, R.drawable.tutorial_battery_saver);


        disableTelemetryButton = findViewById(R.id.telemetry_disable_tweak);
        telemetryStatus = findViewById(R.id.telemetry_disable_status);
        if (load("kill_telemetry")) {
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
                        if (load("kill_telemetry")) {
                            revert("kill_telemetry");
                            disableTelemetryButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.telemetry_string));
                            changeStatus(telemetryStatus, 0, true);
                            showRebootButton();
                        } else {
                            disableTelemetry();

                        }
                    }
                });

        setOnLongClickListener(disableTelemetryButton, R.string.tutorial_telemetry);

        tweakUSBBitrateButton = findViewById(R.id.tweak_bitrate_usb);
        final int[] usbBitrateValue = {0};
        final TextView currentSeekbarUSB = findViewById(R.id.usb_bitrate_currently_set);
        final TextView toBeSetSeekbarUSB = findViewById(R.id.usb_bitrate_to_be_set);
        final SeekBar usbBitrateSeekbar = findViewById(R.id.usb_bitrate_seekbar);
        final Double[] valueUSB = new Double[1];
        usbBitrateSeekbar.setProgress(10);
        toBeSetSeekbarUSB.setText("1.0" + "X");
        usbBitrateSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueUSB[0] = (Double.valueOf(progress) / 10.0);
                toBeSetSeekbarUSB.setText(valueUSB[0] + "X");
                if (usbBitrateSeekbar.getProgress() == 10) {
                    tweakUSBBitrateButton.setText(getString(R.string.reset_tweak) + getString(R.string.set_usb_bitrate) + " " + getString(R.string.default_string));
                    toBeSetSeekbarUSB.setText(valueUSB[0] + "X");
                } else {
                    tweakUSBBitrateButton.setText(getString(R.string.set_value) + getString(R.string.set_usb_bitrate) + " " + valueUSB[0] + " X");
                    toBeSetSeekbarUSB.setText(valueUSB[0] + "X");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (usbBitrateSeekbar.getProgress() == 10) {
                    tweakUSBBitrateButton.setText(getString(R.string.reset_tweak) + getString(R.string.set_usb_bitrate) + " " + getString(R.string.default_string));
                    toBeSetSeekbarUSB.setText(valueUSB[0] + "X");
                } else {
                    tweakUSBBitrateButton.setText(getString(R.string.set_value) + getString(R.string.set_usb_bitrate) + " " + valueUSB[0] + " X");
                    toBeSetSeekbarUSB.setText(valueUSB[0] + "X");
                }
            }
        });

        revertUsbBitrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usbBitrateSeekbar.setProgress(10);
            }
        });


        usbBitrateStatus = findViewById(R.id.tweak_bitrate_usb_status);

        currentlySetUSBSeekbar = findViewById(R.id.usb_bitrate_currently_set);
        if (load("aa_bitrate_usb")) {
            tweakUSBBitrateButton.setText(getString(R.string.reset_tweak) + getString(R.string.set_usb_bitrate) + " " + getString(R.string.default_string));
            changeStatus(usbBitrateStatus, 2, false);
            currentlySetUSBSeekbar.setText(getString(R.string.currently_set) + loadFloat("usb_bitrate_value"));
        } else {
            tweakUSBBitrateButton.setText(getString(R.string.set_value) + getString(R.string.set_usb_bitrate) + "...");
            changeStatus(usbBitrateStatus, 0, false);
        }

        tweakUSBBitrateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (usbBitrateSeekbar.getProgress() == 10) {
                            if (load("aa_bitrate_usb")) {
                                revert("aa_bitrate_usb");
                                saveFloat(0, "usb_bitrate_value");
                                changeStatus(usbBitrateStatus, 0, true);
                                currentlySetUSBSeekbar.setText("");
                                showRebootButton();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.choose_value_first), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            setUSBbitrate(valueUSB[0]);

                        }
                    }
                });

        setOnLongClickListener(tweakUSBBitrateButton, R.string.tutorial_bitrate);




        tweakWiFiBitrateButton = findViewById(R.id.tweak_bitrate_wifi);
        final int[] wifiBitrateValue = {0};
        final TextView currentSeekbarWiFi = findViewById(R.id.wifi_bitrate_currently_set);
        final TextView toBeSetSeekbarWiFi = findViewById(R.id.wifi_bitrate_to_be_set);
        final SeekBar WiFiBitrateSeekbar = findViewById(R.id.wifi_bitrate_seekbar);
        final Double[] valueWiFi = new Double[1];
        WiFiBitrateSeekbar.setProgress(10);
        toBeSetSeekbarWiFi.setText("1.0" + "X");
        WiFiBitrateSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueWiFi[0] = (Double.valueOf(progress) / 10.0);
                toBeSetSeekbarWiFi.setText(valueWiFi[0] + "X");
                if (WiFiBitrateSeekbar.getProgress() == 10) {
                    tweakWiFiBitrateButton.setText(getString(R.string.reset_tweak) + getString(R.string.set_wifi_tweak) + " " + getString(R.string.default_string));
                } else {
                    tweakWiFiBitrateButton.setText(getString(R.string.set_value) + getString(R.string.set_wifi_tweak) + " " + valueWiFi[0] + " X");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (WiFiBitrateSeekbar.getProgress() == 10) {
                    tweakWiFiBitrateButton.setText(getString(R.string.reset_tweak) + getString(R.string.set_wifi_tweak) + " " + getString(R.string.default_string));
                } else {
                    tweakWiFiBitrateButton.setText(getString(R.string.set_value) + getString(R.string.set_wifi_tweak) + " " + valueWiFi[0] + " X");
                }
            }
        });

        setOnLongClickListener(tweakWiFiBitrateButton, R.string.tutorial_bitrate);

        revertWifiBitrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WiFiBitrateSeekbar.setProgress(10);
            }
        });


        wifiBitrateStatus = findViewById(R.id.tweak_bitrate_wifi_status);
        currentlySetWiFiSeekbar = findViewById(R.id.wifi_bitrate_currently_set);
        if (load("aa_bitrate_wireless")) {
            tweakWiFiBitrateButton.setText(getString(R.string.reset_tweak) + getString(R.string.set_wifi_tweak) + " " + getString(R.string.default_string));
            changeStatus(wifiBitrateStatus, 2, false);
            currentlySetWiFiSeekbar.setText(getString(R.string.currently_set) + loadFloat("wifi_bitrate_value"));
        } else {
            tweakWiFiBitrateButton.setText(getString(R.string.set_value) + getString(R.string.set_wifi_tweak) + "...");
            changeStatus(wifiBitrateStatus, 0, false);
        }

        tweakWiFiBitrateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (WiFiBitrateSeekbar.getProgress() == 10) {
                            if (load("aa_bitrate_wireless")) {
                                revert("aa_bitrate_wireless");
                                saveFloat(0, "wifi_bitrate_value");
                                changeStatus(wifiBitrateStatus, 0, true);
                                currentlySetWiFiSeekbar.setText("");
                                showRebootButton();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.choose_value_first), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            setWiFiBitrate(valueWiFi[0]);

                        }
                    }
                });



        newSeekbarTweakButton = findViewById(R.id.new_seekbar_tweak_button);
        newSeekbarTweakStatus = findViewById(R.id.newseekbar_tweak_status);


        if (load("aa_new_seekbar")) {
            newSeekbarTweakButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.tappable_progress));
            changeStatus(newSeekbarTweakStatus, 2, false);
        } else {
            newSeekbarTweakButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.tappable_progress));
            changeStatus(newSeekbarTweakStatus, 0, false);
        }

        newSeekbarTweakButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_new_seekbar")) {
                            revert("aa_new_seekbar");
                            newSeekbarTweakButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.tappable_progress));
                            changeStatus(newSeekbarTweakStatus, 0, true);
                            showRebootButton();
                        } else {
                            newSeekbar();
                        }
                    }
                });

        setOnLongClickListener(newSeekbarTweakButton, R.string.tutorial_new_seekbar);

        coolwalkTweak = findViewById(R.id.coolwalk_tweak_button);
        coolwalkTweakStatus = findViewById(R.id.coolwalk_tweak_status);
        nocoolwalkTweak = findViewById(R.id.nocoolwalk_tweak_button);
        nocoolwalkTweakStatus = findViewById(R.id.nocoolwalk_tweak_status);

        if (load("aa_activate_coolwalk")) {
            coolwalkTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.coolwalk_tweak));
            changeStatus(coolwalkTweakStatus, 2, false);
        } else {
            coolwalkTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.coolwalk_tweak));
            changeStatus(coolwalkTweakStatus, 0, false);
        }

        coolwalkTweak.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                            if (load ("aa_activate_coolwalk")) {

                                coolwalkTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.coolwalk_tweak));
                                changeStatus(coolwalkTweakStatus, 0, true);
                                revert("aa_activate_coolwalk");
                            } else {
                                if (load ("aa_deactivate_coolwalk")) {
                                    revert("aa_deactivate_coolwalk");
                                    nocoolwalkTweak.setText(getString(R.string.force_disable_tweak) + getString(R.string.coolwalk_tweak));
                                    changeStatus(nocoolwalkTweakStatus,0,false);
                                }
                                activateCoolwalk();
                            }

                    }
                });

        setOnLongClickListener(coolwalkTweak, R.string.tutorial_coolwalk, R.drawable.cw5, R.drawable.tutorial_coolwalk_1, R.drawable.tutorial_coolwalk_3);




        if (load("aa_deactivate_coolwalk")) {
            nocoolwalkTweak.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.coolwalk_tweak));
            changeStatus(nocoolwalkTweakStatus, 2, false);
        } else {
            nocoolwalkTweak.setText(getString(R.string.force_disable_tweak) + getString(R.string.coolwalk_tweak));
            changeStatus(nocoolwalkTweakStatus, 0, false);
        }

        nocoolwalkTweak.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (load ("aa_deactivate_coolwalk")) {

                            nocoolwalkTweak.setText(getString(R.string.force_disable_tweak) + getString(R.string.coolwalk_tweak));
                            changeStatus(nocoolwalkTweakStatus, 2, true);
                            revert("aa_deactivate_coolwalk");
                        } else {
                            if (load ("aa_activate_coolwalk")) {
                                revert("aa_activate_coolwalk");
                                coolwalkTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.coolwalk_tweak));
                                changeStatus(coolwalkTweakStatus,0,false);
                            }
                            deactivateCoolwalk();
                        }

                    }
                });

        setOnLongClickListener(nocoolwalkTweak, R.string.tutorial_nocoolwalk);


        assistantTipsButton = findViewById(R.id.assistanttips_tweak_button);
        assistantTipsTweakStatus = findViewById(R.id.assistanttips_tweak_status);


        if (load("aa_activate_assistant_tips")) {
            assistantTipsButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.assistant_tips));
            changeStatus(assistantTipsTweakStatus, 2, false);
        } else {
            assistantTipsButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.assistant_tips));
            changeStatus(assistantTipsTweakStatus, 0, false);
        }

        assistantTipsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_activate_assistant_tips")) {
                            revert("aa_activate_assistant_tips");
                            assistantTipsButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.assistant_tips));
                            changeStatus(assistantTipsTweakStatus, 0, true);
                            showRebootButton();
                        } else {
                            activateAssistantTips();
                        }
                    }
                });

        setOnLongClickListener(assistantTipsButton, R.string.tutorial_assistant_suggestions);

        declineSmsTweak = findViewById(R.id.declinesms_tweak_button);
        declineSmsTweakStatus = findViewById(R.id.declinesms_tweak_status);


        if (load("aa_activate_declinesms")) {
            declineSmsTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.decline_message_tweak));
            changeStatus(declineSmsTweakStatus, 2, false);
        } else {
            declineSmsTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.decline_message_tweak));
            changeStatus(declineSmsTweakStatus, 0, false);
        }

        declineSmsTweak.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_activate_declinesms")) {
                            revert("aa_activate_declinesms");
                            declineSmsTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.decline_message_tweak));
                            changeStatus(declineSmsTweakStatus, 0, true);
                            showRebootButton();
                        } else {
                            activatesmsdecline();
                        }
                    }
                });

        setOnLongClickListener(declineSmsTweak, R.string.tutorial_decline_message);




        deleteCarMode = findViewById(R.id.car_remover);
        deleteCarMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CarRemover.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });

        deleteCarMode.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);

                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_carremover));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, WRAP_CONTENT);

                return true;
            }
        });

    }



    private void setOnLongClickListener(final Button button, final int... p) {
        button.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);

                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(p[0]));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);

                if (p.length>1) {
                    try {
                        img1.setImageDrawable(getDrawable(p[1]));
                    } catch (Exception e) {
                        tutorial.setText(getString(p[0]) + getString(p[1]));
                        e.printStackTrace();
                    }
                }

                ImageView img2 = view.findViewById(R.id.tutorialimage2);
                if (p.length>2) {
                    img2.setImageDrawable(getDrawable(p[2]));
                }

                ImageView img3 = view.findViewById(R.id.tutorialimage3);
                if (p.length>3) {
                    img3.setImageDrawable(getDrawable(p[3]));
                }

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, WRAP_CONTENT);

                return true;
            }
        });
    }




    private void revert(final String toRevert) {

        final TextView logs = initiateLogsText();


        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;

                save(false, toRevert);

                appendText(logs, "\n\n-- Reverting the hack  --");
                if (PhenotypeDb.isNewSchema(path)) {
                    // flag_overrides also holds Play Services' own overrides: delete only this tweak's flags.
                    appendText(logs, runSuRaw(PhenotypeDb.revertCommand(path, toRevert)).getStreamLogsWithLabels());
                } else {
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DROP TRIGGER IF EXISTS " + toRevert + "; DELETE FROM FlagOverrides;'\n"
                    ).getStreamLogsWithLabels());
                }
                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());
            }


        }.start();


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.version);
        item.setTitle("V." + BuildConfig.VERSION_NAME);
        return super.onPrepareOptionsMenu(menu);
    }

    public WindowManager.LayoutParams setDialogLayoutParams(Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WRAP_CONTENT;
        return lp;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copy:

                final String title = "log";
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                final URL[] string = {null};
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final ClipboardManager clipboard = (ClipboardManager)
                                    getSystemService(Context.CLIPBOARD_SERVICE);
                            TextView textView = findViewById(R.id.logs);
                            URL newstring = Pastebin.pastePaste(BuildConfig.PASTEBIN_API_KEY, String.valueOf(textView.getText()), title);
                            Toast.makeText(getApplicationContext(), getString(R.string.copied_pastebin), Toast.LENGTH_LONG).show();
                            ClipData clip = ClipData.newPlainText("logs", newstring.toString());
                            clipboard.setPrimaryClip(clip);
                        } catch (PasteException e) {
                            e.printStackTrace();
                            final ClipboardManager clipboard = (ClipboardManager)
                                    getSystemService(Context.CLIPBOARD_SERVICE);
                            TextView textView = findViewById(R.id.logs);
                            Toast.makeText(getApplicationContext(), getString(R.string.log_copied), Toast.LENGTH_LONG).show();
                            ClipData clip = ClipData.newPlainText("logs", textView.getText());
                            clipboard.setPrimaryClip(clip);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                            final ClipboardManager clipboard = (ClipboardManager)
                                    getSystemService(Context.CLIPBOARD_SERVICE);

                            Toast.makeText(getApplicationContext(), getString(R.string.log_copied), Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), getString(R.string.log_copied), Toast.LENGTH_LONG).show();
                            TextView textView = findViewById(R.id.logs);

                            ClipData clip = ClipData.newPlainText("logs", textView.getText());
                            clipboard.setPrimaryClip(clip);
                        }
                    }
                });




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

    public void saveValue(final int value, String key) throws RuntimeException {
        SharedPreferences sharedPreferences = getPreferences(getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void saveFloat(final float value, String key) {
        SharedPreferences sharedPreferences = getPreferences(getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
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

    public float loadFloat(String key) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void patchforapps() {

        if (temp) {
            return;
        }


        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);


        SharedPreferences appsListPref = getApplicationContext().getSharedPreferences("appsListPref", 0);
        Map<String, ?> allEntries = appsListPref.getAll();
        logs.append("--  Apps which will be added to whitelist: --\n");
        String whiteListString = "";
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            logs.append("\t\t- " + entry.getValue() + " (" + entry.getKey() + ")\n");
            whiteListString += "," + entry.getKey();

            String pathResult = runSuWithCmd("pm path " + entry.getKey()).getInputStreamLogWithLabel();
            String actualPath = pathResult.substring(pathResult.lastIndexOf(":") + 1);

            appendText(logs , runSuWithCmd("mv " + actualPath + " /data/local/tmp/tmpapk" + entry.getKey() + ".apk").getStreamLogsWithLabels());
            appendText(logs , runSuWithCmd("pm uninstall " + entry.getKey()).getStreamLogsWithLabels());
            appendText(logs, runSuWithCmd("pm install -t -i \"com.android.vending\" -r" + " /data/local/tmp/tmpapk" + entry.getKey() + ".apk" ).getStreamLogsWithLabels());


        }

        whiteListString = whiteListString.replaceFirst(",", "");
        final String whiteListStringFinal = whiteListString;
        final StringBuilder finalCommand = new StringBuilder();



        finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, stringVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"app_white_list\", \"\",\"");
            finalCommand.append(whiteListStringFinal);
            finalCommand.append("\",0);");
            finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, stringVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"car_connect_broadcast_whitelist\", \"\",\"");
        finalCommand.append(whiteListStringFinal);
        finalCommand.append("\",0);");
        finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, stringVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"AppValidation__allowed_package_list\",  \"\" ,\"\",0);");
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, stringVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"AppValidation__blocked_packages_by_installer\", \"\" ,\"\",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"AppValidation__should_bypass_validation\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"AppValidation__play_install_api\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"AppValidation__swallow_play_api_exception\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"AppValidation__swallow_play_api_exception_return_value\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"should_bypass_validation\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarProjectionValidator__filter_disabled_packages_in_ispackageallowed_method\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"UnknownSources__allow_full_screen_apps\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("DELETE FROM Flags WHERE name=\"app_black_list\";");
        finalCommand.append("DELETE FROM Flags WHERE name=\"app_white_list\";");
        finalCommand.append(System.getProperty("line.separator"));




        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_patched_apps;\n DROP TRIGGER IF EXISTS after_delete;\n" +
                                "DROP TRIGGER IF EXISTS aa_patched_apps_fix;" +
                                finalCommand + "'").getStreamLogsWithLabels());

                try {
                    this.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (suitableMethodFound) {


                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_patched_apps AFTER DELETE\n" +
                                    "On FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_patched_apps\";'").getInputStreamLog().length() <= 4) {
                        suitableMethodFound = false;
                    } else {
                        appendText(logs, "\n--  end SQL method   --");
                        save(true, "aa_patched_apps");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                changeStatus(patchappstatus, 1, true);
                                showRebootButton();
                                patchapps.setText(getString(R.string.unpatch) + getString(R.string.patch_custom_apps));
                            }
                        });
                    }
                }
                dialog.dismiss();
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "custom_apps");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();
    }

    private String gainOwnership(final TextView logs) {
        appendText(logs, "\n\n--  Gaining ownership of the database   --");
        appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

        String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
        appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
        appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());
        return currentPolicy;
    }


    public void messageAutoRead() {
        final TextView logs = initiateLogsText();


        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Messaging__voice_messages_read_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Messaging__direct_reply_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Messaging__autoplay_messages_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);





                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_message_autoread;\n" + finalCommand + "'").getStreamLogsWithLabels());

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_message_autoread AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_message_autoread\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_message_autoread");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(messageAutoReadStatus, 1, true);
                            showRebootButton();
                            messageAutoReadTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.message_auto_read));
                        }
                    });
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_message_autoread");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();
    }

    public void patchforspeed(int usercount) {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarSensorParameters__max_parked_speed_gps_sensor\",\"\" ,999,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",\"\" ,999,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"VisualPreview__unchained\",\"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"VisualPreview__chained\",\"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"VisualPreview__unchained_experiment_id\",\"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, extensionVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"GearSnacks__parked_gears\",\"\" ,\"999\",0);");
            finalCommand.append(System.getProperty("line.separator"));

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);




                if (suitableMethodFound) {


                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DROP TRIGGER IF EXISTS aa_speed_hack;\n" + finalCommand + "'").getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_speed_hack AFTER DELETE\n" +
                                    "On FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_speed_hack\";'").getInputStreamLog().length() <= 4) {
                        suitableMethodFound = false;
                    } else {
                        appendText(logs, "\n--  end SQL method   --");
                        save(true, "aa_speed_hack");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                changeStatus(noSpeedRestrictionsStatus, 1, true);
                                showRebootButton();
                                nospeed.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
                            }
                        });
                    }
                }
                dialog.dismiss();
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_speed_hack");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();
    }

    public void multiDisplay() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"MultiDisplay__enabled\",\"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"MultiDisplay__clustersim_enabled\",\"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"MultiDisplay__gal_munger_enabled\",\"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"MultiDisplay__cluster_launcher_enabled\",\"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"MultiDisplay__cluster_launcher_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"MultiDisplay__aux_display_default_configuration\",\"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);




                appendText(logs, "\n\n--  run SQL method   --");

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS multi_display;" + finalCommand + "'"
                ).getStreamLogsWithLabels());




                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER multi_display AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"multi_display\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "multi_display");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(mdstatus, 1, true);
                            showRebootButton();
                            mdbutton.setText(getString(R.string.disable_tweak_string) + getString(R.string.multi_display_string));
                        }
                    });
                }


                dialog.dismiss();
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "multi_display");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();
    }

    public void patchfortouchlimit() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);


        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",\"\" ,999,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ContentBrowse__enable_speed_bump_projected\",\"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ContentBrowse__keyboard_force_disabled\",\"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Dialer__speedbump_enabled\",\"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Mesquite__speedbump_enabled\",\"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ContentBrowse__speedbump_force_enabled\",\"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"McFly__speedbump_enabled\",\"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Media__projected_speedbump_enabled\",\"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Watevra__speedbump_enabled\",\"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Watevra__speedbump_map_interactivity_events_enabled\",\"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Watevra__speedbump_non_scroll_events_enabled\",\"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ContentBrowse__sixtap_force_enabled\",\"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ContentBrowse__permits_chart\",\"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ContentBrowse__use_updated_list_view_kill_switch\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"TouchpadUiNavigation__multimove_penalty_mm\",\"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Watevra__speedbump_max_list_size\",\"\" ,400,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Watevra__max_list_size\",\"\" ,400,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Watevra__speedbump_max_grid_list_size\",\"\" ,300,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Watevra__max_grid_list_size\",\"\" ,300,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Watevra__speedbump_map_interactivity_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__max_list_size_with_speedbump\",\"\" ,300,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__add_default_screen_size_value_kill_switch\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__allow_long_text_while_parked_kill_switch\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__allow_secondary_actions_in_half_lists\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__cluster_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__list_template_fab_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__grid_template_fab_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__tab_template_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__task_limit_restrictions_allows_overflow\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"AppQualityTester__developer_setting_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Assistant__transcription_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__app_driven_refresh_enabled\",\"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__app_driven_refresh_enabled_for_undefined_category\",\"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__allow_secondary_actions_in_half_lists\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));




        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);


                appendText(logs, "\n\n-- Run SQL Commands  --");
                {


                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'DROP TRIGGER IF EXISTS aa_six_tap;\n" + finalCommand + "DELETE FROM Flags WHERE name LIKE \"%speedbump%\";'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_six_tap AFTER DELETE\n" +
                                    "On FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_six_tap\";'").getInputStreamLog().length() <= 4) {
                        suitableMethodFound = false;
                    } else {
                        appendText(logs, "\n--  end SQL method   --");
                        save(true, "aa_six_tap");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                changeStatus(taplimitstatus, 1, true);
                                showRebootButton();
                                taplimitat.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.disable_speed_limitations));
                            }
                        });
                    }
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_six_tap");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();
    }

    public void coolwalkdaynightpatch() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\", 0,\"Coolwalk__day_night_theme_enabled\", \"\" ,1,0);");
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\", 0,\"Coolwalk__enable_palette_swap_by_broadcast\", \"\" ,1,0);");

        finalCommand.append(System.getProperty("line.separator"));


        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);



                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS coolwalk_daynight_tweak;\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER coolwalk_daynight_tweak AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"coolwalk_daynight_tweak\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "coolwalk_daynight_tweak");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(navstatus, 1, true);
                            showRebootButton();
                            coolwalkDayNightTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.coolwalk_daynight_tweak));
                        }
                    });
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "coolwalk_daynight_tweak");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();


    }




    public void disableBatteryWarning() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"BatterySaver__warning_enabled\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);



                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS battery_saver_warning;\n" +
                                finalCommand + "'"
                ).getStreamLogsWithLabels());

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER battery_saver_warning AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"battery_saver_warning\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "battery_saver_warning");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(batteryWarningStatus, 1, true);
                            showRebootButton();
                            batteryWarning.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_warning));
                        }
                    });
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "battery_saver_warning");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();


    }

    public void battOutline() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"BatterySaver__icon_outline_enabled\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);



                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_battery_outline;\n\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_battery_outline AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_battery_outline\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_battery_outline");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(batteryOutlineStatus, 1, true);
                            showRebootButton();
                            batteryoutline.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_outline_string));
                        }
                    });
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_battery_outline");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();


    }


    public void activateCoolwalk() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Assistant__coolwalk_suggestions_grpc_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__media_rec_card_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__opt_in _default\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__rail_dock_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__rail_dock_four_app_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__rail_widget_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__allow_focus_input\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__assistant_media_rec_shortcut_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__assistant_suggestions_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__canonical_vertical_rail_default\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__choose_assistant_suggestion_over_app_suggestion\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Media__coolwalk_playback_gradient_scrim_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Media__favorites_button_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__streamed_media_recommendations_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Media__foreground_search_fab_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__a4c_suggestions_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__rotary_proximity_navigation\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__semi_wide_vertical_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__short_canonical_vertical_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__three_actions_hun_ui_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__indicate_severe_thermal_status\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__focus_check_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__fix_status_bar_highlight_ghosting_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__use_widescreen_crossfade\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__dashboard_placement_customization_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__media_notification_high_priority_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__launcher_settings_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Weather__enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Weather__icon_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Weather__preinstalled_frx_toggle_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Boardwalk__news_browser_available\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__car_ui_entry_use_configuration_context_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__media_switcher_page_while_started_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__projection_notification_hun_sbn_converter_hack_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_media_rec_enabled_on_focus_screens\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__wallpaper_backdrop_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"AppListUi__use_updated_calendar_ui\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__is_toggle_allowed_in_map_and_pane_templates_kill_switch\",\"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__messaging_aap_host_logic_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__tab_template_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CompanionDeviceManager__integration_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__allow_all_inputs_kill_switch\",\"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Media__favorites_button_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Media__show_album_art_for_suggestion\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Media__show_settings_button_in_browse_view\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, extensionVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Media__tint_resource_uris\",\"\" ,\"\",0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"CarAppLibrary__radio_buttons_ui_changes_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__improve_startup\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Media__custom_action_assert_connection_kill_switch\",\"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__rail_widget_user_education_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__navigation_signal_to_assistant_enabled\",\"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));



        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);


                if (load ("aa_activate_coolwalk")) {
                    coolwalkTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.coolwalk_tweak));
                    changeStatus(coolwalkTweakStatus, 0, true);
                }

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_activate_coolwalk;\n DROP TRIGGER IF EXISTS aa_deactivate_coolwalk;\n" +
                                finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_activate_coolwalk AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_activate_coolwalk\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_activate_coolwalk");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(coolwalkTweakStatus, 1, true);
                            showRebootButton();
                            coolwalkTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.coolwalk_tweak));
                        }
                    });
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_activate_coolwalk");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void deactivateCoolwalk() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Assistant__coolwalk_suggestions_grpc_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__fishfood_nag_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__media_rec_card_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__opt_in _default\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__rail_dock_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__rail_dock_four_app_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__rail_widget_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__allow_focus_input\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__assistant_media_rec_shortcut_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__assistant_suggestions_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__canonical_vertical_rail_default\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__canonical_vertical_rail_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__choose_assistant_suggestion_over_app_suggestion\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Media__coolwalk_playback_gradient_scrim_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Media__favorites_button_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__streamed_media_recommendations_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__a4c_suggestions_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__rotary_proximity_navigation\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__semi_wide_vertical_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__short_canonical_vertical_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__three_actions_hun_ui_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__indicate_severe_thermal_status\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__use_widescreen_crossfade\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__dashboard_placement_customization_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__media_notification_high_priority_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__add_boardwalk_theme_attrs_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Coolwalk__choreograph_start_composition_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Coolwalk__rail_hotseat_check_app_available_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));






        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);




                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_deactivate_coolwalk;\nDROP TRIGGER IF EXISTS aa_activate_coolwalk;\n" +
                                finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_deactivate_coolwalk AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_deactivate_coolwalk\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_deactivate_coolwalk");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(nocoolwalkTweakStatus, 1, true);
                            showRebootButton();
                            nocoolwalkTweak.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.coolwalk_tweak));
                        }
                    });
                }

                appendText(logs, "\n\n--  restoring Google Play Services   --");
                appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());


                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_deactivate_coolwalk");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void activateMaterialYou() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__material_you_settings_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));


        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);



                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_material_you;\n" +
                                finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_material_you AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_material_you\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_material_you");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(materialYouTweakStatus, 1, true);
                            showRebootButton();
                            materialYouButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.materialyou_tweak));
                        }
                    });
                }

                appendText(logs, "\n\n--  restoring Google Play Services   --");
                appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());


                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_material_you");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void activateAssistantTips() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"LauncherShortcuts__assistant_shortcut_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"LauncherApps__clean_up_cujs_kill_switch\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);



                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_activate_assistant_tips;\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_activate_assistant_tips AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_activate_assistant_tips\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_activate_assistant_tips");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(assistantTipsTweakStatus, 1, true);
                            showRebootButton();
                            assistantTipsButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.assistant_tips));
                        }
                    });
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_activate_assistant_tips");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void activatesmsdecline() {

        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Messaging__decline_call_message_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Messaging__template_ui_enabled\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);



                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_activate_declinesms;\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_activate_declinesms AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_activate_declinesms\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_activate_declinesms");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(declineSmsTweakStatus, 1, true);
                            showRebootButton();
                            declineSmsTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.decline_message_tweak));
                        }
                    });
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_activate_declinesms");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void newSeekbar() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Media__tappable_progress_bar_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);



                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_new_seekbar;\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_new_seekbar AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_new_seekbar\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_new_seekbar");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(newSeekbarTweakStatus, 1, true);
                            showRebootButton();
                            newSeekbarTweakButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.tappable_progress));
                        }
                    });
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_new_sekbar");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    @NonNull
    private TextView initiateLogsText() {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        return logs;
    }



    public void forceNoBt() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"BluetoothPairing__car_bluetooth_service_disable\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"BluetoothPairing__car_bluetooth_service_skip_pairing\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"BluetoothPairing__car_bluetooth_service_disable\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"BluetoothPairing__connect_bluetooth_timeout\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));


        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);



                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS bluetooth_pairing_off;\n" +
                                finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER bluetooth_pairing_off AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"bluetooth_pairing_off\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "bluetooth_pairing_off");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(btstatus, 1, true);
                            showRebootButton();
                            bluetoothoff.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.bluetooth_auto_connect));
                        }
                    });
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "bluetooth_pairing_off");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void disableTelemetry() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"CarEventLoggerRefactorFeature__convert_car_setup_analytics_telemetry\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"CarServiceTelemetry__enabled\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"CarServiceTelemetry__is_wifi_kbps_logging_enabled\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"CarServiceTelemetry__log_battery_temperature\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"CarServiceTelemetry__wifi_latency_log_frequency_ms\", \"\" ,99999999,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"ConnectivityLogging__heartbeat_interval_ms\", \"\" ,99999999,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"TelemetryDriveIdFeature__enable_log_event_validation\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"TelemetryDriveIdFeature__enabled\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"UsbStatusLoggingFeature__monitor_usb_ping_telemetry_enabled\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"TelemetryDriveIdForGearheadFeature__enable_frx_setup_logging_via_gearhead\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"AudioStatsLoggingFeature__audio_stats_logging_period_milliseconds\", \"\" ,99999999,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"FrameworkMediaStatsLoggingFeature__is_media_stats_queue_time_logging_enabled\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ConnectivityLogging__num_background_threads\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ConnectivityLogging__include_extra_events\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ConnectivityLogging__enable_heartbeat\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"WifiChannelLogging__enabled\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ConnectivityLogging__session_info_dump_size\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"BluetoothMetadataLogger__enabled\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"CarEventLoggerRefactorFeature__convert_car_analytics_telemetry\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Bugfix__sensitive_permissions_extra_logging\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ConnectivityLogging__log_bluetooth_rssi\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ConnectivityLogging__save_log_when_usb_starts\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ConnectivityLogging__skip_retroactive_usb_logging\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"InternetConnectivityLogging__enabled\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Telemetry__local_logging\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"WirelessProjectionInGearhead__wireless_wifi_additional_start_logging\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Dialer__r_telemetry_enabled\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"AssistantSilenceDiagnostics__enabled\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"TelemetryDriveIdForGearheadFeature__enable_continuous_telemetry_binding\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"TelemetryDriveIdForGearheadFeature__enable_telemetry_impl_conversion\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ConnectivityLogging__long_session_timeout_ms\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ConnectivityLogging__short_session_timeout_ms\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"ConnectivityLogging__session_timeout_ms\", \"\" ,1000,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolval, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"PhenotypeCache__load_snapshot\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolval, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"PhenotypeCache__save_snapshot\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolval, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"PhenotypeCache__use_snapshot\", \"\" ,0,0);");
            finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolval, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Performance__log_to_telemetry\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolval, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"Performance__primes_network_metrics_enabled_kill_switch\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__use_realtime_if_invalid\", \"\" ,1,0);");
            finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Performance__primes_logging_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Telemetry__westworld_logging_enabled_kill_switch\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"enable_blueooth_fsm_telemetry\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"Performance__use_optimized_car_activities\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"Messaging__assistant_notification_data_sharing_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarProjectionValidator__measure_latency_enabled\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"PhenotypeProcessStableFlags__first_read_latency\", \"\" ,0,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"PhenotypeProcessStableFlags__legacy_flag_infrastructure_enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);



                appendText(logs, "\n\n-- Run SQL Commands  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS kill_telemetry;" +
                                "DELETE FROM Flags WHERE name LIKE \"%telemetry%\" AND packageName=\"com.google.android.projection.gearhead\";\n" +
                                "DELETE FROM Flags WHERE name LIKE \"%telemetry%\" AND packageName=\"com.google.android.gms.car\";" +
                                finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER kill_telemetry AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"kill_telemetry\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "kill_telemetry");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(telemetryStatus, 1, true);
                            showRebootButton();
                            disableTelemetryButton.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.telemetry_string));
                        }
                    });
                }

                dialog.dismiss();
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "kill_telemetry");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void uxprototypeTweak(URL URL) {
        final TextView logs = initiateLogsText();


        final StringBuilder finalCommand = new StringBuilder();



        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"UxPrototype__enabled\", \"\" ,1,0);");
        finalCommand.append(System.getProperty("line.separator"));
        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, stringVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"UxPrototype__url\", \"\" ,\""+ URL +"\",0);");
        finalCommand.append(System.getProperty("line.separator"));



        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);



                appendText(logs, "\n\n-- Run SQL Commands  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS uxprototype_tweak;\n" +
                                finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER uxprototype_tweak AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"uxprototype_tweak\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "uxprototype_tweak");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(uxprototypeTweakStatus, 1, true);
                            showRebootButton();
                            disableTelemetryButton.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.uxprototype_tweak));
                        }
                    });
                }

                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "uxprototype_tweak");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void setHunDuration(View view, final int value) {
        final TextView logs = initiateLogsText();


        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__hun_default_heads_up_timeout_ms\", \"\"," + value + ",0);");
            finalCommand.append(System.getProperty("line.separator"));

        runOnUiThread(new Thread() {
            @Override
            public void run() {

                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);



                appendText(logs, "\n\n-- Run SQL Commands  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_hun_ms;\n" + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_hun_ms AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_hun_ms\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_hun_ms");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(messagesHunStatus, 1, true);
                            showRebootButton();
                            saveValue(value, "messaging_hun_value");
                            currentlySetHun.setText(getString(R.string.currently_set) + value);
                        }
                    });
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_hun_ms");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });

    }

    public void setMediaHunDuration(View view, final int value) {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__media_hun_in_rail_widget_timeout_ms\", \"\"," + value + ",0);");
            finalCommand.append(System.getProperty("line.separator"));


        runOnUiThread(new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);




                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_media_hun;\n" + finalCommand + "'"
                ).getStreamLogsWithLabels());

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_media_hun AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_media_hun\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_media_hun");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(mediaHunStatus, 1, true);
                            showRebootButton();
                            saveValue(value, "media_hun_value");
                            currentlySetMediaHun.setText(getString(R.string.currently_set) + value);
                        }
                    });
                }

                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }

                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_media_hun");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });
        dialog.dismiss();
    }

    public void setUSBbitrate(final double value) {
        final TextView logs = initiateLogsText();


        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"VideoEncoderParamsFeature__bitrate_1080p_usb\", \"\"," + String.format("%.0f", 16000000 * value) + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"VideoEncoderParamsFeature__bitrate_1080p_usb_hevc\", \"\"," + String.format("%.0f", 3000000 * value) + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"VideoEncoderParamsFeature__bitrate_480p_usb\", \"\"," + String.format("%.0f", 8000000 * value) + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"VideoEncoderParamsFeature__bitrate_480p_usb_hevc\", \"\"," + String.format("%.0f", 1000000 * value) + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"VideoEncoderParamsFeature__bitrate_720p_usb\", \"\"," + String.format("%.0f", 12000000 * value) + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"VideoEncoderParamsFeature__bitrate_720p_usb_hevc\", \"\"," + String.format("%.0f", 2000000 * value) + ",0);");
            finalCommand.append(System.getProperty("line.separator"));

        runOnUiThread(new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);




                appendText(logs, "\n\n-- Run SQL Commands  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_bitrate_usb;\n DELETE FROM Flags WHERE name LIKE \"VideoEncoderParamsFeature%\";" +
                                finalCommand + "'"
                ).getStreamLogsWithLabels());



                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_bitrate_usb AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_bitrate_usb\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_bitrate_usb");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(usbBitrateStatus, 1, true);
                            showRebootButton();
                            saveFloat((float) value, "usb_bitrate_value");
                            currentlySetUSBSeekbar.setText(getString(R.string.currently_set) + value);
                        }
                    });
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_bitrate_usb");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });

    }

    public void setWiFiBitrate(final double value) {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"VideoEncoderParamsFeature__bitrate_1080p_wireless\", \"\"," + String.format("%.0f", 16000000 * value) + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"VideoEncoderParamsFeature__bitrate_1080p_wireless_hevc\", \"\"," + String.format("%.0f", 3000000 * value) + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"VideoEncoderParamsFeature__bitrate_480p_wireless\", \"\"," + String.format("%.0f", 8000000 * value) + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"VideoEncoderParamsFeature__bitrate_480p_wireless_hevc\", \"\"," + String.format("%.0f", 1000000 * value) + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"VideoEncoderParamsFeature__bitrate_720p_wireless\", \"\"," + String.format("%.0f", 12000000 * value) + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",  0,\"VideoEncoderParamsFeature__bitrate_720p_wireless_hevc\", \"\"," + String.format("%.0f", 2000000 * value) + ",0);");
            finalCommand.append(System.getProperty("line.separator"));

        runOnUiThread(new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);




                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_bitrate_wireless;\n DELETE FROM Flags WHERE name LIKE \"VideoEncoderParamsFeature%\";\n" + finalCommand + "'"
                ).getStreamLogsWithLabels());

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_bitrate_wireless AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_bitrate_wireless\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_bitrate_wireless");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(wifiBitrateStatus, 1, true);
                            showRebootButton();
                            saveFloat((float) value, "wifi_bitrate_value");
                            currentlySetWiFiSeekbar.setText(getString(R.string.currently_set) + value);
                        }
                    });

                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_bitrate_wireless");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });

    }

    private void inertialScrollTweak() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__inertial_scrolling_enabled\", \"\",1,0);");
            finalCommand.append(System.getProperty("line.separator"));

        runOnUiThread(new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_inertial_scroll;\n"  + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_inertial_scroll AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());


                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_inertial_scroll\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_inertial_scroll");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(intertialScrollStatus, 1, true);
                            showRebootButton();
                            intertialScrollButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.inertial_scroll_tweak));
                        }
                    });
                }
                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_inertial_scroll");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });

    }


    private void verticalBarTweak() {
        final TextView logs = initiateLogsText();

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();


        finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__horizontal_rail_canonical_breakpoint_dp\", \"\",40,0);");
        finalCommand.append(System.getProperty("line.separator"));

        runOnUiThread(new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_vertical_bar;\n"  + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_vertical_bar AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());


                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_vertical_bar\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_vertical_bar");
                    verticalBarTweakButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.vertical_bar_tweak));
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(verticalBarStatus, 1, true);
                            showRebootButton();
                        }
                    });
                }

                appendText(logs, "\n\n--  restoring Google Play Services   --");
                appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());


                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_vertical_bar");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });

    }

    public void forceWideScreen(View view, final int value) {
        final TextView logs = initiateLogsText();
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);
        final StringBuilder finalCommand = new StringBuilder();



        if (value == 10) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__short_portrait_breakpoint_dp\", \"\"," + value + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__portrait_breakpoint_dp\", \"\"," + value + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__widescreen_breakpoint_dp\", \"\"," + 3000 + ",0);");
            finalCommand.append(System.getProperty("line.separator"));

        }

        if (value == 470) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__widescreen_breakpoint_dp\", \"\"," + value + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__rail_assistant_media_rec_enabled_min_screen_width\", \"\"," + value + ",0);");
            finalCommand.append(System.getProperty("line.separator"));
        }

            if (value == 1920) {

                finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__regular_layout_max_width_dp\", \"\"," + 1919 + ",0);");
                finalCommand.append(System.getProperty("line.separator"));
                finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__semi_widescreen_breakpoint_dp\", \"\"," + value + ",0);");
                finalCommand.append(System.getProperty("line.separator"));
                finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__widescreen_breakpoint_dp\", \"\"," + 2000 + ",0);");
                finalCommand.append(System.getProperty("line.separator"));
                finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__short_portrait_breakpoint_dp\", \"\"," + value + ",0);");
                finalCommand.append(System.getProperty("line.separator"));
                finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName,  flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",  0,\"SystemUi__portrait_breakpoint_dp\", \"\"," + value + ",0);");
                finalCommand.append(System.getProperty("line.separator"));
            }
        

        runOnUiThread(new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                killps(logs);
                String currentPolicy = gainOwnership(logs);
                String decideWhat = new String();




                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'DROP TRIGGER IF EXISTS force_ws;\n DROP TRIGGER IF EXISTS force_no_ws;\n" + finalCommand + "'").getStreamLogsWithLabels());

                switch (value) {
                    case 470: {
                        decideWhat = "force_ws";
                        break;
                    }
                    case 1920: {
                        decideWhat = "force_no_ws";
                        break;
                    }
                    case 10: {
                        decideWhat = "force_portrait";
                        break;
                    }
                }
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER " + decideWhat + " AFTER DELETE\n" +
                                "On FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"" + decideWhat + "\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    switch (value) {
                        case 470: {
                            forceNoWideScreen.setText(getString(R.string.force_disable_tweak) + getString(R.string.base_no_ws));
                            forcePortrait.setText(getString(R.string.enable_tweak_string) + getString(R.string.portrait_layout));
                            changeStatus(forceWideScreenStatus, 1, true);
                            changeStatus(forceNoWideScreenStatus, 0, false);
                            changeStatus(forcePortraitStatus, 0, false);
                            showRebootButton();
                            break;
                        }
                        case 1920: {
                            forceWideScreenButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.base_no_ws));
                            forcePortrait.setText(getString(R.string.enable_tweak_string) + getString(R.string.portrait_layout));
                            changeStatus(forceNoWideScreenStatus, 1, true);
                            changeStatus(forceWideScreenStatus, 0, false);
                            changeStatus(forcePortraitStatus, 0, false);
                            showRebootButton();
                            break;
                        }
                        case 10: {
                            forceNoWideScreen.setText(getString(R.string.force_disable_tweak) + getString(R.string.base_no_ws));
                            forceWideScreenButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.base_no_ws));
                            changeStatus(forcePortraitStatus, 1, true);
                            changeStatus(forceNoWideScreenStatus, 0, false);
                            changeStatus(forceWideScreenStatus, 0, false);
                            showRebootButton();
                            break;
                        }
                    }
                    save(true, decideWhat);
                }

                
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", decideWhat);
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });

    }

    private void killps(final TextView logs) {
        appendText(logs, "\n\n--  Force stopping Google Play Services   --");
        appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
    }


    public static StreamLogs runSuWithCmd(String cmd) {
        return runSuRaw(PhenotypeDb.rewriteCommand(cmd));
    }

    /** Runs {@code cmd} as root without translating phenotype.db SQL for the new schema. */
    public static StreamLogs runSuRaw(String cmd) {
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        InputStream errorStream = null;

        StreamLogs streamLogs = new StreamLogs();
        streamLogs.setOutputStreamLog(cmd);

        try {
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
        } catch (IOException e) {
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


    private void appendText(final TextView textView, final String s) {
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
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"FlagOverrides\";" +
                                "SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\";" +

                                "SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\" AND name=\"after_delete\";" +
                                "SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\" AND name=\"aa_patched_apps\";'").getInputStreamLog();
                String[] lines = get_names.split(System.getProperty("line.separator"));
                for (int i = 0; i < lines.length; i++) {
                    save(true, lines[i]);
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
                if (PhenotypeDb.isNewSchema(path)) {
                    // Dropping/recreating the legacy table would only leave an orphan Play Services ignores:
                    // revert each of our triggers and delete just their overrides.
                    List<String> triggers = PhenotypeDb.ourTriggers(path);
                    appendText(log, "\n" + triggers);
                    if (!triggers.isEmpty()) {
                        appendText(log, runSuRaw(PhenotypeDb.command(PhenotypeDb.sqlite3Path(path), PhenotypeDb.revertSql(triggers))).getStreamLogsWithLabels());
                    }
                    for (String trigger : triggers) {
                        save(false, trigger);
                    }
                    appendText(log, runSuWithCmd(PhenotypeDb.restoreOwnershipCommand()).getStreamLogsWithLabels());
                    dialog.dismiss();
                    return;
                }
                allTriggerString[0] = path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'";
                String get_names = runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\";'").getInputStreamLog();
                appendText(log, get_names);
                String[] lines = get_names.split(System.getProperty("line.separator"));
                final StringBuilder finalCommand = new StringBuilder();
                appendText(log, runSuWithCmd(path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'DROP TABLE FlagOverrides;'").getOutputStreamLog());
                appendText(log, runSuWithCmd(path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'DELETE FROM Flags WHERE name=\"com.google.android.projection.gearhead\";'").getOutputStreamLog());
                appendText(log, runSuWithCmd(path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'DELETE FROM Flags WHERE name=\"com.google.android.gms.car\";'").getOutputStreamLog());

                for (int i = 0; i < lines.length; i++) {
                    finalCommand.append("DROP TRIGGER IF EXISTS " + lines[i] + ";");
                    finalCommand.append("\n");
                }

                for (int i = 0; i < lines.length; i++) {
                    appendText(log, runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'" + finalCommand + "'").getOutputStreamLog());
                }
                appendText(log, runSuWithCmd(path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'CREATE TABLE FlagOverrides (packageName TEXT NOT NULL, user TEXT NOT NULL, name TEXT NOT NULL, flagType INTEGER NOT NULL, intVal INTEGER, boolVal INTEGER, floatVal REAL, stringVal TEXT, extensionVal BLOB, committed, PRIMARY KEY(packageName, user, name, committed));'").getOutputStreamLog());
                dialog.dismiss();
            }

        }.start();

        return;
    }

    public void showRebootButton() {
        runOnUiThread(new Thread() {
            @Override
            public void run() {
                final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.reboot_button_anim);

                if (!animationRun) {
                    rebootButton.setVisibility(View.VISIBLE);
                    rebootButton.startAnimation(anim);
                    animationRun = true;
                }
            }
        });

    }

    public static void openApp(Context context, String packageName) {
        if (isAppInstalled(context, packageName))
            if (isAppEnabled(context, packageName)) {
                PackageManager pm = context.getPackageManager();
                Intent launchIntent = new Intent("com.google.android.projection.gearhead.SETTINGS");
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
            } else
                Toast.makeText(context, context.getString(R.string.not_enabled_warning), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, context.getString(R.string.not_installed_warning), Toast.LENGTH_SHORT).show();
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

    public static String replaceAll(StringBuilder builder, String from, String to) {
        Pattern p = Pattern.compile(from);
        Matcher m = p.matcher(builder);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, to);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private void changeStatus(ImageView resource, int status, boolean doAnimation) {
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
