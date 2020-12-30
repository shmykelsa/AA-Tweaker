package sksa.aa.tweaker;

import android.app.Dialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    public static String appDirectory = new String();

    private static Context mContext;

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
                        "'SELECT COUNT(DISTINCT user) FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\";'").getInputStreamLog();
        final int UserCount = Integer.parseInt(CountUsers);
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
        final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.reboot_button_anim);
        final RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(400);
        rotate.setInterpolator(new LinearInterpolator());

        final Boolean[] animationRun = {false};

        final Button nospeed = findViewById(R.id.nospeed);
        final ImageView nospeedimg = findViewById(R.id.speedhackstatus);
        if(load("aa_speed_hack")) {
            nospeed.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
            nospeedimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            nospeedimg.setColorFilter(Color.argb(255,0,255,0));
        } else {
            nospeed.setText(getString(R.string.disable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
            nospeedimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            nospeedimg.setColorFilter(Color.argb(255,255,0,0));
        }

        nospeed.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_speed_hack")){
                            revert("aa_speed_hack");
                            nospeed.setText(getString(R.string.disable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
                            nospeedimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            nospeedimg.setColorFilter(Color.argb(255,255,0,0));
                            nospeedimg.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchforspeed(view, UserCount);
                            nospeed.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
                            nospeedimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            nospeedimg.setColorFilter(Color.argb(255,255,255,0));
                            nospeedimg.startAnimation(rotate);
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

        final Button assistshort = findViewById(R.id.assistshort);
        final ImageView assisthackimg = findViewById(R.id.shortcutstatus);
        if(load("assist_short")) {
            assistshort.setText(getString(R.string.disable_tweak_string) + getString(R.string.enable_assistant_shortcuts));
            assisthackimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            assisthackimg.setColorFilter(Color.argb(255,0,255,0));
        } else {
            assistshort.setText(getString(R.string.enable_tweak_string) + getString(R.string.enable_assistant_shortcuts));
            assisthackimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            assisthackimg.setColorFilter(Color.argb(255,255,0,0));
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
                            assisthackimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            assisthackimg.setColorFilter(Color.argb(255,255,0,0));
                            assisthackimg.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchforassistshort(view, UserCount);
                            assistshort.setText(getString(R.string.disable_tweak_string) + getString(R.string.enable_assistant_shortcuts));
                            assisthackimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            assisthackimg.setColorFilter(Color.argb(255,255,255,0));
                            assisthackimg.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button taplimitat = findViewById(R.id.taplimit);
        final ImageView taplimitstatus = findViewById(R.id.sixtapstatus);
        if(load("aa_six_tap")) {
            taplimitat.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.disable_speed_limitations));
            taplimitstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            taplimitstatus.setColorFilter(Color.argb(255,0,255,0));
        } else {
            taplimitat.setText(getString(R.string.disable_tweak_string) + getString(R.string.disable_speed_limitations));
            taplimitstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            taplimitstatus.setColorFilter(Color.argb(255,255,0,0));
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
                            taplimitstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            taplimitstatus.setColorFilter(Color.argb(255,255,0,0));
                            taplimitstatus.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchfortouchlimit(view, UserCount);
                            taplimitat.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.disable_speed_limitations));
                            taplimitstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            taplimitstatus.setColorFilter(Color.argb(255,255,255,0));
                            taplimitstatus.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button startupnav = findViewById(R.id.startup);
        final ImageView navstatus = findViewById(R.id.navstatus);
        if(load("aa_startup_policy")) {
            startupnav.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.navigation_at_start));
            navstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            navstatus.setColorFilter(Color.argb(255,0,255,0));
        } else {
            startupnav.setText(getString(R.string.disable_tweak_string) + getString(R.string.navigation_at_start));
            navstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            navstatus.setColorFilter(Color.argb(255,255,0,0));
        }
        startupnav.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_startup_policy")){
                            revert("aa_startup_policy");
                            revert("aa_startup_policy_cleanup");
                            startupnav.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.navigation_at_start));
                            navstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            navstatus.setColorFilter(Color.argb(255,255,0,0));
                            navstatus.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            navpatch(view, UserCount);
                            startupnav.setText(getString(R.string.disable_tweak_string) + getString(R.string.navigation_at_start));
                            navstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            navstatus.setColorFilter(Color.argb(255,255,255,0));
                            navstatus.startAnimation(rotate);
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

        final Button patchapps = findViewById(R.id.patchapps);
        final ImageView patchappstatus = findViewById(R.id.patchedappstatus);


        if(load("aa_patched_apps") || load("after_delete")) {
            patchapps.setText(getString(R.string.unpatch) + getString(R.string.patch_custom_apps));
            patchappstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            patchappstatus.setColorFilter(Color.argb(255,0,255,0));
        } else {
            patchapps.setText(getString(R.string.patch_app) + getString(R.string.patch_custom_apps));
            patchappstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            patchappstatus.setColorFilter(Color.argb(255,255,0,0));
        }

        patchapps.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_patched_apps")){
                            revert("aa_patched_apps");
                            patchapps.setText(getString(R.string.patch_app) + getString(R.string.patch_custom_apps));
                            patchappstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            patchappstatus.setColorFilter(Color.argb(255,255,0,0));
                            patchappstatus.startAnimation(rotate);
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
                                Toast.makeText(getApplicationContext(), "Choose apps to whitelist.", Toast.LENGTH_LONG).show();
                            } else {
                                patchforapps(view);
                                patchapps.setText(getString(R.string.unpatch) + getString(R.string.patch_custom_apps));
                                patchappstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                                patchappstatus.setColorFilter(Color.argb(255, 0, 255, 0));
                                patchappstatus.startAnimation(rotate);
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

        final Button assistanim = findViewById(R.id.assistanim);
        final ImageView assistanimstatus = findViewById(R.id.assistanimstatus);
        if(load("aa_assistant_rail")) {
            assistanim.setText(getString(R.string.disable_tweak_string) + getString(R.string.enable_assistant_animation_in_navbar));
            assistanimstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            assistanimstatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            assistanim.setText(getString(R.string.enable_tweak_string) + getString(R.string.enable_assistant_animation_in_navbar));
            assistanimstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            assistanimstatus.setColorFilter(Color.argb(255,255,0,0));
        }

        assistanim.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_assistant_rail")){
                            revert("aa_assistant_rail");
                            assistanim.setText(getString(R.string.enable_tweak_string) + getString(R.string.enable_assistant_animation_in_navbar));
                            assistanimstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            assistanimstatus.setColorFilter(Color.argb(255,255,0,0));
                            assistanimstatus.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchrailassistant(view, UserCount);
                            assistanim.setText(getString(R.string.disable_tweak_string) + getString(R.string.enable_assistant_animation_in_navbar));
                            assistanimstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            assistanimstatus.setColorFilter(Color.argb(255,255,255,0));
                            assistanimstatus.startAnimation(rotate);
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

        final Button batteryoutline = findViewById(R.id.battoutline);
        final ImageView batterystatus = findViewById(R.id.batterystatus);
        if(load("aa_battery_outline")) {
            batteryoutline.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_outline_string));
            batterystatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            batterystatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            batteryoutline.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_outline_string));
            batterystatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            batterystatus.setColorFilter(Color.argb(255,255,0,0));
        }

        batteryoutline.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_battery_outline")){
                            revert("aa_battery_outline");
                            batteryoutline.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_outline_string));
                            batterystatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            batterystatus.setColorFilter(Color.argb(255,255,0,0));
                            batterystatus.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            battOutline(view, UserCount);
                            batteryoutline.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_outline_string));
                            batterystatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            batterystatus.setColorFilter(Color.argb(255,255,255,0));
                            batterystatus.startAnimation(rotate);
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
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 500);

                return true;
            }
        });

        final Button statusbaropaque = findViewById(R.id.statusbar_opaque);
        final ImageView opauqestatus = findViewById(R.id.statusbar_opaque_status);
        if(load("aa_sb_opaque")) {
            statusbaropaque.setText(getString(R.string.disable_tweak_string) + getString(R.string.statb_opaque_string));
            opauqestatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            opauqestatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            statusbaropaque.setText(getString(R.string.enable_tweak_string) + getString(R.string.statb_opaque_string));
            opauqestatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            opauqestatus.setColorFilter(Color.argb(255,255,0,0));
        }

        statusbaropaque.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_sb_opaque")){
                            revert("aa_sb_opaque");
                            statusbaropaque.setText(getString(R.string.enable_tweak_string) + getString(R.string.statb_opaque_string));
                            opauqestatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            opauqestatus.setColorFilter(Color.argb(255,255,0,0));
                            opauqestatus.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            opaqueStatusBar(view, UserCount);
                            statusbaropaque.setText(getString(R.string.disable_tweak_string) + getString(R.string.statb_opaque_string));
                            opauqestatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            opauqestatus.setColorFilter(Color.argb(255,255,255,0));
                            opauqestatus.startAnimation(rotate);
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
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 500);

                return true;
            }
        });

        final Button forceNoWideScreen = findViewById(R.id.force__no_ws_button);
        final ImageView forceNoWideScreenStatus = findViewById(R.id.force_no_ws_status);

        final Button forceWideScreenButton = findViewById(R.id.force_ws_button);
        final ImageView forceWideScreenStatus = findViewById(R.id.force_ws_status);

        if(load("force_ws")) {
            forceWideScreenButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.force_widescreen_text));
            forceWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            forceWideScreenStatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            forceWideScreenButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.force_widescreen_text));
            forceWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            forceWideScreenStatus.setColorFilter(Color.argb(255,255,0,0));
        }

        forceWideScreenButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("force_ws")){
                            revert("force_ws");
                            forceWideScreenButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.force_widescreen_text));
                            forceWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            forceWideScreenStatus.setColorFilter(Color.argb(255,255,0,0));
                            forceWideScreenStatus.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            forceWideScreen(view, 470, UserCount);
                            forceWideScreenButton.setText(getString(R.string.disable_tweak_string)+ getString(R.string.force_widescreen_text));
                            forceWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            forceWideScreenStatus.setColorFilter(Color.argb(255,255,255,0));
                            forceWideScreenStatus.startAnimation(rotate);
                            save(true, "force_ws");
                            if (load("force_no_ws")) {
                                Toast.makeText(getApplicationContext(), R.string.force_disable_widescreen_warning, Toast.LENGTH_LONG).show();
                                save(false,"force_no_ws");
                                forceNoWideScreen.setText(getString(R.string.force_disable_tweak) + getString(R.string.base_no_ws));
                                forceNoWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                                forceNoWideScreenStatus.setColorFilter(Color.argb(255, 255, 0, 0));
                                forceNoWideScreenStatus.startAnimation(rotate);
                            }
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
            forceNoWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            forceNoWideScreenStatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            forceNoWideScreen.setText(getString(R.string.force_disable_tweak) + getString(R.string.base_no_ws));
            forceNoWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            forceNoWideScreenStatus.setColorFilter(Color.argb(255,255,0,0));
        }

        forceNoWideScreen.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("force_no_ws")){
                            revert("force_no_ws");
                            forceNoWideScreen.setText(getString(R.string.force_disable_tweak) + getString(R.string.base_no_ws));
                            forceNoWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            forceNoWideScreenStatus.setColorFilter(Color.argb(255,255,0,0));
                            forceNoWideScreenStatus.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            forceWideScreen(view, 3000, UserCount);
                            forceNoWideScreen.setText(getString(R.string.reset_tweak) + getString(R.string.base_no_ws));
                            forceNoWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            forceNoWideScreenStatus.setColorFilter(Color.argb(255,255,255,0));
                            forceNoWideScreenStatus.startAnimation(rotate);
                            save(true, "force_no_ws");
                            if (load ("force_ws")) {
                                save(false, "force_ws");
                                Toast.makeText(getApplicationContext(), R.string.force_widescreen_warning, Toast.LENGTH_LONG).show();
                                forceWideScreenButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.force_widescreen_text));
                                forceWideScreenStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                                forceWideScreenStatus.setColorFilter(Color.argb(255, 255, 0, 0));
                                forceWideScreenStatus.startAnimation(rotate);
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
                huntrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress()+ " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                huntrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress()+ " ms");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                scrollbarStatus[0] = hunSeekbar.getProgress();
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                huntrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress()+ " ms");
            }
        });


        final ImageView hunstatus = findViewById(R.id.huntrottlingstatus);
        if(load("aa_hun_ms")) {
            huntrottling.setText(getString(R.string.reset_tweak) + getString(R.string.set_notification_duration_to) + getString(R.string.default_string));
            hunstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            hunstatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            huntrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to));
            hunstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            hunstatus.setColorFilter(Color.argb(255,255,0,0));
        }

        huntrottling.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_hun_ms") && hunSeekbar.getProgress() == 8000){
                            revert("aa_hun_ms");
                            huntrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to));
                            hunstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            hunSeekbar.setProgress(8000);
                            hunstatus.setColorFilter(Color.argb(255,255,0,0));
                            hunstatus.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            setHunDuration(view, hunSeekbar.getProgress(), UserCount);
                            huntrottling.setText(getString(R.string.reset_tweak) + getString(R.string.set_notification_duration_to) + " default");
                            hunstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            hunstatus.setColorFilter(Color.argb(255,255,255,0));
                            hunstatus.startAnimation(rotate);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        huntrottling.setOnLongClickListener(new View.OnLongClickListener() {
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
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 500);

                return true;
            }
        });

        final Button mediathrottlingbutton = findViewById(R.id.media_throttling_button);
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
                mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to) + " " + mediaSeekbar.getProgress()+ " ms");
            }
        });


        final ImageView mediaHunStatus = findViewById(R.id.media_trhrottling_status);
        if(load("aa_media_hun")) {
            mediathrottlingbutton.setText(getString(R.string.reset_tweak) + getString(R.string.media_notification_duration_to) + getString(R.string.default_string));
            mediaHunStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            mediaHunStatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to));
            mediaHunStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            mediaHunStatus.setColorFilter(Color.argb(255,255,0,0));
        }

        mediathrottlingbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_media_hun") && mediaSeekbar.getProgress() == 8000){
                            revert("aa_media_hun");
                            mediaSeekbar.setProgress(8000);
                            mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to));
                            mediaHunStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            mediaHunStatus.setColorFilter(Color.argb(255,255,0,0));
                            mediaHunStatus.startAnimation(rotate);
