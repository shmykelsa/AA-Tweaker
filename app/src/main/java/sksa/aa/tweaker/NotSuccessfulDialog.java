package sksa.aa.tweaker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.heinrichreimersoftware.androidissuereporter.IssueReporterLauncher;

public class NotSuccessfulDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.warning_title));
        builder.setMessage(R.string.error_generic);
        builder.setNeutralButton( getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton( R.string.open_issue_button
                ,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        IssueReporterLauncher.forTarget("shmykelsa", "AA-tweaker")
                                // [Recommended] Theme to use for the reporter.
                                // (See #theming for further information.)
                                .theme(R.style.IssueTheme)
                                // [Optional] Auth token to open issues if users don't have a GitHub account
                                // You can register a bot account on GitHub and copy ist OAuth2 token here.
                                // (See #how-to-create-a-bot-key for further information.)
                                // [Optional] Include other relevant info in the bug report (like custom variables)
                                .putExtraInfo("Log", getArguments().getString("log"))
                                .putExtraInfo("Tweak Applied", getArguments().getString("tweak"))
                                // [Optional] Disable back arrow in toolbar
                                .homeAsUpEnabled(true)
                                .launch(getActivity());

                    }
                });
        return builder.create();
    }
}