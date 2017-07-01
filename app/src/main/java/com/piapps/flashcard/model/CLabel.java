package com.piapps.flashcard.model;

/**
 * Created by abduaziz on 2/18/17.
 */

public class CLabel {

    String title;
    int icon;
    boolean isChecked = false;

    public CLabel(String title, int icon, boolean isChecked) {
        this.title = title;
        this.icon = icon;
        this.isChecked = isChecked;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
