package dev.csg.mytodolist.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Collections;
import java.util.Comparator;

@Entity
public class Todo {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    public Todo() {
    }

    public Todo(String title) {
        this.title = title;
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
