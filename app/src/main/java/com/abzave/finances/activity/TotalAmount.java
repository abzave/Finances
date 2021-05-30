package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.abzave.finances.R;
import com.abzave.finances.model.Entry;
import com.abzave.finances.model.Expenditure;
import com.abzave.finances.model.IBaseModel;
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

        QueryModel entryQuery = Entry.Companion.sum(columns).group("currency");
        ArrayList<Float> entries = entryQuery.get(this, "amount");

        setLabels(entries, expenditures);
    }

    private void requestData(SQLiteDatabase dataBaseReader){
        ArrayList<Float> expenditures = new ArrayList<>();
        ArrayList<Float> entries = new ArrayList<>();
        if (context == EXPENDED_CONTEXT) {
            ArrayList<String> columns = new ArrayList<>();
            columns.add("amount");

            QueryModel expendituresQuery = Expenditure.Companion.sum(columns).group("currency");
            expenditures = expendituresQuery.get(this, "amount");
        } else if (context == TOTAL_AMOUNT_CONTEXT) {
            ArrayList<String> columns = new ArrayList<>();
            columns.add("amount");

            QueryModel expendituresQuery = Entry.Companion.sum(columns).group("currency");
            entries = expendituresQuery.get(this, "amount");
        } else {
            Cursor cursor = dataBaseReader.rawQuery(getContextQuery(), getContextParameters(dataBaseReader));
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                entries.add(cursor.getFloat(AMOUNT_COLUMN));
                cursor.moveToNext();
            }
            cursor.close();
        }
        setLabels(entries, expenditures);
    }

    private String getContextQuery(){
        switch (context){
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
    private void setLabels(ArrayList<Float> entries, ArrayList<Float> expenditures){
        float difference = getDifference(entries, expenditures);
        colonesAmountLabel.setText(String.format(MONEY_FORMAT, difference));
        difference = getDifference(entries, expenditures);
        dollarsAmountLabel.setText(String.format(MONEY_FORMAT, difference));
    }

    private Float popListValue(ArrayList<Float> list){
        float value = 0f;

        if (list != null && !list.isEmpty()) {
            value = list.get(0);
            list.remove(0);
        }

        return value;
    }

    private float getDifference(ArrayList<Float> entriesCursor, ArrayList<Float> expendituresList){
        float entries = popListValue(entriesCursor);
        float expenditures = popListValue(expendituresList);
        return entries - expenditures;
    }

}
