package com.abzave.finances;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
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
        int currency = getCurrencyId(dataBaseWriter);
        if(amount.isEmpty() || description.isEmpty()){
            Toast.makeText(this, IMCOMPLETE_CONTENT_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(AMOUNT, amount);
        values.put(DESCRIPTION, description);
        values.put(CURRENCY, currency);
        validateInsertion(dataBaseWriter.insert(ENTRY_TABLE, NO_NULL_COLUMNS, values));
        dataBaseWriter.close();
        clearFields();
    }

    private int getCurrencyId(SQLiteDatabase dataBase){
        String currency = colonesButton.isChecked() ? COLONES : DOLLARS;
        Cursor cursor = dataBase.rawQuery("select id from CurrencyType where type = \"" + currency + "\"", null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        cursor.close();
        return id;
    }

    private void validateInsertion(long returnedId){
        if(returnedId == -1){
            Toast.makeText(this, INSERTION_FAILED_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields(){
        amountEdit.setText("");
        descriptionEdit.setText("");
    }

}
