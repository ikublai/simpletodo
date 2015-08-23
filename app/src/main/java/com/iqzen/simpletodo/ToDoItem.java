package com.iqzen.simpletodo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Teddy on 8/19/2015.
 */
public class ToDoItem implements Serializable {
    public String Description;
    public Date DueDate;

    public ToDoItem(String description, Date dueDate) {
        this.Description = description;
        this.DueDate = dueDate;
    }

    public ToDoItem() {
    }
}