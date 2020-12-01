package sksa.aa.tweaker;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import static android.content.Context.CLIPBOARD_SERVICE;


public class AboutDialog extends DialogFragment {

    Context context = MainActivity.getContext();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final TextView message = new TextView(getContext());

        builder.setMessage(Html.fromHtml("AA AIO TWEAKER is developed and maintained by Shmykelsa.<br><br>" +
                "Source code is available on <a href=\"https://github.com/shmykelsa/AA-Tweaker\">GitHub</a><br><br>" +
                "If you really like my work, please consider a donation.<br> You can donate me through <a href=\"http://www.paypal.me/grizzo96\">PayPal</a> or you can copy my" +
                " Bitcoin address with the button down below.<br><br> Even a small amount will be much appreciated :)"));
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
        builder.setNegativeButton("Bitcoin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", "39bdKem8taTZvm2WeyH8wwDhYKzZ2PzhGn");
                clipboard.setPrimaryClip(clip);
                Toast mytoast = Toast.makeText(getContext(), "BTC Address copied to clipboard", Toast.LENGTH_LONG);
                mytoast.show();
            }
        });
        AlertDialog Alert = builder.create();
        Alert.show();
        ((TextView)Alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        return Alert;
    }

}