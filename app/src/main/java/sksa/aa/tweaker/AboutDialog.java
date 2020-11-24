package sksa.aa.tweaker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

public class AboutDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Html.fromHtml("This app has been developed by Shmykelsa<br> The source code is available on GitHub<br><br>If you wish " +
                "to make a donation, you can use PayPal"));
        builder.setCancelable(true);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNeutralButton("Source Code", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/shmykelsa/AA-Tweaker"));
                startActivity(browserIntent);
            }
        });
        builder.setNegativeButton("Donation", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://paypal.me/grizzo96"));
                startActivity(browserIntent);
            }
        });
        AlertDialog Alert = builder.create();
        Alert.show();
        ((TextView) Alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        return builder.create();
    }
}