package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.abzave.finances.R;
import com.abzave.finances.dataBase.IDataBaseConnection;
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

    public void goToChart(View view){
        goToActivity(Chart.class);
    }

    private void goToActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void goToActivity(Class activity, String context){
        Intent intent = new Intent(this, activity);
        intent.putExtra(CONTEXT, context);
        startActivity(intent);
    }

}
