package com.csg.mytodolist.model;

public class Todo {
    private String title;
    private String time;

    public Todo(String title, String content) {
        this.title = title;
        this.time = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
