package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.abzave.finances.R;

public class Stadistics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stadistics);
    }

    public void goToAmountView(View view){
        goToActivity(TotalAmount.class);
    }

    public void goToChart(View view){
        goToActivity(Chart.class);
    }

    private void goToActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

}
