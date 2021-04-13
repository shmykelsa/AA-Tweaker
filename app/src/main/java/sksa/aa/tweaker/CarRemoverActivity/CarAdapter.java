package sksa.aa.tweaker.CarRemoverActivity;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rm.rmswitch.RMSwitch;

import java.util.ArrayList;

import sksa.aa.tweaker.R;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.SampleViewHolder> {

    public ArrayList<CarInfo> mCarInfo;


    public class SampleViewHolder extends RecyclerView.ViewHolder {

        public RMSwitch mCheckBox;
        public TextView title;

        public SampleViewHolder(View pItem) {
            super(pItem);
            title = pItem.findViewById(R.id.app_name);
            mCheckBox = pItem.findViewById(R.id.checkbox_app);
        }
    }

    public CarAdapter(ArrayList<CarInfo> pCarInfo){
        mCarInfo = pCarInfo;
    }

    @NonNull
    @Override
    public SampleViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.account_info_layout, viewGroup, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCarInfo.get(i).getIsChecked()) {
                    mCarInfo.get(i).setChecked(false);
                } else {
                    mCarInfo.get(i).setChecked(true);
                }
            }
        });

        return new SampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SampleViewHolder viewHolder, int i) {
        final CarInfo currentItem = mCarInfo.get(i);
        viewHolder.title.setText(currentItem.getName());
    }


    @Override
    public int getItemCount() {
        return mCarInfo.size();
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
        return mCarInfo.get(position).getIsChecked();
    }

    public void onClickSaveAppsWhiteList (View v, int position) {
        SharedPreferences appsListPref = v.getContext().getSharedPreferences("idList", 0);
        SharedPreferences.Editor editor = appsListPref.edit();
        if (mCarInfo.get(position).getIsChecked()) {
            editor.remove(mCarInfo.get(position).getId());
            editor.apply();
            mCarInfo.get(position).setChecked(false);
        } else {
            editor.putBoolean(mCarInfo.get(position).getId(), true);
            editor.commit();
            mCarInfo.get(position).setChecked(true);
        }

    }


    public String getId(int position) {
        return mCarInfo.get(position).getId();
    }


}