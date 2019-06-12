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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
public class UpdateTaskFragment extends Fragment {
    private Todo mTodo;
    private EditText mEditText;
    private EditText mDateEditText;
    private String mDate;
    private LinearLayout mLinearLayout;
    private CheckBox mCheckBox;

    public UpdateTaskFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_task, container, false);

        mDateEditText = view.findViewById(R.id.date_edit_text);
        mDateEditText.setFocusable(false);
        mDateEditText.setClickable(false);
        mEditText = view.findViewById(R.id.edit_text);
        mLinearLayout = view.findViewById(R.id.ll_date_picker);

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
        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatePickerDialog();
            }
        });

        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatePickerDialog();
            }
        });

        mCheckBox = view.findViewById(R.id.check_box);
        mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                buttonView.setText("작업 완료!");
            } else {
                buttonView.setText("작업을 완료하시겠습니까?");
            }
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
            // Display the formatted date

            mDateEditText.setText(mDate);

        });
        newFragment.show(UpdateTaskFragment.this.requireActivity().getSupportFragmentManager(), "datePicker");
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

            Vibrator vibe = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);

            // 왜 있는거지,,,?
//            if (mDateEditText.getText().toString().isEmpty()) {
//                mTodo.setDate("");
//            }

            if (TextUtils.isEmpty(mEditText.getText().toString())) {
                if (vibe != null) {
                    vibe.vibrate(50);
                    Toast.makeText(requireContext(), "처음 작업을 입력하세요!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            } else {
                Bundle bundle = getArguments();
                if (bundle != null) {

                    int id = bundle.getInt("id");
                    mTodo = AppDatabase.getInstance(requireContext()).todoDao().getTodoById(id);
                    mTodo.setTitle(mEditText.getText().toString());
                    mTodo.setDate(mDateEditText.getText().toString());

                    if (mCheckBox.isChecked()) {
                        mTodo.setDone(true);
                        AppDatabase.getInstance(requireContext()).todoDao().update(mTodo);
                    }
                }
            }

            AppDatabase.getInstance(requireContext()).todoDao().update(mTodo);

            navController.popBackStack();


            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
