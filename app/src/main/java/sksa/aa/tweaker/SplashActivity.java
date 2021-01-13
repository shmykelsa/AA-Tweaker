package sksa.aa.tweaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static sksa.aa.tweaker.MainActivity.runSuWithCmd;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Intent intent = new Intent(this, MainActivity.class);
        final NoRootDialog noRootDialog = new NoRootDialog();
        final StreamLogs isDeviceRooted =  runSuWithCmd("echo 1");
        copyAssets();
        SharedPreferences sharedPreferences = getSharedPreferences("MainActivity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("aa_speed_hack", false);
        editor.putBoolean("assist_short", false);
        editor.putBoolean("aa_six_tap", false);
        editor.putBoolean("aa_startup_policy", false);
        editor.putBoolean("aa_patched_apps", false);
        editor.putBoolean("aa_assistant_rail", false);
        editor.putBoolean("aa_battery_outline", false);
        editor.putBoolean("aa_sb_opaque", false);
        editor.putBoolean("force_ws", false);
        editor.putBoolean("force_no_ws", false);
        editor.putBoolean("aa_hun_ms", false);
        editor.putBoolean("aa_media_hun", false);
        editor.putBoolean("bluetooth_pairing_off", false);
        editor.putBoolean("multi_display", false);
        editor.putBoolean("battery_saver_warning", false);
        editor.putBoolean("aa_wallpapers", false);
        editor.putBoolean("aa_night_mode_revert", false);
        editor.putBoolean("kill_telemetry", false);
        editor.commit();


        final Button continueButton = findViewById(R.id.proceed_button);
        continueButton.setEnabled(false);
        new CountDownTimer(5000, 10) {
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) ( 1 + (millisUntilFinished/1000));
                continueButton.setText(getString(R.string.proceed) + " (" + secondsRemaining + ")");
            }

            @Override
            public void onFinish() {
                continueButton.setEnabled(true);
                continueButton.setText(R.string.proceed);
            }
        }.start();

        continueButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isDeviceRooted.getInputStreamLog().equals("1")) {
                            startActivity(intent);
                            finish();
                        } else {
                            noRootDialog.show(getSupportFragmentManager(), "NoRootDialog");
                        }
                    }
                });
    }


    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private void copyAssets() {
        String path = getApplicationInfo().dataDir;
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


}