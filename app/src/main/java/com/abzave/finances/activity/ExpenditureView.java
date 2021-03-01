package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abzave.finances.R;
import com.abzave.finances.model.Expenditure;
import com.abzave.finances.model.QueryModel;
import com.abzave.finances.model.database.IDataBaseConnection;

import java.util.ArrayList;

public class ExpenditureView extends AppCompatActivity implements IDataBaseConnection {

    private LinearLayout layout;
    private TextView baseLabel;
    private boolean isEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure_view);
        getUiElements();
        loadExpenditures();
    }

    private void getUiElements(){
        layout = findViewById(R.id.descriptionsLayout);
        baseLabel = findViewById(R.id.baseLabel);
        isEntry = getIntent().getBooleanExtra(ENTRY_STRING, IS_ENTRY);
    }

    private void loadExpenditures(){
        SQLiteDatabase dataBaseReader = getDataBaseReader(this);
        ArrayList<ArrayList<Object>> records = getList(dataBaseReader);
        if(records.isEmpty()){
            baseLabel.setText(NO_REGISTERS_MESSAGE);
            return;
        }
        addElements(records);
        layout.removeView(baseLabel);
    }

    private ArrayList<ArrayList<Object>> getList(SQLiteDatabase dataBaseReader){
        ArrayList<ArrayList<Object>> records = new ArrayList<>();

        if (!this.isEntry) {
            String selection = "amount, description, type";
            String joins = "INNER JOIN CurrencyType on Expenditure.currency = CurrencyType.id";

            QueryModel query = Expenditure.Companion.select(selection).join(joins);

            ArrayList<Object> amounts = query.get(this, "amount");
            ArrayList<Object> descriptions = query.get(this, "description");
            ArrayList<Object> types = query.get(this, "type");

            for (int recordIndex = 0; recordIndex < amounts.size(); recordIndex++) {
                ArrayList<Object> row = new ArrayList<>();

                row.add(amounts.get(recordIndex));
                row.add(descriptions.get(recordIndex));
                row.add(types.get(recordIndex));

                records.add(row);
            }
        } else {
            Cursor cursor = dataBaseReader.rawQuery(ENTRIES_WITH_CURRENCY_QUERY, NO_SELECTION_ARGUMENTS);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                ArrayList<Object> row = new ArrayList<>();

                row.add(cursor.getFloat(AMOUNT_COLUMN));
                row.add(cursor.getString(EXPENDITURES_DESCRIPTION_COLUMN));
                row.add(cursor.getString(CURRENCY_COLUMN));

                records.add(row);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return records;
    }

    @SuppressLint("DefaultLocale")
    private void addElements(ArrayList<ArrayList<Object>> records){
        TextView elementLabel;
        String message;

        for (ArrayList<Object> record : records) {
            String description = (String) record.get(EXPENDITURES_DESCRIPTION_COLUMN);
            String currency = (String) record.get(CURRENCY_COLUMN);
            float amount = (Float) record.get(AMOUNT_COLUMN);

            message = String.format("%s: %s %s", description, String.format(MONEY_FORMAT, amount), currency);
            elementLabel = new TextView(getApplicationContext());
            elementLabel.setText(message);
            elementLabel.setTextColor(Color.WHITE);
            layout.addView(elementLabel);
        }
    }

}
