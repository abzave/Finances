package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.abzave.finances.R;
import com.abzave.finances.dataBase.IDataBaseConnection;

public class AddEntry extends AppCompatActivity implements IDataBaseConnection {

    private EditText amountEdit;
    private EditText descriptionEdit;
    private RadioButton colonesButton;
    private Boolean isEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        amountEdit = findViewById(R.id.amountEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
        colonesButton = findViewById(R.id.colonesButton);
        isEntry = getIntent().getBooleanExtra(ENTRY_STRING, IS_ENTRY);
        Toast.makeText(this, getTable(), Toast.LENGTH_SHORT).show();
    }

    public void add(View view){
        SQLiteDatabase dataBaseWriter = getDataBaseWriter(this);
        String amount = amountEdit.getText().toString();
        String description = descriptionEdit.getText().toString();
        int currency = getId(dataBaseWriter, CURRENCY_TYPE_QUERY, getCheckedButton());
        if(amount.isEmpty() || description.isEmpty()){
            Toast.makeText(this, IMCOMPLETE_CONTENT_MESSAGE, Toast.LENGTH_SHORT).show();
            dataBaseWriter.close();
            return;
        }
        long returnedId = insertData(dataBaseWriter, amount, description, currency);
        if(returnedId != NO_INSERTION && getTable().equals(ENTRY_TABLE)){
            addReserves(dataBaseWriter, returnedId, amount);
        }
        dataBaseWriter.close();
        clearFields();
    }

    private long insertData(SQLiteDatabase database, String amount, String description, int currency){
        ContentValues values = new ContentValues();
        values.put(AMOUNT, amount);
        values.put(DESCRIPTION, description);
        values.put(CURRENCY, currency);
        String table = getTable();
        return validateInsertion(database.insert(table, NO_NULL_COLUMNS, values));
    }

    private String getCheckedButton(){
        return colonesButton.isChecked() ? COLONES : DOLLARS;
    }

    private String getTable(){
        return isEntry ? ENTRY_TABLE : EXPENDITURE_TABLE;
    }

    private long validateInsertion(long returnedId){
        if(returnedId == NO_INSERTION){
            Toast.makeText(this, INSERTION_FAILED_MESSAGE, Toast.LENGTH_SHORT).show();
        }
        return returnedId;
    }

    private void addReserves(SQLiteDatabase database, long entry, String amount){
        double entryAmount = Double.valueOf(amount);
        double retirement = entryAmount * RETIREMENT_PERCENTAGE;
        double emergencies = entryAmount * EMERGENCIES_PERCENTAGE;
        double whims = entryAmount * WHIMS_PERCENTAGE;
        insertReserve(database, entry, retirement, RETIREMENT);
        insertReserve(database, entry, emergencies, EMERGENCIES);
        insertReserve(database, entry, whims, WHIMS);
    }

    private void insertReserve(SQLiteDatabase database, long entry, double amount, String type){
        ContentValues values = new ContentValues();
        values.put(TYPE, getId(database, RESERVE_TYPE_QUERY, type));
        values.put(AMOUNT, amount);
        values.put(ENTRY_STRING, entry);
        validateInsertion(database.insert(RESERVE_TABLE, NO_NULL_COLUMNS, values));
    }

    private void clearFields(){
        amountEdit.setText("");
        descriptionEdit.setText("");
    }

}
