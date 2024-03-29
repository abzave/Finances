package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.abzave.finances.R;
import com.abzave.finances.lib.IConstants;

public class Stadistics extends AppCompatActivity implements IConstants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stadistics);
    }

    public void goToAmountView(View view){
        goToActivity(TotalAmount.class, TOTAL_AMOUNT_CONTEXT);
    }

    public void goToExpendedView(View view){
        goToActivity(TotalAmount.class, EXPENDED_CONTEXT);
    }

    public void goToRemainingView(View view){
        goToActivity(TotalAmount.class, REMAINING_CONTEXT);
    }

    public void goToRetirementView(View view){
        goToActivity(TotalAmount.class, RETIREMENT_CONTEXT);
    }

    public void goToEmegenciesView(View view){
        goToActivity(TotalAmount.class, EMERGENCIES_CONTEXT);
    }

    public void goToWhimsView(View view){
        goToActivity(TotalAmount.class, WHIMS_CONTEXT);
    }

    public void goToEntriesChart(View view){
        goToActivity(Chart.class, ENTRIES_CONTEXT);
    }

    public void goToExpendituresChart(View view){
        goToActivity(Chart.class, EXPENDITURES_CONTEXT);
    }

    private void goToActivity(Class activity, short context){
        Intent intent = new Intent(this, activity);
        intent.putExtra(CONTEXT, context);
        startActivity(intent);
    }

}
