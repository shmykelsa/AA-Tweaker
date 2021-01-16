package sksa.aa.tweaker;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
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
                builder.setMessage(Html.fromHtml("Russian: Diversant96<br>" +
                        "Dutch: Coyenzo<br>" +
                        "Slovak: Jozo19<br>" +
                        "Spanish: Krilok<br>" +
                        "Czech: Martin2412<br>" +
                        "Slovenian: Brubblu<br>" +
                        "French: Nova.kin<br>" +
                        "Italian: Shmykelsa<br>" +
                        "Brazilian Portuguese: Gsproenca<br>" +
                        "Vietnamese: Quang.chk1<br>" +
                        "German: Lassmiranda<br>" +
                        "Korean: Mabig<br>" +
                        "Catalan: rogerpi95<br>" +
                        "Polish: Nor7ovich, MarcinzSowie<br>" +
                        "<br>Interested in translating AA AIO TWEAKER in your own language? Join translations on <a href=\"https://crowdin.com/project/aa-aio-tweaker\">Crowdin</a>!"));
                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.positive_button), new DialogInterface.OnClickListener() {
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


}