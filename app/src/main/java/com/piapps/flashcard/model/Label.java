package com.piapps.flashcard.model;

/**
 * Created by abduaziz on 2/18/17.
 */

public class Label{

    String title;
    int icon;

    public Label(String title, int icon) {
        this.title = title;
        this.icon = icon;
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

}
