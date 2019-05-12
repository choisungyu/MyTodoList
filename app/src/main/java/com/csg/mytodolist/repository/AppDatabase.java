package com.csg.mytodolist.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.csg.mytodolist.model.Todo;

@Database(entities = {Todo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TodoDao todoDao();

    private static AppDatabase db;

    public static AppDatabase getInstance(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context,AppDatabase.class,"database_name")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return db;
    }
}
