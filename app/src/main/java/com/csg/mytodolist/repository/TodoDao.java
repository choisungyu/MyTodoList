package com.csg.mytodolist.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.csg.mytodolist.model.Todo;

import java.util.List;

@Dao
public interface TodoDao {
    @Query("SELECT * FROM todo ORDER BY 'id'")
    LiveData<List<Todo>> getAll();

    @Insert
    void insertAll(Todo...todos);

}
