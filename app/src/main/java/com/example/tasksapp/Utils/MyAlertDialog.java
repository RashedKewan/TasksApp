package com.example.tasksapp.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyAlertDialog extends DialogFragment {
    private final static String TITLE_KEY = "title";
    private final static String ARE_YOU_SURE = "Are you sure?";
    private final static String YES = "Yes";
    private final static String NO = "No";


    private IMyAlertDialogListener listener;

    public MyAlertDialog() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try {
            this.listener = (IMyAlertDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the class " +
                    getActivity().getClass().getName() +
                    " must implements the interface 'IMyAlertDialogListener'");
        }
        super.onAttach(context);
    }

    public static MyAlertDialog newInstance(String title) {
        MyAlertDialog frag = new MyAlertDialog();
        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE_KEY);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(ARE_YOU_SURE)
                .setPositiveButton(YES, (dialog, whichButton) ->
                        listener.onPositiveButton(dialog, whichButton))
                .setNegativeButton(NO, (dialog, whichButton) ->
                        listener.onNegativeButton(dialog, whichButton));
        return alertDialogBuilder.create();
    }

    public interface IMyAlertDialogListener {
        void onPositiveButton(DialogInterface dialog, int whichButton);

        void onNegativeButton(DialogInterface dialog, int whichButton);
    }
}
