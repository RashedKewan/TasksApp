package com.example.tasksapp.Utils;

import android.util.Log;

import com.example.tasksapp.Models.Task;

import java.util.Comparator;

import static android.content.ContentValues.TAG;

public class TaskComparator implements Comparator<Task> {
    /**      SORT BY DATE USING COMPARATOR INTERFACE     **/
    @Override
    public int compare(Task t1, Task t2) {
        int t1Year = Integer.parseInt(t1.getDate().getYear());
        int t2Year = Integer.parseInt(t2.getDate().getYear());

        if (t1Year == t2Year) {
            int t1Month = Integer.parseInt(t1.getDate().getMonth());
            int t2Month = Integer.parseInt(t2.getDate().getMonth());

            if (t1Month == t2Month) {
                int t1Day = Integer.parseInt(t1.getDate().getDay());
                int t2Day = Integer.parseInt(t2.getDate().getDay());

                if (t1Day == t2Day) {
                    String[] time1 = t1.getTime().split(" : ");
                    String[] time2 = t2.getTime().split(" : ");
                    int t1Hour = Integer.parseInt(time1[0]);
                    int t2Hour = Integer.parseInt(time2[0]);
                    if (t1Hour == t2Hour) {
                        int t1Min = Integer.parseInt(time1[1]);
                        int t2Min = Integer.parseInt(time2[1]);

                        if (t1Min == t2Min) {
                            return 0;
                        }
                        return t1Min > t2Min ? 1 : -1;
                    }
                    return t1Hour > t2Hour ? 1 : -1;

                }
                return t1Day > t2Day ? 1 : -1;
            }
            return t1Month > t2Month ? 1 : -1;
        }
        return t1Year > t2Year ? 1 : -1;
    }
}
