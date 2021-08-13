package com.example.tasksapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.tasksapp.Fragments.SettingFragment;
import com.example.tasksapp.Fragments.ShowTaskFragment;
import com.example.tasksapp.Fragments.TaskDataFragment;
import com.example.tasksapp.Fragments.TasksFragment;
import com.example.tasksapp.Models.Task;
import com.example.tasksapp.Service.ForegroundService;
import com.example.tasksapp.Utils.MainViewModel;
import com.example.tasksapp.Utils.MyAlertDialog;
import com.example.tasksapp.Utils.MyDiffUtilCallback;
import com.example.tasksapp.Utils.TasksAdapter;
import com.example.tasksapp.Interfaces.IDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Serializable, MainViewModel.IStartFirstFragmentListener, IDialog, MyAlertDialog.IMyAlertDialogListener, TasksFragment.ITasksFragmentListener, ShowTaskFragment.IShowTaskFragmentListener, TasksAdapter.ITaskAdapterListener {
    //    private DatabaseReference mDatabase;
//    private MainViewModel viewModel;
    private NetworkReceiver networkReceiver;
    private com.airbnb.lottie.LottieAnimationView noNetwork;
    private View containerFragment;


    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //When pressing back arrow ,  if Fragments Manager is empty i need to get out of app
        if (getSupportFragmentManager().findFragmentById(R.id.container_fragment) == null) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    protected void onPause() {
        if (networkReceiver != null)
            unregisterReceiver(networkReceiver);
        networkReceiver = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //  Broadcast Receiver: Dynamic Registration
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkReceiver, filter, Manifest.permission.ACCESS_NETWORK_STATE, null);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // CHECKING IF FOREGROUND SERVICE IS RUNNING
        if (!isMyServiceRunning(ForegroundService.class)) {
            Intent intent = new Intent(
                    this,
                    ForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.putExtra("activity", this);
                startForegroundService(intent);
            }
        }

        noNetwork = findViewById(R.id.networkStateAnimation);
        containerFragment = findViewById(R.id.container_fragment);

        networkReceiver = new NetworkReceiver();
        registerNetworkReceiver();

        replaceWith(R.id.container_fragment, TasksFragment.newInstance());


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*          HANDLING NETWORKS         */
    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkReceiver(), filter);
    }

    public class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                containerFragment.setVisibility(View.VISIBLE);
                noNetwork.setVisibility(View.GONE);
                getSupportActionBar().show();
            } else {
                noNetwork.setVisibility(View.VISIBLE);
                containerFragment.setVisibility(View.GONE);
                getSupportActionBar().hide();
            }
        }
    }
    /*          END HANDLING NETWORKS MODE          */


    private <T extends Fragment> void replaceWith(int containerViewId, T fragment) {
        getSupportFragmentManager().beginTransaction().
                replace(containerViewId, fragment).
                addToBackStack(null).
                commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void addTask() {
        replaceWith(R.id.container_fragment, new TaskDataFragment());
    }

    @Override
    public void openSettings() {
        replaceWith(R.id.container_fragment, new SettingFragment());
    }

    @Override
    public void editTask() {
        replaceWith(R.id.container_fragment, TaskDataFragment.newInstance());
    }

    @Override
    public void openTask() {
        replaceWith(R.id.container_fragment, ShowTaskFragment.newInstance());
    }


    @Override
    public void startFragment() {
        replaceWith(R.id.container_fragment, TasksFragment.newInstance());
    }


    /*      HANDLING DIALOG       */
    @Override
    public void popUpDialog() {
        DialogFragment exitDialog = MyAlertDialog.newInstance("Exit The Application.");
        exitDialog.show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    public void onPositiveButton(DialogInterface dialog, int whichButton) {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onNegativeButton(DialogInterface dialog, int whichButton) {
        dialog.dismiss();
    }
    /*          END HANDLING DIALOG          */


/*****************************************    END OF CODE     *******************************************/


}