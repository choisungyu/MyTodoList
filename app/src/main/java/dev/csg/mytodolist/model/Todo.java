package dev.csg.mytodolist.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Todo {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String date;
    private String time;
    private String tag;
    private Boolean isDone = false;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public Todo() {
    }

    public Todo(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public Todo(String title, String date, String time, String tag) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.tag = tag;
    }

    public Todo(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }

    public Todo(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Todo(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
