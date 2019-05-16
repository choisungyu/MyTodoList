package com.csg.mytodolist.ui;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.csg.mytodolist.R;
import com.csg.mytodolist.model.Todo;
import com.csg.mytodolist.repository.AppDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateTaskFragment extends Fragment {

    public UpdateTaskFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_task, container, false);
        // 수정해야 하는 editText
        EditText editText = view.findViewById(R.id.edit_text);

        Bundle bundle = getArguments();
        if (bundle != null) {

            String mTitle = bundle.getString("title");
            editText.setText(mTitle);
        }

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_update_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.check:
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                // Todo : 수정한 내용과 함께 저장하면서(!=insert(n+1번째 자리) => update(그 자리로 가야함) ) 백스택한다.
                navController.popBackStack();
                return true;

        }
        return super.onOptionsItemSelected(item);

    }
}
