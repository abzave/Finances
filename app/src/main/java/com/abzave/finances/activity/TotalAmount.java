package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.abzave.finances.R;
import com.abzave.finances.dataBase.IDataBaseConnection;

public class TotalAmount extends AppCompatActivity implements IDataBaseConnection {

    private TextView colonesAmountLabel;
    private TextView dollarsAmountLabel;
    private String context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_amount);
        context = getIntent().getStringExtra(CONTEXT);
        getViews();
        setAmounts();
    }

    private void getViews(){
        colonesAmountLabel = findViewById(R.id.colonesAmountLabel);
        dollarsAmountLabel = findViewById(R.id.dollarsAmountLabel);
    }

    private void setAmounts(){
        SQLiteDatabase dataBaseReader = getDataBaseReader(this);
        if (context.equals(TOTAL_AMOUNT_CONTEXT)){
            requestDifference(dataBaseReader);
        }else {
            requestData(dataBaseReader);
        }
        dataBaseReader.close();
    }

    private void requestDifference(SQLiteDatabase dataBaseReader){
        Cursor entriesCursor = dataBaseReader.rawQuery(SUM_OF_ENTRIES_QUERY, null);
        Cursor expendituresCursor = dataBaseReader.rawQuery(SUM_OF_EXPENDITURES_QUERY, null);
        setLabels(entriesCursor, expendituresCursor);
        entriesCursor.close();
        expendituresCursor.close();
    }

    private void requestData(SQLiteDatabase dataBaseReader){
        Cursor cursor = dataBaseReader.rawQuery(getContextQuery(), null);
        setLabels(cursor);
        cursor.close();
    }

    private String getContextQuery(){
        return SUM_OF_EXPENDITURES_QUERY;
    }

    private void setLabels(Cursor entriesCursor, Cursor expendituresCursor){
        Cursor entry = entriesCursor.moveToFirst() ? entriesCursor : null;
        Cursor expenditure = expendituresCursor.moveToFirst() ? expendituresCursor : null;
        double difference = getDifference(entry, expenditure);
        colonesAmountLabel.setText(String.format(MONEY_FORMAT, difference));
        entry = entry != null && entriesCursor.moveToNext() ? entriesCursor : null;
        expenditure = expenditure != null && expendituresCursor.moveToNext() ? expendituresCursor : null;
        difference = getDifference(entry, expenditure);
        dollarsAmountLabel.setText(String.format(MONEY_FORMAT, difference));
    }

    private void setLabels(Cursor cursor){
        Cursor information = cursor.moveToFirst() ? cursor : null;
        colonesAmountLabel.setText(String.format(MONEY_FORMAT, getCursorValue(information)));
        information = information != null && information.moveToNext() ? information : null;
        dollarsAmountLabel.setText(String.format(MONEY_FORMAT, getCursorValue(information)));
    }

    private Double getCursorValue(Cursor cursor){
        return cursor != null ? cursor.getDouble(SUM_COLUMN) : 0;
    }

    private double getDifference(Cursor entriesCursor, Cursor expendituresCursor){
        double entries = 0;
        double expenditures = 0;
        if (entriesCursor != null){
            entries = entriesCursor.getDouble(SUM_COLUMN);
        }
        if (expendituresCursor != null){
            expenditures = expendituresCursor.getDouble(SUM_COLUMN);
        }
        return entries - expenditures;
    }

}
