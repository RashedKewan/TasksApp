package com.example.tasksapp.Models;


import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Objects;


@IgnoreExtraProperties
public class Task implements Serializable {
    private String id ;
    private Date date;
    private String time;
    private String taskData;
    private String subject;
    private boolean remember = false;

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Task() {
    }

    public Task(String subject, String time, Date date, String taskData) {
        this.date = date;
        this.time = time;
        this.taskData = taskData;
        this.subject = subject;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTaskData() {
        return taskData;
    }

    public void setTaskData(String taskData) {
        this.taskData = taskData;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "\nTaskData{\n" +
                "subject = " + subject + '\'' +
                "Time = " + time + '\'' +
                "date = " + date.toString() +
                "body = " + taskData + '\''
                ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return remember == task.remember &&
                Objects.equals(id, task.id) &&
                Objects.equals(date, task.date) &&
                Objects.equals(time, task.time) &&
                Objects.equals(taskData, task.taskData) &&
                Objects.equals(subject, task.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, time, taskData, subject, remember);
    }
}
