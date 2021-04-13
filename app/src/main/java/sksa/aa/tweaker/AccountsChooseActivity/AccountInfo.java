package sksa.aa.tweaker.AccountsChooseActivity;

import java.util.ArrayList;

public class AccountInfo {
    private String name;

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private boolean isChecked;

    public AccountInfo(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean getIsChecked() {
        return isChecked;
    }


}
