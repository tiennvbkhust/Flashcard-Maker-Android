package com.piapps.flashcard.model;

/**
 * Created by abduaziz on 3/4/17.
 */

public class Stats{

    String setId;
    String trueAnswers;
    String timeSpent;
    String date;

    public Stats(){}

    public Stats(String setId, String trueAnswers, String timeSpent, String date) {
        this.setId = setId;
        this.trueAnswers = trueAnswers;
        this.timeSpent = timeSpent;
        this.date = date;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getTrueAnswers() {
        return trueAnswers;
    }

    public void setTrueAnswers(String trueAnswers) {
        this.trueAnswers = trueAnswers;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "setId='" + setId + '\'' +
                ", trueAnswers='" + trueAnswers + '\'' +
                ", timeSpent='" + timeSpent + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
