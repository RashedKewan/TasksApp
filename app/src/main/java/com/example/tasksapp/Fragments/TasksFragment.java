package com.example.tasksapp.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.tasksapp.R;
import com.example.tasksapp.Utils.MainViewModel;
import com.example.tasksapp.Utils.TasksAdapter;
import com.example.tasksapp.Interfaces.IDialog;

import java.io.IOException;

public class TasksFragment extends Fragment {
    public static final int EXIT_CODE = 1;
    private TasksAdapter adapter;
    private ITasksFragmentListener listener;
    private IDialog dialogListener;
    private MainViewModel viewModel;


    public TasksFragment() {
    }

    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.listener = (ITasksFragmentListener) context;
            this.dialogListener = (IDialog) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the class " + getActivity().getClass().getName() + " must implements the interface 'IOperandsFragment'");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        viewModel = MainViewModel.getInstance(getActivity().getApplication(), getContext(), getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new TasksAdapter(getActivity().getApplication(), getActivity(), getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager((new LinearLayoutManager(getContext())));
        recyclerView.setAdapter(adapter);

        super.onViewCreated(view, savedInstanceState);
    }


    /*         MENU         */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                listener.addTask();
                return true;
            case R.id.menu_settings:
                listener.openSettings();
                return true;
            case R.id.menu_exit:
                dialogListener.popUpDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*         INTERFACE         */
    public interface ITasksFragmentListener {
        void addTask();

        void openSettings();
    }


/*****************************************    END OF CODE     *******************************************/

}