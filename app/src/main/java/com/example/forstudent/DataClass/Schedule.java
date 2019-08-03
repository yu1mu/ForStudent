package com.example.forstudent.DataClass;

import java.util.Calendar;

public class Schedule {

    String title;
    Calendar date;
    String memo;
    boolean important;

    public Schedule(String title, Calendar date, String memo, boolean important) {
        this.title = title;
        this.date = date;
        this.memo = memo;
        this.important = important;
    }

    public Schedule(String title, Calendar date, boolean important) {
        this.title = title;
        this.date = date;
        this.important = important;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "title='" + title + '\'' +
                ", date=" + date +
                ", memo='" + memo + '\'' +
                ", important=" + important +
                '}';
    }
}
