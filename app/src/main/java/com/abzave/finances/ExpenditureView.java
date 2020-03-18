package com.abzave.finances;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpenditureView extends AppCompatActivity implements IConstants{

    LinearLayout layout;
    TextView baseLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure_view);
        getUiElements();
        loadExpenditures();
    }

    private void getUiElements(){
        layout = findViewById(R.id.layout);
        baseLabel = findViewById(R.id.baseLabel);
    }

    private void loadExpenditures(){
        SQLiteDatabase dataBaseReader = getDataBase();
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

    private SQLiteDatabase getDataBase(){
        DataBase dataBase = new DataBase(this, DATABASE_NAME, CURSOR_FACTORY, DATABASE_VERSION);
        return dataBase.getReadableDatabase();
    }

    private Cursor getExpenditures(SQLiteDatabase dataBaseReader){
        return dataBaseReader.rawQuery(EXPENDITURES_QUERY, null);
    }

    private void addElements(Cursor cursor){
        TextView elementLabel;
        String message;
        while (!cursor.isAfterLast()){
            message = cursor.getString(DESCRIPTION_COLUMN) + ": " + cursor.getString(AMOUNT_COLUMN) + " " + cursor.getString(CURRENCY_COLUMN);
            elementLabel = new TextView(getApplicationContext());
            elementLabel.setText(message);
            layout.addView(elementLabel);
            cursor.moveToNext();
        }
    }

}
