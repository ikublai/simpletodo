package com.iqzen.simpletodo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import java.io.IOException;
import java.util.ArrayList;

// Main activity that displays the Todos and basic add functionality
public class MainActivity extends ActionBarActivity {
    private ArrayList<String> listItems;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private String todoFileName = "todo2.txt";
    private final int RESULT_CODE = 200;
    private final int REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set reference and bind the array
        this.lvItems = (ListView) findViewById(R.id.lvItems);
        this.listItems = new ArrayList<String>();
        this.readItems();
        this.itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.listItems);
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
            int pos = data.getExtras().getInt("position", 0);
            // Toast the name to display temporarily on screen
            Toast.makeText(this, newText, Toast.LENGTH_SHORT).show();

            if (this.listItems != null && this.listItems.size() > 0 && pos < this.listItems.size()) {
                this.listItems.set(pos, newText);
                this.itemsAdapter.notifyDataSetChanged();
                this.writeItems();
            }
        }
    }

    /// add item handler and reset editText text
    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        this.itemsAdapter.add(itemText);
        etNewItem.setText("");
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

                String selectedText = ((TextView) view).getText().toString();
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();

                Intent editItent = new Intent(MainActivity.this, EditItemActivity.class);
                editItent.putExtra("edittext", selectedText);
                editItent.putExtra("index", position);
                editItent.putExtra("id", id);
                startActivityForResult(editItent, REQUEST_CODE);
            }
        });
    }

    // read the items from the file
    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, this.todoFileName);
        try {
            this.listItems = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            this.listItems = new ArrayList<String>();
        }
    }

    // save the list to the file
    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, this.todoFileName);
        try {
            FileUtils.writeLines(todoFile, this.listItems);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
