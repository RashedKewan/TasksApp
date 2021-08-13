package com.example.tasksapp.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tasksapp.Models.Date;
import com.example.tasksapp.Models.Task;
import com.example.tasksapp.R;
import com.example.tasksapp.Utils.MainViewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

public class TaskDataFragment extends Fragment implements View.OnClickListener {
    private TextInputLayout subject, day, month, year, taskData;
    private ProgressBar progressBar;
    private TextView time;
    private MainViewModel myViewModel;
    private static int flag = 0;
    private MaterialTimePicker picker;
    private Button add;
    private Task task;
    private String timeString;


    private void inflateView(View v) {
        subject = (TextInputLayout) v.findViewById(R.id.fragment_task_data_subject);
        time = (TextView) v.findViewById(R.id.fragment_task_data_time);
        day = (TextInputLayout) v.findViewById(R.id.fragment_task_data_day);
        month = (TextInputLayout) v.findViewById(R.id.fragment_task_data_month);
        year = (TextInputLayout) v.findViewById(R.id.fragment_task_data_year);
        taskData = (TextInputLayout) v.findViewById(R.id.fragment_task_data_text);
        add = (Button) v.findViewById(R.id.fragment_task_data_add);
        progressBar = v.findViewById(R.id.fragment_task_data_progressBar);
    }


    public TaskDataFragment() {
    }


    public static TaskDataFragment newInstance() {
        TaskDataFragment fragment = new TaskDataFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        flag = 1;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_data, container, false);
    }

    @Override
    public void onPause() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        super.onPause();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myViewModel = MainViewModel.getInstance(getActivity().getApplication(), getContext(), getActivity());
        inflateView(view);
        add.setText("add");

        /*            Anonymous Inner Class   Listener     */
        time.setOnClickListener(view1 -> showTimePicker());


        if (flag == 1) {
            task = myViewModel.getItemSelected().getValue();
            subject.getEditText().setText(task.getSubject());
            time.setText(task.getTime());
            day.getEditText().setText(task.getDate().getDay());
            month.getEditText().setText(task.getDate().getMonth());
            year.getEditText().setText(task.getDate().getYear());
            taskData.getEditText().setText(task.getTaskData());

            add.setText("Edit");

        }
        /*              Implementation in Activity         */
        add.setOnClickListener(this);


    }


    private void showTimePicker() {
        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();
        picker.show(getActivity().getSupportFragmentManager(), "note");
        picker.addOnPositiveButtonClickListener(view -> {
            timeString = picker.getHour() + " : " + picker.getMinute();
            time.setText("");
            time.setText(timeString);
        });

    }


    /*              Implementation in Activity         */
    @Override
    public void onClick(View v) {
        // Get Edit Text's Info


        String s_subject = subject.getEditText().getText().toString().trim();
        String s_time = time.getText().toString().trim();
        String s_day = day.getEditText().getText().toString().trim();
        String s_month = month.getEditText().getText().toString().trim();
        String s_year = year.getEditText().getText().toString().trim();
        String s_taskData = taskData.getEditText().getText().toString().trim();

        //  check if there is one of them has'nt fielded
        if (s_taskData.equals("")
                | s_subject.equals("")
                | s_time.equals("")
                | s_day.equals("")
                | s_month.equals("")
                | s_year.equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), "One Or more  Fields are Empty", Toast.LENGTH_SHORT).show();
        } else {

            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH)+1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            String[] time1 = s_time.split(" : ");

            if (Integer.parseInt(s_month) < 1
                    | Integer.parseInt(s_month) < month
                    | Integer.parseInt(s_month) > 12
                    | Integer.parseInt(s_day) > 31
                    | (Integer.parseInt(s_day) < day&& Integer.parseInt(s_month) == month)
                    | Integer.parseInt(s_day) < 1
                    | Integer.parseInt(s_year) < year
                    | Integer.parseInt(time1[0]) < hour
                    | (Integer.parseInt(time1[0]) == hour&&Integer.parseInt(time1[1])<min )
            )

                Toast.makeText(getActivity().getApplicationContext(), "Please Enter Valid Date", Toast.LENGTH_SHORT).show();
            else {
                progressBar.setVisibility(View.VISIBLE);
                add.setVisibility(View.GONE);
                Task task1;
                if (flag == 0) {
                    task1 = new Task(s_subject, s_time, new Date(s_day, s_month, s_year), s_taskData);
                    task1.setRemember(false);
                    myViewModel.saveTask(task1, true);
                } else {
                    task.getDate().setDay(s_day);
                    task.getDate().setMonth(s_month);
                    task.getDate().setYear(s_year);

                    task.setSubject(s_subject);
                    task.setTaskData(s_taskData);
                    task.setTime(s_time);
                    task1 = task;
                    flag = 0;

                    myViewModel.saveTask(task1, false);
                }
            }
        }
    }
    /*****************************************    END OF CODE     *******************************************/

}