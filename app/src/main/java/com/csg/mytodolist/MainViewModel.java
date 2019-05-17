package com.csg.mytodolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.csg.mytodolist.model.Todo;
import com.csg.mytodolist.repository.AppDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private AppDatabase mDb;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mDb = AppDatabase.getInstance(application);

    }

    public LiveData<List<Todo>> getItems() {
        return mDb.todoDao().getAll();
    }


}
