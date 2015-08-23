package com.iqzen.simpletodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Teddy on 8/19/2015.
 */
public class ToDoItemAdapter extends ArrayAdapter<ToDoItem> {
    private static class ViewHolder {
        TextView description;
        TextView dueDate;
    }

    public ToDoItemAdapter(Context context, ArrayList<ToDoItem> todoItems) {
        super (context, R.layout.todo_item, todoItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the todo item for this position
        ToDoItem todoItem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.todo_item, parent, false);
            viewHolder.description = (TextView) convertView.findViewById(R.id.txtDescription);
            viewHolder.dueDate = (TextView) convertView.findViewById(R.id.txtDueDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.description.setText(todoItem.Description);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        viewHolder.dueDate.setText(df.format(todoItem.DueDate));
        // Return the completed view to render on screen

        return convertView;
    }
}
