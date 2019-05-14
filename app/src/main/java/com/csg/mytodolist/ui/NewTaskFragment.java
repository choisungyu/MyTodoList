package com.csg.mytodolist.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.csg.mytodolist.R;
import com.csg.mytodolist.model.Todo;
import com.csg.mytodolist.repository.AppDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewTaskFragment extends Fragment {

    private EditText editText;

    public NewTaskFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_task, container, false);
        editText = view.findViewById(R.id.edit_text);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_new_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.check:
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

                String mTitle = editText.getText().toString();
                AppDatabase.getInstance(requireActivity()).todoDao().insertAll(
                        new Todo(mTitle)
                );
                editText.setText("");

                navController.popBackStack();
                return true;

        }
        return super.onOptionsItemSelected(item);

    }
}
