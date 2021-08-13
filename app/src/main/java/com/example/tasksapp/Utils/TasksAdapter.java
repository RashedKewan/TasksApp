package com.example.tasksapp.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasksapp.Interfaces.IDialog;
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

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {
    //Constants
    public static final String DATABASE_TREE_NAME = "Tasks";
    private int CLICKED = 0;


    // Variables

    private MainViewModel viewModel;
    private Application application;
    private Context context;
    private ArrayList<Task> taskList;
    private Activity activity;
    private com.airbnb.lottie.LottieAnimationView shimmer;
    private TaskComparator comparator;


    // Interfaces
    private IDialog dialogListener;
    private ITaskAdapterListener listener;
    private DatabaseReference mDatabase;



    public TasksAdapter(Application application, Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        this.application = application;
        taskList = new ArrayList<>();
        viewModel = MainViewModel.getInstance(application, context, activity);
        shimmer = activity.findViewById(R.id.shimmerAnimation);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(DATABASE_TREE_NAME);

        if (netIsAvailable(context))
            shimmer.setVisibility(View.VISIBLE);

        taskList.clear();
        notifyDataSetChanged();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) return;

                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    task.setId(taskSnapshot.getKey());
                    taskList.add(task);
                    notifyDataSetChanged();
                }
                Log.i(TAG, "onDataChange:viewModel.getIfSortingByTimeSP() " + viewModel.getIfSortingByTimeSP());
                if (viewModel.getIfSortingByTimeSP())
                    Collections.sort(taskList, new TaskComparator());
                shimmer.setVisibility(View.GONE);
                notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "No data founded");
            }
        });

    }


    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        listener = (ITaskAdapterListener) parent.getContext();
        dialogListener = (IDialog) parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View countryView = inflater.inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(countryView);
    }


    @Override
    public void onBindViewHolder(TasksAdapter.TaskViewHolder holder, int position) {
        Task testTask = IsraelTimeZone.getCurrentTimeAsTask();
        Task task = taskList.get(position);
        comparator = new TaskComparator();
        viewModel.setItemSelect(task);
        //  if (!task.isRemember() || (task.isRemember() && comparator.compare(task, testTask) != 1)) {
        if ((task.isRemember() && comparator.compare(task, testTask) != 1)) {
            viewModel.doNotRememberTask(task.getId());
            updateRememberIconState(holder, R.drawable.ic_notifications_offline);
        } else if (task.isRemember()) {
            updateRememberIconState(holder, R.drawable.ic_notifications_active_);
            registerNotificationReceiver(new NotificationReceiver(holder, position));
        }

        /**            Anonymous Inner Class   Listener     **/
        holder.linearLayout.setOnClickListener(view -> {
            viewModel.setItemSelect(task);
            notifyDataSetChanged();
            listener.openTask();
        });


        /**              Member Class Listener              **/
        holder.subject.setOnClickListener(new HandleClick(task));


        /**            Anonymous Inner Class   Listener     **/
        holder.notificationIm.setOnClickListener(view -> {
            if (comparator.compare(task, testTask) == 1) {
                if (task.isRemember()) {
                    holder.animation.setVisibility(View.INVISIBLE);
                    updateRememberIconState(holder, R.drawable.ic_notifications_offline);
                    viewModel.doNotRememberTask(task.getId());

                } else {
                    registerNotificationReceiver(new NotificationReceiver(holder, position));
                    updateRememberIconState(holder, R.drawable.ic_notifications_active_);
                    viewModel.rememberTask(task.getId());
                }
            } else {
                Toast.makeText(application, "Set valid future time", Toast.LENGTH_SHORT).show();
                updateRememberIconState(holder, R.drawable.ic_notifications_offline);
            }
            updateData(taskList);
        });
        if (taskList != null || taskList.size() != 0)
            holder.bindData(task.getSubject(), task.getTime(), task.getDate().toString());
    }


    private void updateRememberIconState(TasksAdapter.TaskViewHolder holder, int iconId) {
        holder.notificationIm.setImageDrawable(ContextCompat.getDrawable(activity, iconId));

    }


    public void insertData(ArrayList<Task> insertList) {
        MyDiffUtilCallback diffUtilCallback = new MyDiffUtilCallback(taskList, insertList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);

        taskList.addAll(insertList);
        diffResult.dispatchUpdatesTo(this);
        updateData(taskList);
        shimmer.setVisibility(View.GONE);
    }

    public void updateData(ArrayList<Task> newList) {
        MyDiffUtilCallback diffUtilCallback = new MyDiffUtilCallback(taskList, newList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
        taskList.clear();
        taskList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }


    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }


    /**
     * Member Class Listener
     **/
    private class HandleClick implements View.OnClickListener {
        Task task;

        public HandleClick(Task task) {
            this.task = task;
        }

        public void onClick(View arg0) {
            viewModel.setItemSelect(task);
            notifyDataSetChanged();
            listener.openTask();
        }
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView subject;
        private final TextView time;
        private final TextView date;
        private RelativeLayout rowRelativelayout;
        private LinearLayout linearLayout;
        private ImageView notificationIm;
        private com.airbnb.lottie.LottieAnimationView animation;

        public TaskViewHolder(View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.task_item_subject);
            time = itemView.findViewById(R.id.task_item_time);
            date = itemView.findViewById(R.id.task_item_date);
            notificationIm = itemView.findViewById(R.id.task_item_image_notification);
            rowRelativelayout = itemView.findViewById(R.id.task_item_relativeLayout);
            linearLayout = itemView.findViewById(R.id.task_item_line1);
            animation = itemView.findViewById(R.id.task_item_liveTask);


        }

        public void bindData(String subject_, String time_, String date_) {
            subject.setText(subject_);
            time.setText(time_);
            date.setText(date_);

        }
    }

    /**
     * NotificationReceiver
     **/
    private void registerNotificationReceiver(NotificationReceiver receiver) {
        IntentFilter filter = new IntentFilter(ForegroundService.FOREGROUND_PROGRESS);
        activity.registerReceiver(receiver, filter);
    }

    public class NotificationReceiver extends BroadcastReceiver {
        private TasksAdapter.TaskViewHolder holder;
        int position;

        public NotificationReceiver(TasksAdapter.TaskViewHolder holder, int position) {
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Task t = (Task) intent.getSerializableExtra("task");
            if (comparator.compare(t, taskList.get(position)) == 0) {
                holder.animation.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * CHECKING CONNECTION
     **/
    private boolean netIsAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Interface
     **/
    public interface ITaskAdapterListener {
        void openTask();
    }


}


/*****************************************    END OF CODE     *******************************************/

