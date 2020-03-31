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
        if (context.equals(REMAINING_CONTEXT)){
            requestDifference(dataBaseReader);
        }else {
            requestData(dataBaseReader);
        }
        dataBaseReader.close();
    }

    private void requestDifference(SQLiteDatabase dataBaseReader){
        Cursor entriesCursor = dataBaseReader.rawQuery(SUM_OF_ENTRIES_QUERY, NO_SELECTION_ARGUMENTS);
        Cursor expendituresCursor = dataBaseReader.rawQuery(SUM_OF_EXPENDITURES_QUERY, NO_SELECTION_ARGUMENTS);
        setLabels(entriesCursor, expendituresCursor);
        entriesCursor.close();
        expendituresCursor.close();
    }

    private void requestData(SQLiteDatabase dataBaseReader){
        Cursor cursor = dataBaseReader.rawQuery(getContextQuery(), getContextParameters(dataBaseReader));
        setLabels(cursor, NO_CURSOR);
        cursor.close();
    }

    private String getContextQuery(){
        switch (context){
            case TOTAL_AMOUNT_CONTEXT:
                return SUM_OF_ENTRIES_QUERY;
            case EXPENDED_CONTEXT:
                return SUM_OF_EXPENDITURES_QUERY;
            case RETIREMENT_CONTEXT:
            case EMERGENCIES_CONTEXT:
            case WHIMS_CONTEXT:
                return SUM_OF_RESERVE_QUERY;
            default:
                return null;
        }
    }

    private String[] getContextParameters(SQLiteDatabase database){
        String reserveType;
        switch (context){
            case RETIREMENT_CONTEXT:
                reserveType = RETIREMENT;
                break;
            case EMERGENCIES_CONTEXT:
                reserveType = EMERGENCIES;
                break;
            case WHIMS_CONTEXT:
                reserveType = WHIMS;
                break;
            default:
                return NO_SELECTION_ARGUMENTS;
        }
        return new String[]{Long.toString(getId(database, RESERVE_TYPE_QUERY, reserveType))};
    }

    private void setLabels(Cursor entriesCursor, Cursor expendituresCursor){
        Cursor entry = entriesCursor != null && entriesCursor.moveToFirst() ? entriesCursor : null;
        Cursor expenditure = expendituresCursor != null && expendituresCursor.moveToFirst() ? expendituresCursor : null;
        double difference = getDifference(entry, expenditure);
        colonesAmountLabel.setText(String.format(MONEY_FORMAT, difference));
        entry = entry != null && entriesCursor.moveToNext() ? entriesCursor : null;
        expenditure = expenditure != null && expendituresCursor.moveToNext() ? expendituresCursor : null;
        difference = getDifference(entry, expenditure);
        dollarsAmountLabel.setText(String.format(MONEY_FORMAT, difference));
    }

    private Double getCursorValue(Cursor cursor){
        return cursor != null ? cursor.getDouble(SUM_COLUMN) : 0;
    }

    private double getDifference(Cursor entriesCursor, Cursor expendituresCursor){
        double entries = getCursorValue(entriesCursor);
        double expenditures = getCursorValue(expendituresCursor);
        return entries - expenditures;
    }

}
