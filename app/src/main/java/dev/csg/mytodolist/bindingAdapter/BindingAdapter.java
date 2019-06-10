package dev.csg.mytodolist.bindingAdapter;

import android.view.View;
import android.widget.TextView;

public class BindingAdapter {
    @androidx.databinding.BindingAdapter("date")
    public static void isSelectedDate(TextView textView, String date) {
        if (date == null) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(date);
        }
    }

//    @androidx.databinding.BindingAdapter("done")
//    public static void isSelectedTaskDone(TextView textView, boolean isChecked) {
//        if (isChecked) {
//
//            textView.setText("안채크");
//        } else {
//            textView.setText("체크");
//        }
//    }
}
