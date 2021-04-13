package sksa.aa.tweaker.AccountsChooseActivity;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rm.rmswitch.RMSwitch;

import java.util.ArrayList;

import sksa.aa.tweaker.R;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.SampleViewHolder> {

    public ArrayList<AccountInfo> mAccountInfo;


    public class SampleViewHolder extends RecyclerView.ViewHolder {

        public RMSwitch mCheckBox;
        public TextView title;

        public SampleViewHolder(View pItem) {
            super(pItem);
            title = pItem.findViewById(R.id.app_name);
            mCheckBox = pItem.findViewById(R.id.checkbox_app);
        }
    }

    public AccountAdapter(ArrayList<AccountInfo> pAccountInfo){
        mAccountInfo = pAccountInfo;
    }

    @NonNull
    @Override
    public SampleViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.account_info_layout, viewGroup, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSaveAppsWhiteList(v, i);
                notifyItemChanged(i);
            }
        });

        SharedPreferences accountsPrefs = viewGroup.getContext().getSharedPreferences("accountsList", 0);
        SharedPreferences.Editor editor = accountsPrefs.edit();
        editor.clear();
        editor.apply();

        return new SampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SampleViewHolder viewHolder, int i) {
        final AccountInfo currentItem = mAccountInfo.get(i);
        viewHolder.title.setText(currentItem.getName());
    }

    public void onClickSaveAppsWhiteList (View v, int position) {
        SharedPreferences appsListPref = v.getContext().getSharedPreferences("accountsList", 0);
        SharedPreferences.Editor editor = appsListPref.edit();
        if (mAccountInfo.get(position).getIsChecked()) {
            editor.remove(String.valueOf(position));
            editor.apply();
            mAccountInfo.get(position).setChecked(false);
        } else {
            editor.putBoolean(String.valueOf(position), true);
            editor.commit();
            mAccountInfo.get(position).setChecked(true);
        }

    }

    @Override
    public int getItemCount() {
        return mAccountInfo.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public Boolean getChecked(int position) {
        return mAccountInfo.get(position).getIsChecked();
    }
}