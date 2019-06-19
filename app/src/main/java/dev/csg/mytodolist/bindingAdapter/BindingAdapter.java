package dev.csg.mytodolist.bindingAdapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class BindingAdapter {
    @androidx.databinding.BindingAdapter("date")
    public static void isSelectedDate(TextView textView, long date) {

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        String dateText = dateFormat.format(date);

        SimpleDateFormat timeFormat = new SimpleDateFormat("a hh:mm", Locale.getDefault());
        String timeText = timeFormat.format(date);


        if (TextUtils.isEmpty(dateText)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.format("%s.", dateText));
            if (!TextUtils.isEmpty(timeText)) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(String.format("%s, %s", dateText, timeText));
            }
        }
    }
}
