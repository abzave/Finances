package com.abzave.finances.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.abzave.finances.R;
import com.abzave.finances.model.CurrencyType;
import com.abzave.finances.model.Entry;
import com.abzave.finances.model.Expenditure;
import com.abzave.finances.model.IBaseModel;
import com.abzave.finances.model.database.IDataBaseConnection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import kotlin.Pair;

public class AddEntry extends AppCompatActivity implements IDataBaseConnection {

    private EditText amountEdit;
    private EditText descriptionEdit;
    private RadioButton colonesButton;
    private Boolean isEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        setupUi();
        Toast.makeText(this, getTable(), Toast.LENGTH_SHORT).show();
    }

    private void setupUi(){
        amountEdit = findViewById(R.id.amountEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
        colonesButton = findViewById(R.id.colonesButton);
        isEntry = getIntent().getBooleanExtra(ENTRY_STRING, IS_ENTRY);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void add(View view){
        SQLiteDatabase dataBaseWriter = getDataBaseWriter(this);
        String amount = amountEdit.getText().toString();
        String description = descriptionEdit.getText().toString();

        Pair<String, ?> currencyQuery = new Pair<>("type", getCheckedButton());
        ArrayList<CurrencyType> types = CurrencyType.Companion.findBy(this, currencyQuery);
        if (types.isEmpty()){
            return;
        }

        int currency = (Integer) types.get(0).get("id");
        if(amount.isEmpty() || description.isEmpty()){
            Toast.makeText(this, IMCOMPLETE_CONTENT_MESSAGE, Toast.LENGTH_SHORT).show();
            dataBaseWriter.close();
            return;
        }
        long returnedId = insertData(amount, description, currency);
        if(returnedId != NO_INSERTION && getTable().equals(ENTRY_TABLE)){
            addReserves(dataBaseWriter, returnedId, amount);
        }
        dataBaseWriter.close();
        clearFields();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long insertData(String amount, String description, int currency){
        HashMap<String, Object> values = new HashMap<>();
        values.put(AMOUNT, amount);
        values.put(DESCRIPTION, description);
        values.put(CURRENCY, currency);
        values.put(DATE, getDate());

        IBaseModel model = isEntry ? new Entry(values) : new Expenditure(values);
        return model.save(this) ? (long)model.get("id") : -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return formatter.format(now);
    }

    private String getCheckedButton(){
        return colonesButton.isChecked() ? COLONES : DOLLARS;
    }

    private String getTable(){
        return isEntry ? ENTRY_TABLE : EXPENDITURE_TABLE;
    }

    private void validateInsertion(long returnedId){
        if(returnedId == NO_INSERTION){
            Toast.makeText(this, INSERTION_FAILED_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    private void addReserves(SQLiteDatabase database, long entry, String amount){
        float entryAmount = Float.valueOf(amount);
        float retirement = entryAmount * RETIREMENT_PERCENTAGE;
        float emergencies = entryAmount * EMERGENCIES_PERCENTAGE;
        float whims = entryAmount * WHIMS_PERCENTAGE;
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
