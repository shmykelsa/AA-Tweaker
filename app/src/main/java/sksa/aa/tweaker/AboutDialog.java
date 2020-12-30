package sksa.aa.tweaker;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

    Context context = MainActivity.getContext();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage(Html.fromHtml(getString(R.string.about_part_one) +
                getString(R.string.about_part_two)));
        builder.setCancelable(true);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNeutralButton(R.string.translators_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(Html.fromHtml("\uD83C\uDDF7\uD83C\uDDFA Diversant96<br>" +
                        "\uD83C\uDDF3\uD83C\uDDF1 Coyenzo<br>" +
                        "\uD83C\uDDF8\uD83C\uDDF0 Jozo19<br>" +
                        "\uD83C\uDDEA\uD83C\uDDF8 Krilok<br>" +
                        "\uD83C\uDDE8\uD83C\uDDFF Martin2412<br>" +
                        "\uD83C\uDDF8\uD83C\uDDEE Brubblu<br>" +
                        "\uD83C\uDDEB\uD83C\uDDF7 Nova.kin<br>" +
                        "\uD83C\uDDEE\uD83C\uDDF9 Shmykelsa<br>" +
                        "\uD83C\uDDE7\uD83C\uDDF7 Gsproenca<br>" +
                        "\uD83C\uDDF9\uD83C\uDDF7 Uaslan<br>" +
                        "\uD83C\uDDE9\uD83C\uDDEA Lassmiranda<br>" +
                        "<br>Interested in translating AA AIO TWEAKER in your own language? Join translations on <a href=\"https://crowdin.com/project/aa-aio-tweaker\">Crowdin</a>!"));
                builder.setCancelable(true);
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog Alert1 = builder.create();
                Alert1.show();
                ((TextView) Alert1.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

            }
        });
        builder.setNegativeButton("Bitcoin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", "39bdKem8taTZvm2WeyH8wwDhYKzZ2PzhGn");
                clipboard.setPrimaryClip(clip);
                Toast mytoast = Toast.makeText(getContext(), R.string.copied_address_toast, Toast.LENGTH_LONG);
                mytoast.show();
            }
        });
        AlertDialog Alert = builder.create();
        Alert.show();
        ((TextView)Alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        return Alert;
    }

    private void startBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://crowdin.com/project/aa-aio-tweaker"));
        browserIntent.putExtra("Activity", "AboutDialog");
        startActivity(browserIntent);
    }



}