package dev.csg.mytodolist.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import dev.csg.mytodolist.R;
import dev.csg.mytodolist.model.Todo;
import dev.csg.mytodolist.repository.AppDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateTaskFragment extends Fragment {
    private Todo mTodo;
    private EditText mEditText;
    private ImageView mImageView;
    private EditText mDateEditText;
    private String mDate;

    public UpdateTaskFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_task, container, false);

        mImageView = view.findViewById(R.id.btn_date_picker_dialog);
        mDateEditText = view.findViewById(R.id.date_edit_text);
        mEditText = view.findViewById(R.id.edit_text);

        Bundle bundle = getArguments();
        if (bundle != null) {

            int id = bundle.getInt("id");

            mTodo = AppDatabase.getInstance(requireContext()).todoDao()
                    .getTodoById(id);
            mEditText.setText(mTodo.getTitle());
            mDateEditText.setText(mTodo.getDate());
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mImageView.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment((view1, year, month, dayOfMonth) -> {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth, 0, 0);
                Date chosenDate = cal.getTime();

                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
                mDate = dateFormat.format(chosenDate);
                // Display the formatted date

                if (mDate.isEmpty()) {

                } else {

                    mDateEditText.setText(mDate);
                }

            });
            newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
        });

        CheckBox checkBox = view.findViewById(R.id.check_box);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                buttonView.setText("작업 완료!");
            } else {
                buttonView.setText("작업을 완료하시겠습니까?");
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_update_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.check) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

            mTodo.setTitle(mEditText.getText().toString());
            mTodo.setDate(mDateEditText.getText().toString());
            if (mDateEditText.getText().toString().isEmpty()) {
                mTodo.setDate("");
            } else {

                AppDatabase.getInstance(requireContext()).todoDao().update(mTodo);
            }

            navController.popBackStack();


            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
