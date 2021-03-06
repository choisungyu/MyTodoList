package dev.csg.mytodolist.ui;


import android.annotation.SuppressLint;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.work.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dev.csg.mytodolist.NotificationWorker;
import dev.csg.mytodolist.R;
import dev.csg.mytodolist.model.Todo;
import dev.csg.mytodolist.repository.AppDatabase;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateTaskFragment extends Fragment {

    private LinearLayout mTimePickerLayout;

    private CheckBox mCheckBox;

    private Calendar mCalendar;


    private EditText mTitleEditText;
    private EditText mDateEditText;
    private EditText mTimeEditText;

    private ImageView mCancelImageView;
    private ImageView mDatePickerImageView;
    private ImageView mTimePickerImageView;

    private Todo mTodo;
    private InputMethodManager imm;

    public UpdateTaskFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCalendar = Calendar.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_task, container, false);

        mDateEditText = view.findViewById(R.id.date_edit_text);
        mDateEditText.setFocusable(false);
        mDateEditText.setClickable(false);

        mTimeEditText = view.findViewById(R.id.time_edit_text);
        mTimeEditText.setFocusable(false);
        mTimeEditText.setClickable(false);


        mCancelImageView = view.findViewById(R.id.btn_cancel_date_picker_dialog);
        mDatePickerImageView = view.findViewById(R.id.btn_date_picker_dialog);
        mTimePickerImageView = view.findViewById(R.id.btn_time_picker_dialog);

        mTitleEditText = view.findViewById(R.id.edit_text);

        Bundle bundle = getArguments();
        if (bundle != null) {

            int id = bundle.getInt("id");

            mTodo = AppDatabase.getInstance(requireContext()).todoDao()
                    .getTodoById(id);

            long todoDate = mTodo.getDate();
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
            String dateText = dateFormat.format(todoDate);

            SimpleDateFormat timeFormat = new SimpleDateFormat("a hh:mm", Locale.getDefault());
            String timeText = timeFormat.format(todoDate);

            mTitleEditText.setText(mTodo.getTitle());
            mDateEditText.setText(dateText);
            mTimeEditText.setText(timeText);

        }
        imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);

        mTimePickerLayout = view.findViewById(R.id.ll_time_picker);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mDateEditText.setOnClickListener(v -> getDatePickerDialog());
        mDatePickerImageView.setOnClickListener(v -> getDatePickerDialog());

        mCancelImageView.setOnClickListener(v -> {
            mDateEditText.setText(null);

            mCancelImageView.setVisibility(View.GONE);

            // 취소 했을시, time 저장된 값 초기화 시켜야 함
            mTimeEditText.setText(null);
            mTimeEditText.getText().clear();

            mTimePickerLayout.setVisibility(View.GONE);
        });

        mTimeEditText.setOnClickListener(v -> getTimePickerDialog());

        mTimePickerImageView.setOnClickListener(v -> getTimePickerDialog());

        mCheckBox = view.findViewById(R.id.check_box);
        mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                buttonView.setText("작업 완료!");
            } else {
                buttonView.setText("작업을 완료하시겠습니까?");
            }
        });

        mTimePickerLayout.setVisibility(View.VISIBLE);


        super.onViewCreated(view, savedInstanceState);
    }

    private void getDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment((view1, year, month, dayOfMonth) -> {

            mCalendar.set(year, month, dayOfMonth);

            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
            String date = dateFormat.format(mCalendar.getTime());

            mDateEditText.setText(date);
            mCancelImageView.setVisibility(View.VISIBLE);
            mTimePickerLayout.setVisibility(View.VISIBLE);

        });
        newFragment.show(UpdateTaskFragment.this.requireActivity().getSupportFragmentManager(), "datePicker");
    }

    private void getTimePickerDialog() {
        @SuppressLint("DefaultLocale") DialogFragment timeFragment = new TimePickerFragment((view, hourOfDay, minute) -> {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat dateFormat = new SimpleDateFormat("a hh:mm", Locale.getDefault());
            String time = dateFormat.format(mCalendar.getTime());
            mTimeEditText.setText(time);

        });
        timeFragment.show(requireActivity().getSupportFragmentManager(), "TimePicker");
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

            if (TextUtils.isEmpty(mTitleEditText.getText().toString())) {
                if (vibe != null) {
                    vibe.vibrate(50);
                }
                Toast.makeText(requireContext(), "처음 작업을 입력하세요!", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (mTodo != null) {
                mTodo.setDone(mCheckBox.isChecked());
                mTodo.setTitle(getTitle());
                mTodo.setDate(mCalendar.getTimeInMillis());
                AppDatabase.getInstance(requireContext()).todoDao().update(mTodo);

                long alertTime = mCalendar.getTimeInMillis() - System.currentTimeMillis();

                int id = (int) (Math.random() * 50 + 1);

                // Data 를 만들어서 빌더를 통해서 보냄
                Data data = new Data.Builder()
                        .putString("title", getTitle())
                        .putString("date", getDate())
                        .putString("time", getTime())
                        .putInt("id", id)
                        .build();

                NotificationWorker.scheduleReminder(alertTime, data, mTodo.getTag());

                imm.hideSoftInputFromWindow(mTitleEditText.getWindowToken(), 0);

                navController.popBackStack();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);

    }


    private String getDate() {
        return mDateEditText.getText().toString();
    }

    private String getTime() {
        return mTimeEditText.getText().toString();
    }

    private String getTitle() {
        return mTitleEditText.getText().toString();
    }
}
