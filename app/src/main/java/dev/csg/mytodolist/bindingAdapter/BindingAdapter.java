package dev.csg.mytodolist.bindingAdapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public class BindingAdapter {
    @androidx.databinding.BindingAdapter({"date", "time"})
    public static void isSelectedDate(TextView textView, String date, String time) {
        if (TextUtils.isEmpty(date)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(new StringBuilder().append(date).append(".").toString());
            if (!TextUtils.isEmpty(time)) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(new StringBuilder().append(date).append(", ").append(time).toString());
            }
        }
    }
}
