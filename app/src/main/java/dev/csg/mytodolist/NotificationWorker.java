package dev.csg.mytodolist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;
import java.util.concurrent.TimeUnit;

import dev.csg.mytodolist.model.Todo;
import dev.csg.mytodolist.ui.NewTaskFragment;

public class NotificationWorker extends Worker {
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString("title");
        String date = getInputData().getString("date");
        String time = getInputData().getString("time");

        int id = getInputData().getInt("id", 0);

        sendNotification(title, date, time, id);
        return Result.success();
    }

    private void sendNotification(String title, String date, String time, int id) {
        Intent intent = new Intent(getApplicationContext(), NewTaskFragment.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("id", id);

        Bitmap largeIcon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.common_google_signin_btn_icon_dark_focused);


        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("default", "default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // 노티 알림 처리 하는 곳(푸쉬 메시지 작성)
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(date + "," + time + "에 작업")
                .setContentText(title)
                .setContentIntent(pendingIntent)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_focused)
                .setAutoCancel(true);

        notificationManager.notify(id, notification.build());
    }

    public static void scheduleReminder(long duration, Data data, String tag) {
        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                .addTag(tag) //  tag 를 달아서 cancel 할 수 도 있음
                .setInputData(data).build();

        WorkManager.getInstance().enqueue(notificationWork);
    }

    public static void cancelReminder(String tag) {
        WorkManager.getInstance().cancelAllWorkByTag(tag);
    }



}
