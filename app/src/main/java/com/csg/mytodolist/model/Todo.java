package com.csg.mytodolist.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Todo {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @Ignore
    private String title;
    private String content;

    public Todo() {
    }

    public Todo(String title, String content) {
        this.title = title;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
