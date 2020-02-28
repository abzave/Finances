package com.abzave.finances;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class AddEntry extends AppCompatActivity implements IConstants{

    EditText amountEdit;
    EditText descriptionEdit;
    RadioButton colonesButton;
    RadioButton dollarsButton;
    Boolean isEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        amountEdit = findViewById(R.id.amountEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
        colonesButton = findViewById(R.id.colonesButton);
        dollarsButton = findViewById(R.id.dollarsButton);
        isEntry = getIntent().getBooleanExtra(ENTRY_STRING, true);
    }

    public void add(View view){
        DataBase dataBase = new DataBase(this, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase dataBaseWriter = dataBase.getWritableDatabase();
        String amount = amountEdit.getText().toString();
        String description = descriptionEdit.getText().toString();
        if(amount.isEmpty() || description.isEmpty()){
            Toast.makeText(this, IMCOMPLETE_CONTENT_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
    }

}
