package dev.csg.mytodolist.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import dev.csg.mytodolist.R;
import dev.csg.mytodolist.model.Todo;
import dev.csg.mytodolist.repository.AppDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewTaskFragment extends Fragment {

    private EditText editText;
    private ImageView imageView;
    private String mTitle;

    public NewTaskFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_task, container, false);

        // 새로 입력 받은 값
        editText = view.findViewById(R.id.edit_text);
        imageView = view.findViewById(R.id.btn_date_picker_dialog);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        imageView.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
        });
        super.onViewCreated(view, savedInstanceState);
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

                // newTask 에서도 삽입 있어야 함
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
