package com.example.tasksapp.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tasksapp.Models.Task;
import com.example.tasksapp.R;
import com.example.tasksapp.Service.ForegroundService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.ContentValues.TAG;

public class MainViewModel extends AndroidViewModel {
    public static final String DATABASE_TREE_NAME = "Tasks";

    private MutableLiveData<ArrayList<Task>> tasksLiveData;
    private MutableLiveData<ArrayList<Task>> rememberTasksLiveData;
    private MutableLiveData<Task> indexItemSelected;
    private static MainViewModel instance;
    private DatabaseReference mDatabase;
    public Activity activity;
    public Context context;
    private IStartFirstFragmentListener listener;


    public MainViewModel(@NonNull Application application, Context context, Activity activity) {
        super(application);
        this.activity = activity;
        this.context = context;
        listener = (IStartFirstFragmentListener) context;
        init();
    }

    public MutableLiveData<Task> getItemSelected() {
        return indexItemSelected;
    }

    public LiveData<ArrayList<Task>> getRememberTasksList() {
        return rememberTasksLiveData;
    }

    public LiveData<ArrayList<Task>> getTasksListLiveData() {
        return tasksLiveData;
    }

    public void setTasksListLiveData(ArrayList<Task> tasks) {
        tasksLiveData.setValue(tasks);
    }

    public void setItemSelect(Task task) {
        indexItemSelected.setValue(task);
    }


    public static MainViewModel getInstance(Application application, Context context, Activity activity) {
        if (instance == null)
            instance = new MainViewModel(application, context, activity);
        return instance;
    }

    public void init() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(DATABASE_TREE_NAME);
        indexItemSelected = new MutableLiveData<>();
        rememberTasksLiveData = new MutableLiveData<>();
        tasksLiveData = new MutableLiveData<>();
        setRememberTasksList(getRememberTasksList1());
        setTasksListLiveData(getTasksList());
    }

    public void rememberTask(String id) {
        mDatabase.child(id).child("remember").setValue(true);
        setRememberTasksList(getRememberTasksList1());
    }

    public void doNotRememberTask(String id) {
        mDatabase.child(id).child("remember").setValue(false);
        setRememberTasksList(getRememberTasksList1());
    }

    public void setRememberTasksList(ArrayList<Task> rTasks) {
        rememberTasksLiveData.setValue(rTasks);
        if (!isMyServiceRunning(ForegroundService.class)) {
            Intent intent = new Intent(activity, ForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.startForegroundService(intent);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Task> getRememberTasksList1() {
        ArrayList<Task> rTasks = new ArrayList<>();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) return;

                Task testTask = IsraelTimeZone.getCurrentTimeAsTask();
                TaskComparator comparator = new TaskComparator();

                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    task.setId(taskSnapshot.getKey());
                    if (task != null) {
                        if (task.isRemember() && comparator.compare(task, testTask) == 1) {
                            rTasks.add(task);
                            Log.i(TAG, "============added " + task.toString());
                        }
                    }
                }
                if (rTasks != null && getIfSortingByTimeSP())
                    Collections.sort(rTasks, new TaskComparator());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rTasks;
    }

    public ArrayList<Task> getTasksList() {
        ArrayList<Task> rTasks = new ArrayList<>();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) return;

                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    task.setId(taskSnapshot.getKey());
                    if (task != null) {
                        rTasks.add(task);
                        // Log.i(TAG, "============added " + task.toString());
                    }
                }
                if (rTasks != null && getIfSortingByTimeSP())
                    Collections.sort(rTasks, new TaskComparator());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rTasks;
    }

    public void saveTask(Task task, boolean NEW_TASK) {
        ProgressBar progressBar = activity.findViewById(R.id.fragment_task_data_progressBar);
        Button addBtn = activity.findViewById(R.id.fragment_task_data_add);
        if (NEW_TASK)
            mDatabase.push().setValue(task).addOnSuccessListener(unused -> {
                listener.startFragment();
                progressBar.setVisibility(View.GONE);
                addBtn.setVisibility(View.VISIBLE);
            });
        else
            mDatabase.child(task.getId()).setValue(task).addOnSuccessListener(unused -> {
                listener.startFragment();
                progressBar.setVisibility(View.GONE);
                addBtn.setVisibility(View.VISIBLE);
            });
        Toast.makeText(activity, "Saved", Toast.LENGTH_LONG).show();
    }

    public boolean getIfSortingByTimeSP() {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean("sortedByTime", false);
    }


    public interface IStartFirstFragmentListener {
        void startFragment();
    }
/*****************************************    END OF CODE     *******************************************/


}
