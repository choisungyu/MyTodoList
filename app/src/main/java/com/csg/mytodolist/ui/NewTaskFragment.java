package com.csg.mytodolist.ui;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.csg.mytodolist.R;
import com.csg.mytodolist.model.Todo;
import com.csg.mytodolist.repository.AppDatabase;

import java.util.Calendar;
import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewTaskFragment extends Fragment {

    private EditText editText;

    public NewTaskFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

//    private void init(View view) {
//        final Calendar cal = Calendar.getInstance();
//
//        Log.e(TAG, cal.get(Calendar.YEAR)+"");
//        Log.e(TAG, cal.get(Calendar.MONTH)+1+"");
//        Log.e(TAG, cal.get(Calendar.DATE)+"");
//        Log.e(TAG, cal.get(Calendar.HOUR_OF_DAY)+"");
//        Log.e(TAG, cal.get(Calendar.MINUTE)+"");
//
//        //DATE PICKER DIALOG
//        view.findViewById(R.id.btn_date_picker_dialog).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                DatePickerDialog dialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
//
////                        String msg = String.format("%d 년 %d 월 %d 일", year, month+1, date);
////                        Toast.makeText(PickerActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                }
//                        , cal.get(Calendar.YEAR)
//                        , cal.get(Calendar.MONTH)
//                        , cal.get(Calendar.DATE));
//
//                dialog.getDatePicker().setMaxDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
//                dialog.show();
//
//            }
//        });
//
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_task, container, false);
//        init(view);

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
