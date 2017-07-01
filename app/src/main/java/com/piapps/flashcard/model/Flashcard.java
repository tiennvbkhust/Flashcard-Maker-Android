package com.piapps.flashcard.model;

/**
 * Created by abduaziz on 2/14/17.
 */

public class Flashcard {

    private String setId;
    private String title;
    private String count;
    private String label;
    private String color;
    private String useCount;

    public Flashcard() {
    }

    public Flashcard(String setId, String title, String count, String label, String color, String useCount) {
        this.setId = setId;
        this.title = title;
        this.count = count;
        this.label = label;
        this.color = color;
        this.useCount = useCount;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUseCount() {
        return useCount;
    }

    public void setUseCount(String useCount) {
        this.useCount = useCount;
    }
}
