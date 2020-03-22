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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_amount);
        getViews();
        setAmounts();
    }

    private void getViews(){
        colonesAmountLabel = findViewById(R.id.colonesAmountLabel);
        dollarsAmountLabel = findViewById(R.id.dollarsAmountLabel);
    }

    private void setAmounts(){
        SQLiteDatabase dataBaseReader = getDataBaseReader(this);
        Cursor entriesCursor = dataBaseReader.rawQuery(SUM_OF_ENTRIES_QUERY, null);
        Cursor expendituresCursor = dataBaseReader.rawQuery(SUM_OF_EXPENDITURES_QUERY, null);
        setLabels(entriesCursor, expendituresCursor);
        entriesCursor.close();
        expendituresCursor.close();
        dataBaseReader.close();
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
