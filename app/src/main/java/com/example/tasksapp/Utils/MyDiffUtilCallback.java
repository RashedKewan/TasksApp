package com.example.tasksapp.Utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.tasksapp.Models.Task;

import java.util.ArrayList;

public class MyDiffUtilCallback extends DiffUtil.Callback {
    private ArrayList<Task> oldList;
    private ArrayList<Task> newList;

    public MyDiffUtilCallback(ArrayList<Task> oldList, ArrayList<Task> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList == newList;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }
}
