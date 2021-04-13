package sksa.aa.tweaker.CarRemoverActivity;

public class CarInfo {
    private String name;

    public CarInfo(String str, boolean b) {
        this.name = str;
        this.isChecked = b;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    private boolean isChecked;

    public CarInfo(String name, boolean isChecked, String id) {
        this.name = name;
        this.isChecked = isChecked;
        this.id = id;
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
