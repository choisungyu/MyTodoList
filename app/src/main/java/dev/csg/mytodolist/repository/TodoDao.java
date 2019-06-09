package dev.csg.mytodolist.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import dev.csg.mytodolist.model.Todo;

import java.util.List;

@Dao
public interface TodoDao {
    @Query("SELECT * FROM todo ORDER BY id DESC")
    LiveData<List<Todo>> getAll();

    @Query("SELECT * FROM todo")
    Todo getTodo();

    @Insert
    void insertAll(Todo todo);

    @Delete
    void deleteAll(List<Todo> todos);

    @Update
    void update(Todo todo);

    @Update
    void update(List<Todo> todos);

    @Query("SELECT * FROM todo WHERE id = :id")
    Todo getTodoById(int id);

    @Query("SELECT * FROM todo WHERE id IN (:userIds)")
    List<Todo> loadAllByIds(List<Integer> userIds);

    @Query("SELECT * FROM todo WHERE isDone = 0 ORDER BY id DESC")
        // unchecked
    LiveData<List<Todo>> getMainTask();

    @Query("SELECT * FROM todo WHERE isDone = 1 ORDER BY id DESC")
        // checked
    LiveData<List<Todo>> getDoneTask();

}
