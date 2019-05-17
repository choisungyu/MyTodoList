package com.csg.mytodolist.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.csg.mytodolist.model.Todo;

import java.util.List;

@Dao
public interface TodoDao {
    @Query("SELECT * FROM todo ORDER BY 'id'")
    LiveData<List<Todo>> getAll();

    @Insert
    void insertAll(Todo... todos);

    @Delete
    void deleteAll(List<Todo> todos);

    @Update
    void update(Todo todo);

    @Query("SELECT * FROM todo WHERE id = :id")
    Todo getTodo(int id);

}
