package com.example.anshuman.todo_revised;

/**
 * Created by anshuman on 12-02-2017.
 */
public class tasks {

    private int _id;
    private String _taskname;

    public tasks(String taskname) {
        this._taskname = taskname;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_taskname(String _taskname) {
        this._taskname = _taskname;
    }

    public int get_id() {
        return _id;
    }

    public String get_taskname() {
        return _taskname;
    }
}
