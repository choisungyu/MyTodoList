package dev.csg.mytodolist.ui;


import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import dev.csg.mytodolist.R;
import dev.csg.mytodolist.model.Todo;
import dev.csg.mytodolist.repository.AppDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewTaskFragment extends Fragment {

    private EditText mTitleEditText;
    private EditText mDateEditText;
    private String mDate;

    public NewTaskFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_task, container, false);

        // 새로 입력 받은 값
        mTitleEditText = view.findViewById(R.id.edit_text);
        mDateEditText = view.findViewById(R.id.date_edit_text);
        mDateEditText.setFocusable(false);
        mDateEditText.setClickable(false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mDateEditText.setOnClickListener(v -> {
            getDatePickerDialog();
        });
        view.findViewById(R.id.ll_date_picker).setOnClickListener(v -> {
            getDatePickerDialog();
        });
        super.onViewCreated(view, savedInstanceState);


    }

    private void getDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment((view1, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth, 0, 0);
            Date chosenDate = cal.getTime();

            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
            mDate = dateFormat.format(chosenDate);
            mDateEditText.setText(mDate);
        });
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
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

                Vibrator vibe = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);

                if (TextUtils.isEmpty(mTitleEditText.getText().toString())) {
                    if (vibe != null) {
                        vibe.vibrate(100);
                        Toast.makeText(requireContext(), "처음 작업을 입력하세요!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                } else {
                    String mTitle = mTitleEditText.getText().toString();
                    AppDatabase.getInstance(requireActivity()).todoDao().insertAll(
                            new Todo(mTitle, mDate)
                    );
                }

                navController.popBackStack();
                return true;

        }
        return super.onOptionsItemSelected(item);

    }
}
