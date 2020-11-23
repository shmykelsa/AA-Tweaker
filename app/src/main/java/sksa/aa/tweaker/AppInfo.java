package sksa.aa.tweaker;

import java.util.ArrayList;

public class AppInfo implements Comparable<AppInfo> {
    private String name;
    private String packageName;
    private boolean isChecked;

    public AppInfo(String name, String packageName, boolean isChecked) {
        this.name = name;
        this.packageName = packageName;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked (boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public int compareTo(AppInfo o) {
        ArrayList<String> aAutoAppsList = new ArrayList<>();
        aAutoAppsList.add("com.github.slashmax.aamirror");
        aAutoAppsList.add("com.github.slashmax.aamirror_plus");
        aAutoAppsList.add("com.google.android.kk");
        aAutoAppsList.add("com.google.android.kk2");
        aAutoAppsList.add("com.mqbcoding.stats");
        aAutoAppsList.add("org.openauto.aautolauncher");
        aAutoAppsList.add("ru.inceptive.screentwoauto");
        int comp1;
        int comp2;
        int result;

        comp1 = o.getIsChecked() ? 1 : 0;
        comp2 = this.getIsChecked() ? 1 : 0;

        result = comp1 - comp2;

        if (result != 0) {
            return result;
        }

        comp1 = aAutoAppsList.contains(o.getPackageName()) ? 1 : 0;
        comp2 = aAutoAppsList.contains(this.getPackageName()) ? 1 : 0;

        result = comp1 - comp2;

        if (result != 0) {
            return result;
        }

        return this.getName().compareTo(o.getName());
    }
}
