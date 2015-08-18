package com.iqzen.simpletodo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class EditItemActivity extends ActionBarActivity {
    private final int RESULT_CODE = 200;
    private final int REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String textToEdit = intent.getStringExtra("edittext");
        final int position = intent.getIntExtra("index", 0);
        int id = intent.getIntExtra("id", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        this.setEditText(textToEdit);

        // set the submit click handler
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etTodoEdit = (EditText) findViewById(R.id.etEditTodoText);
                Intent data = new Intent();
                data.putExtra("newtext", etTodoEdit.getText().toString());
                data.putExtra("position", position);
                setResult(RESULT_CODE, data);
                finish();
            }
        });

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
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

    // set edit text
    private void setEditText(String textToEdit) {
        TextView edTodo = (TextView) findViewById(R.id.etEditTodoText);
        edTodo.setText(textToEdit);
    }
}
