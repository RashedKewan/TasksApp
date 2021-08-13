package com.example.tasksapp.Service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.tasksapp.MainActivity;
import com.example.tasksapp.Models.Task;
import com.example.tasksapp.R;
import com.example.tasksapp.Utils.MainViewModel;
import com.example.tasksapp.Utils.TaskComparator;
import com.example.tasksapp.Utils.TasksAdapter;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class ForegroundService extends Service {
    public static int NOTIFICATION_ID1 = 1;
    public static final String FOREGROUND_ENDED = " com.example.tasksapp.foreground-ended";
    public static final String FOREGROUND_PROGRESS = " com.example.tasksapp.foreground-progress";

    MainViewModel viewModel;

    private static NotificationManager mNotiMgr = null;
    private Notification.Builder mNotifyBuilder;

    private MyWorker worker;

    @Override
    public void onCreate() {
        // EXECUTES ONE TIME
        initForeground();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        viewModel = MainViewModel.getInstance(getApplication(), getApplicationContext(), (Activity) intent.getSerializableExtra("activity"));
        //this.worker = null;
        if (this.worker == null) {
            this.worker = new MyWorker(viewModel.getRememberTasksList().getValue());
            worker.start();
        }
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class MyWorker extends Thread {
        private ArrayList<Task> rememberTasks;


        public MyWorker(ArrayList<Task> tasks) {
            this.rememberTasks = tasks;
        }

        @Override
        public void run() {
            super.run();
            Intent intent = new Intent(FOREGROUND_PROGRESS);

            try {
                for (int i = 0; i < 100; i++) {
                    updateNotification(Integer.toString(i));
                    Thread.sleep(80);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stopSelf();
            for (int i = 0; i < rememberTasks.size(); i++) {
                try {

                    Task task = rememberTasks.get(rememberTasks.size() - i - 1);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, Integer.parseInt(task.getDate().getYear()));
                    calendar.set(Calendar.MONTH, Integer.parseInt(task.getDate().getMonth()) - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(task.getDate().getDay()));

                    String[] time1 = task.getTime().split(" : ");
                    int hour = Integer.parseInt(time1[0]);
                    int min = Integer.parseInt(time1[1]);

                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, min - 1);
                    calendar.set(Calendar.MILLISECOND, 0);
                    Log.i(TAG, "------------  run: time in sec " + Math.max(calendar.getTimeInMillis() - System.currentTimeMillis() + 60000, 0) / 1000);
                    Thread.sleep(Math.max(calendar.getTimeInMillis() - System.currentTimeMillis() + 60000, 0));
                    updateNotification(task.getSubject());
                    NOTIFICATION_ID1++;
                    intent.putExtra("task", task);
                    sendBroadcast(intent);

                    stopSelf();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void initForeground() {
        String CHANNEL_ID = "my_channel_01";

        if (mNotiMgr == null)
            mNotiMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNEL_ID,
                    "My main channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);
        }

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotifyBuilder = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Loading data...")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent);
        }
        startForeground(NOTIFICATION_ID1, updateNotification(Integer.toString(0)));
    }

    public Notification updateNotification(String details) {
        mNotifyBuilder.
                setContentTitle(details)
                .setSmallIcon(R.drawable.ic_event_note)
                .setOnlyAlertOnce(false);
        Notification notification = mNotifyBuilder
                .build();
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        mNotiMgr.notify(NOTIFICATION_ID1, notification);
        return notification;
    }

}
