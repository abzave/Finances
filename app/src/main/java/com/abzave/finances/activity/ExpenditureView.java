package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abzave.finances.R;
import com.abzave.finances.dataBase.IDataBaseConnection;

public class ExpenditureView extends AppCompatActivity implements IDataBaseConnection {

    private LinearLayout layout;
    private TextView baseLabel;

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
    }

    private void loadExpenditures(){
        SQLiteDatabase dataBaseReader = getDataBaseReader(this);
        Cursor cursor = getExpenditures(dataBaseReader);
        if(!cursor.moveToFirst()){
            baseLabel.setText(NO_REGISTERS_MESSAGE);
            cursor.close();
            return;
        }
        addElements(cursor);
        cursor.close();
        layout.removeView(baseLabel);
    }

    private Cursor getExpenditures(SQLiteDatabase dataBaseReader){
        return dataBaseReader.rawQuery(EXPENDITURES_QUERY, null);
    }

    private void addElements(Cursor cursor){
        TextView elementLabel;
        String message;
        while (!cursor.isAfterLast()){
            message = cursor.getString(DESCRIPTION_COLUMN) + ": " + String.format(MONEY_FORMAT, cursor.getDouble(AMOUNT_COLUMN)) + " " + cursor.getString(CURRENCY_COLUMN);
            elementLabel = new TextView(getApplicationContext());
            elementLabel.setText(message);
            elementLabel.setTextColor(Color.WHITE);
            layout.addView(elementLabel);
            cursor.moveToNext();
        }
    }

}