//                            save(false, "aa_media_hun");
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            setMediaHunDuration(view, mediaSeekbar.getProgress(), UserCount);
                            mediathrottlingbutton.setText(getString(R.string.reset_tweak) + getString(R.string.media_notification_duration_to) + getString(R.string.default_string));
                            mediaHunStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            mediaHunStatus.setColorFilter(Color.argb(255,255,255,0));
                            mediaHunStatus.startAnimation(rotate);
                            //save(true, "aa_media_hun");
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
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 600);

                return true;
            }
        });

        final Button bluetoothoff = findViewById(R.id.bluetooth_disable_button);
        final ImageView btstatus = findViewById(R.id.bt_disable_status);
        if(load("bluetooth_pairing_off")) {
            bluetoothoff.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.bluetooth_auto_connect));
            btstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            btstatus.setColorFilter(Color.argb(255,0,255,0));
        } else {
            bluetoothoff.setText(getString(R.string.disable_tweak_string) + getString(R.string.bluetooth_auto_connect));
            btstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            btstatus.setColorFilter(Color.argb(255,255,0,0));
        }

        bluetoothoff.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("bluetooth_pairing_off")){
                            revert("bluetooth_pairing_off");
                            bluetoothoff.setText(getString(R.string.disable_tweak_string) + getString(R.string.bluetooth_auto_connect));
                            btstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            btstatus.setColorFilter(Color.argb(255,255,0,0));
                            btstatus.startAnimation(rotate);

                            //save(false, "bluetooth_pairing_off");
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            forceNoBt(view, UserCount);
                            bluetoothoff.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.bluetooth_auto_connect));
                            btstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            btstatus.setColorFilter(Color.argb(255,255,255,0));
                            btstatus.startAnimation(rotate);
                            //save(true, "bluetooth_pairing_off");
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
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 500);

                return true;
            }
        });

        final Button mdbutton = findViewById(R.id.multi_display_button);
        final ImageView mdstatus = findViewById(R.id.multi_display_status);
        if(load("multi_display")) {
            mdbutton.setText(getString(R.string.disable_tweak_string) + getString(R.string.multi_display_string));
            mdstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            mdstatus.setColorFilter(Color.argb(255,0,255,0));
        } else {
            mdbutton.setText(getString(R.string.enable_tweak_string) + getString(R.string.multi_display_string));
            mdstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            mdstatus.setColorFilter(Color.argb(255,255,0,0));
        }

        mdbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("multi_display")){
                            revert("multi_display");
                            mdbutton.setText(getString(R.string.enable_tweak_string) + getString(R.string.multi_display_string));
                            mdstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            mdstatus.setColorFilter(Color.argb(255,255,0,0));
                            mdstatus.startAnimation(rotate);

                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            multiDisplay(view, UserCount);
                            mdbutton.setText(getString(R.string.disable_tweak_string) + getString(R.string.multi_display_string));
                            mdstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            mdstatus.setColorFilter(Color.argb(255,255,255,0));
                            mdstatus.startAnimation(rotate);

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
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 600);
                return true;
            }
        });

        final Button batteryWarning = findViewById(R.id.battery_warning_button);
        final ImageView batteryWarningStatus = findViewById(R.id.battery_warning_status);
        if(load("battery_saver_warning")) {
            batteryWarning.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_warning));
            batteryWarningStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            batteryWarningStatus.setColorFilter(Color.argb(255,0,255,0));
        } else {
            batteryWarning.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_warning));
            batteryWarningStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            batteryWarningStatus.setColorFilter(Color.argb(255,255,0,0));
        }

        batteryWarning.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("battery_saver_warning")){
                            revert("battery_saver_warning");
                            batteryWarning.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_warning));
                            batteryWarningStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            batteryWarningStatus.setColorFilter(Color.argb(255,255,0,0));
                            batteryWarningStatus.startAnimation(rotate);

                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                rebootButton.startAnimation(anim);
                                animationRun[0] = true;
                            }
                        }
                        else {
                            disableBatteryWarning(view, UserCount);
                            batteryWarning.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_warning));
                            batteryWarningStatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            batteryWarningStatus.setColorFilter(Color.argb(255,255,255,0));
                            batteryWarningStatus.startAnimation(rotate);

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
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT , 500);
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
                final DialogFragment revertDialog = new RevertDialog();
                revertDialog.show(getSupportFragmentManager(), "RevertDialog");
                break;


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

            new Thread() {
                @Override
                public void run() {
                    String path = getApplicationInfo().dataDir;
                    boolean suitableMethodFound = true;
                    

                    appendText(logs, "\n\n-- Drop Triggers  --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DROP TRIGGER IF EXISTS aa_patched_apps;'"
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
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);'"
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
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "END;'\n"
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
                                        "END;'\n"
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
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);'"
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
                            save(true, "aa_patched_apps");

                        } else {
                            appendText(logs, "\n\n--  Check NOT OK.  --");
                            appendText(logs, "\n     Length before delete and after was not equal.");
                            appendText(logs, "\n        Before: " + checkStep1.getInputStreamLog().length());
                            appendText(logs, "\n        After:  " + checkStep3.getInputStreamLog().length());
                        }


                    }
                    // Check End
                }
            }.start();

    }

    public void patchforassistshort(final View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
                    } else {
                        suitableMethodFound = false;
                        appendText(logs, "\n\n--  Suitable method NOT found!  --");
                    }

                }
            }.start();
        }

    public void patchrailassistant(final View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
        

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,99999999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }



            }
        }.start();
    }

    public void multiDisplay(final View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__multi_region_new_widescreen_activities_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__require_bfr\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"EnhancedNavigationMetadata__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"EnhancedNavigationMetadata__verify_turn_side_when_disabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__clustersim_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__gal_munger_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__multi_region_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }



            }
        }.start();
    }

    public void patchfortouchlimit(final View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();
    }

    public void navpatch(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();


    }

    public void disableBatteryWarning(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());


        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__warning_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__on_at_start_warning_delay_ms\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__switched_on_warning_delay_ms\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();


    }

    public void battOutline(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());


        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();


    }

    public void opaqueStatusBar (View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Boardwalk__status_bar_force_opaque\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();

    }

    public void forceNoBt (View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"BluetoothPairing__car_bluetooth_service_disable\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"BluetoothPairing__car_bluetooth_service_skip_pairing\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();

    }

    public void setHunDuration (View view, final int value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__hun_default_heads_up_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();

    }

    public void setMediaHunDuration (View view, final int value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__media_hun_in_rail_widget_timeout_ms\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();

    }

    public void forceWideScreen (View view, final int value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__widescreen_breakpoint_dp\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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

    public void forceAssistantFocus (View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());


        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i<=(usercount-1) ; i ++) {
            finalCommand.append("'INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"AssistantUiProviderFeature__assistant_car_activity_steals_focus_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT ");
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
                                "'DROP TRIGGER IF EXISTS assistant_focus_force;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    finalCommand + "'\n"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER assistant_focus_force AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "assistant_focus_force");
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();

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
            }
        });

    }

    public static void getAndRemoveOptionsSelected() {
        final String[] allTriggerString = {new String()};

        new Thread() {
            @Override
            public void run() {
                String path = appDirectory;
                allTriggerString[0] = path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'";
                String get_names = runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"FlagOverrides\";" +
                                "SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\" AND name=\"after_delete\";" +
                                "SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\" AND name=\"aa_patched_apps\";'").getInputStreamLog();

                String[] lines = get_names.split(System.getProperty("line.separator"));
                for (int i = 0; i < lines.length; i++) {
                    runSuWithCmd(path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'DROP TRIGGER IF EXISTS \"" + lines[i] + "\";'");
                }
                runSuWithCmd(path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'DELETE * FROM FlagOverrides;'");
            }

        }.start();

        return;
    }

}
