package com.example.scit.diarysp;

public class Diary {

    private int code;
    String title;
    String date;
    String contents;

    public Diary(){}

    public Diary(int code, String title, String date, String contents) {
        this.code = code;
        this.title = title;
        this.date = date;
        this.contents = contents;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "Diary{" +
                "code=" + code +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
