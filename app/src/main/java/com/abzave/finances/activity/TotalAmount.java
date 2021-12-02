package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.abzave.finances.R;
import com.abzave.finances.model.Entry;
import com.abzave.finances.model.Expenditure;
import com.abzave.finances.model.QueryModel;
import com.abzave.finances.model.database.IDataBaseConnection;

import java.util.ArrayList;

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
        if (context == REMAINING_CONTEXT){
            requestDifference();
        }else {
            requestData();
        }
    }

    /**
     * Fetches all the entries and expenditures amount information
     */
    private void requestDifference(){
        ArrayList<String> columns = new ArrayList<>();
        columns.add("amount");

        // Gets all the expenditures amount
        QueryModel expendituresQuery = Expenditure.Companion.sum(columns).group("currency");
        ArrayList<Float> expenditures = expendituresQuery.get(this, "amount");

        // Gets all the entries amount
        QueryModel entryQuery = Entry.Companion.sum(columns).group("currency");
        ArrayList<Float> entries = entryQuery.get(this, "amount");

        // Set the data into the UI
        setLabels(entries, expenditures);
    }

    /**
     * Fetches the required data based on the context
     */
    private void requestData(){
        ArrayList<String> columns = new ArrayList<>();
        columns.add("amount");

        // Fetches the records
        QueryModel expendituresQuery = context == EXPENDED_CONTEXT
                ? Expenditure.Companion.sum(columns).group("currency")
                : Entry.Companion.sum(columns).group("currency");

        // Sets the retrieved information if needed
        ArrayList<Float> expenditures = context == EXPENDED_CONTEXT
                ? expendituresQuery.get(this, "amount")
                : new ArrayList<>();

        // Sets the retrieved information if needed
        ArrayList<Float> entries = context == TOTAL_AMOUNT_CONTEXT
                ? expendituresQuery.get(this, "amount")
                : new ArrayList<>();

        // Set the data into the UI
        setLabels(entries, expenditures);
    }

    @SuppressLint("DefaultLocale")
    private void setLabels(ArrayList<Float> entries, ArrayList<Float> expenditures){
        float difference = getDifference(entries, expenditures);
        colonesAmountLabel.setText(String.format(MONEY_FORMAT, difference));

        difference = getDifference(entries, expenditures);
        dollarsAmountLabel.setText(String.format(MONEY_FORMAT, difference));
    }

    /**
     * Handles an array list as stack and pops the first element
     * @param list Array list to be used
     * @return Element removed from the array list
     */
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
