package com.piapps.flashcard.model;

/**
 * Created by abduaziz on 2/16/17.
 */

public class Card{

    String id;
    String setId;
    String front;
    String back;
    String color;
    String frontImage;
    String backImage;
    String frontPath;
    String backPath;

    public Card(){}

    public Card(String id, String setId,
                String front, String back,
                String color, String frontImage,
                String backImage,
                String frontPath,
                String backPath) {
        this.id = id;
        this.setId = setId;
        this.front = front;
        this.back = back;
        this.color = color;
        this.frontImage = frontImage;
        this.backImage = backImage;
        this.frontPath = frontPath;
        this.backPath = backPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(String frontImage) {
        this.frontImage = frontImage;
    }

    public String getBackImage() {
        return backImage;
    }

    public void setBackImage(String backImage) {
        this.backImage = backImage;
    }

    public String getFrontPath() {
        return frontPath;
    }

    public void setFrontPath(String frontPath) {
        this.frontPath = frontPath;
    }

    public String getBackPath() {
        return backPath;
    }

    public void setBackPath(String backPath) {
        this.backPath = backPath;
    }
}
