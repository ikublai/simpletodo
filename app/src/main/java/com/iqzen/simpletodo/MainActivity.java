package com.iqzen.simpletodo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Main activity that displays the Todos and basic add functionality
public class MainActivity extends ActionBarActivity {
    private ArrayList<ToDoItem> listItems;
    //private ArrayAdapter<String> itemsAdapter;
    private ToDoItemAdapter itemsAdapter;
    private ListView lvItems;
    private String todoFileName = "todowithduedate2.txt";
    private final int RESULT_CODE = 200;
    private final int REQUEST_CODE = 10;
    private SimpleDateFormat dataFormat = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set reference and bind the array
        this.lvItems = (ListView) findViewById(R.id.lvItems);
        this.listItems = new ArrayList<ToDoItem>();
        Log.d("MainActivity.onCreate", "About to read items");
        this.readItems();
        this.itemsAdapter = new ToDoItemAdapter(this, this.listItems);
        this.lvItems.setAdapter(this.itemsAdapter);
        this.setupListViewListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Toast.makeText(this, "In onActivityResult " + resultCode + ":" + requestCode, Toast.LENGTH_LONG).show();

        // If the requestCode is 10 REQUEST_CODE and resultCode is RESULT_CODE,
        // update the list view
        if (resultCode == RESULT_CODE && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String newText = data.getExtras().getString("newtext");
            String newDueDate = data.getExtras().getString("duedate");
            int pos = data.getExtras().getInt("position", 0);
            // Toast the name to display temporarily on screen
            Toast.makeText(this, newText, Toast.LENGTH_SHORT).show();

            Date dueDate = this.parseDate(newDueDate, new Date());
            ToDoItem toDoItem = new ToDoItem(newText, dueDate);

            if (this.listItems != null && this.listItems.size() > 0 && pos < this.listItems.size()) {
                this.listItems.set(pos, toDoItem);
                this.itemsAdapter.notifyDataSetChanged();
                this.writeItems();
            }
        }
    }

    /// add item handler and reset editText text
    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        Date defaultDate = new Date();

        Date dueDate = defaultDate;
        EditText etDueDate = (EditText) findViewById(R.id.etDueDate);
        String dueDateString = etDueDate.getText().toString();

        if (!dueDateString.isEmpty()) {
            try {
                dueDate = this.dataFormat.parse(dueDateString);
            } catch (ParseException e) {
                e.printStackTrace();
                dueDate = defaultDate;
            }
        }

        ToDoItem todoItem = new ToDoItem(itemText, dueDate);

        if (this.itemsAdapter == null) {
            this.listItems = new ArrayList<ToDoItem>();
            this.itemsAdapter = new ToDoItemAdapter(this, this.listItems);
        }
        this.itemsAdapter.add(todoItem);
        //Log.d("MainActivity", Integer.toString(this.listItems.size()));
        etNewItem.setText("");
        etDueDate.setText("");
        this.writeItems();
    }

    private void setupListViewListener() {
        this.lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                listItems.remove(position);
                // refresh the list
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });

        this.lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //convertView = inflater.inflate(R.layout.todo_item, parent, false);
                TextView description = (TextView) view.findViewById(R.id.txtDescription);
                TextView dueDate = (TextView) view.findViewById(R.id.txtDueDate);

                String selectedText = description.getText().toString();
                String selectedDueDate = dueDate.getText().toString();

                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(), selectedText, Toast.LENGTH_SHORT).show();

                Intent editItent = new Intent(MainActivity.this, EditItemActivity.class);
                editItent.putExtra("edittext", selectedText);
                editItent.putExtra("duedate", selectedDueDate);
                editItent.putExtra("index", position);
                editItent.putExtra("id", id);
                startActivityForResult(editItent, REQUEST_CODE);
            }
        });
    }

    // read the list of todos
    private void readItems() {
        ObjectInputStream objectinputstream = null;
        FileInputStream streamIn = null;
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, this.todoFileName);

        try {
            streamIn = new FileInputStream(todoFile);
            objectinputstream = new ObjectInputStream(streamIn);
            ArrayList<ToDoItem> todoItems = (ArrayList<ToDoItem>) objectinputstream.readObject();
            this.listItems = todoItems;
            Log.d("MaingActivity", "Read:" + Integer.toString(todoItems.size()));
            objectinputstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // save the list of todos
    private void writeItems() {
        File filesDir = getFilesDir();
        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        File todoFile = new File(filesDir, this.todoFileName);

        try{
            fout = new FileOutputStream(todoFile, false);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(this.listItems);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    // read the items from the file
//    private void readItems2() {
//        File filesDir = getFilesDir();
//
//        try {
//            File todoFile = new File(filesDir, this.todoFileName);
//            List<ToDoItem> results= FileUtils.readLines(todoFile);
//            Log.d("readItems", "Read file " + results.size());
//            this.listItems = new ArrayList<ToDoItem>(results); //(FileUtils.readLines(todoFile));
//        } catch (IOException e) {
//            this.listItems = new ArrayList<ToDoItem>();
//        } catch (Exception ex) {
//            this.listItems = new ArrayList<ToDoItem>();
//        }
//    }
//
//    // save the list to the file
//    private void writeItems2() {
//        File filesDir = getFilesDir();
//        try {
//            File todoFile = new File(filesDir, this.todoFileName);
//            FileUtils.writeLines(todoFile, this.listItems);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    // get the due date. Use the supplied default date if dateString cannot be parsed
    private Date parseDate(String dateString, Date defaultDate) {
        Date newDate = defaultDate;
        if (dateString != null && !dateString.isEmpty()){
            try {
                newDate = this.dataFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                newDate = defaultDate;
            }
        }

        return newDate;
    }
}
