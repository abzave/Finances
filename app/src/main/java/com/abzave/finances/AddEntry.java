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
        isEntry = getIntent().getBooleanExtra(ENTRY_STRING, IS_ENTRY);
        Toast.makeText(this, getTable(), Toast.LENGTH_SHORT).show();
    }

    public void add(View view){
        DataBase dataBase = new DataBase(this, DATABASE_NAME, CURSOR_FACTORY, DATABASE_VERSION);
        SQLiteDatabase dataBaseWriter = dataBase.getWritableDatabase();
        String amount = amountEdit.getText().toString();
        String description = descriptionEdit.getText().toString();
        int currency = getCurrencyId(dataBaseWriter);
        if(amount.isEmpty() || description.isEmpty()){
            Toast.makeText(this, IMCOMPLETE_CONTENT_MESSAGE, Toast.LENGTH_SHORT).show();
            dataBaseWriter.close();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(AMOUNT, amount);
        values.put(DESCRIPTION, description);
        values.put(CURRENCY, currency);
        String table = getTable();
        validateInsertion(dataBaseWriter.insert(table, NO_NULL_COLUMNS, values));
        dataBaseWriter.close();
        clearFields();
    }

    private int getCurrencyId(SQLiteDatabase dataBase){
        String currency = colonesButton.isChecked() ? COLONES : DOLLARS;
        Cursor cursor = dataBase.rawQuery(CURRENCY_TYPE_QUERY + "\"" + currency + "\"", null);
        cursor.moveToFirst();
        int id = cursor.getInt(ID_COLUMN);
        cursor.close();
        return id;
    }

    private String getTable(){
        return isEntry ? ENTRY_TABLE : EXPENDITURE_TABLE;
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
