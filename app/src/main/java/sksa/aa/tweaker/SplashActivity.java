package sksa.aa.tweaker;

import android.app.Activity;
import android.content.Intent;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent intent = new Intent(this, MainActivity.class);

        final NoRootDialog noRootDialog = new NoRootDialog();

        MainActivity rootChecker = new MainActivity();
        final StreamLogs isDeviceRooted =  rootChecker.runSuWithCmd("echo 1");


        Button continueButton = findViewById(R.id.proceed_button);
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




}