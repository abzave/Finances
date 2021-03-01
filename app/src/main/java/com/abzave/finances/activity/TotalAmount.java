package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.abzave.finances.R;
import com.abzave.finances.model.Expenditure;
import com.abzave.finances.model.QueryModel;
import com.abzave.finances.model.database.IDataBaseConnection;

import java.util.ArrayList;
import java.util.HashMap;

public class TotalAmount extends AppCompatActivity implements IDataBaseConnection {

    private TextView colonesAmountLabel;
    private TextView dollarsAmountLabel;
    private short context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_amount);
        context = getIntent().getShortExtra(CONTEXT, TOTAL_AMOUNT_CONTEXT);
        getViews();
        setAmounts();
    }

    private void getViews(){
        colonesAmountLabel = findViewById(R.id.colonesAmountLabel);
        dollarsAmountLabel = findViewById(R.id.dollarsAmountLabel);
    }

    private void setAmounts(){
        SQLiteDatabase dataBaseReader = getDataBaseReader(this);
        if (context == REMAINING_CONTEXT){
            requestDifference(dataBaseReader);
        }else {
            requestData(dataBaseReader);
        }
        dataBaseReader.close();
    }

    private void requestDifference(SQLiteDatabase dataBaseReader){
        ArrayList<String> columns = new ArrayList<>();
        columns.add("amount");

        QueryModel expendituresQuery = Expenditure.Companion.sum(columns).group("currency");
        ArrayList<Float> expenditures = expendituresQuery.get(this, "amount");

        Cursor entriesCursor = dataBaseReader.rawQuery(SUM_OF_ENTRIES_QUERY, NO_SELECTION_ARGUMENTS);
        setLabels(entriesCursor, expenditures);
        entriesCursor.close();
    }

    private void requestData(SQLiteDatabase dataBaseReader){
        Cursor cursor = null;
        ArrayList<Float> expenditures = new ArrayList<>();
        if (context == EXPENDED_CONTEXT) {
            ArrayList<String> columns = new ArrayList<>();
            columns.add("amount");

            QueryModel expendituresQuery = Expenditure.Companion.sum(columns).group("currency");
            expenditures = expendituresQuery.get(this, "amount");

        } else {
            cursor = dataBaseReader.rawQuery(getContextQuery(), getContextParameters(dataBaseReader));
        }
        setLabels(cursor, expenditures);
        if (cursor != null) {
            cursor.close();
        }
    }

    private String getContextQuery(){
        switch (context){
            case TOTAL_AMOUNT_CONTEXT:
                return SUM_OF_ENTRIES_QUERY;
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

    @SuppressLint("DefaultLocale")
    private void setLabels(Cursor entriesCursor, ArrayList<Float> expenditures){
        Cursor entry = entriesCursor != null && entriesCursor.moveToFirst() ? entriesCursor : null;
        ArrayList<Float> expenditure = !expenditures.isEmpty() ? expenditures : null;

        float difference = getDifference(entry, expenditure);
        colonesAmountLabel.setText(String.format(MONEY_FORMAT, difference));
        entry = entry != null && entriesCursor.moveToNext() ? entriesCursor : null;
        difference = getDifference(entry, expenditure);
        dollarsAmountLabel.setText(String.format(MONEY_FORMAT, difference));
    }

    private Float getCursorValue(Cursor cursor){
        return cursor != null ? cursor.getFloat(SUM_COLUMN) : 0;
    }

    private Float popListValue(ArrayList<Float> list){
        float value = 0f;

        if (list != null && !list.isEmpty()) {
            value = list.get(0);
            list.remove(0);
        }

        return value;
    }

    private float getDifference(Cursor entriesCursor, ArrayList<Float> expendituresList){
        float entries = getCursorValue(entriesCursor);
        float expenditures = popListValue(expendituresList);
        return entries - expenditures;
    }

}
