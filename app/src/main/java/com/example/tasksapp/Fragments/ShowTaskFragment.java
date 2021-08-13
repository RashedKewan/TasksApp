package com.example.tasksapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.tasksapp.R;
import com.example.tasksapp.Utils.MainViewModel;


public class ShowTaskFragment extends Fragment {

    private IShowTaskFragmentListener listener;
    private TextView text;
    private MainViewModel myViewModel;

    public ShowTaskFragment() {
    }

    public static ShowTaskFragment newInstance() {
        ShowTaskFragment fragment = new ShowTaskFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_show_task, container, false);
        text = v.findViewById(R.id.show_task_txt);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myViewModel = MainViewModel.getInstance(getActivity().getApplication(), getContext(), getActivity());
        myViewModel.getItemSelected().observe(getViewLifecycleOwner(), task -> text.setText(task.getTaskData()));
    }


    public void onAttach(@NonNull Context context) {
        try {
            this.listener = (IShowTaskFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the class " + getActivity().getClass().getName() + " must implements the interface 'IOperandsFragment'");
        }
        super.onAttach(context);
    }




                  /*        MENU         */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                listener.editTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*         INTERFACE         */
    public interface IShowTaskFragmentListener {
        void editTask();
    }


    /*****************************************    END OF CODE     *******************************************/
}