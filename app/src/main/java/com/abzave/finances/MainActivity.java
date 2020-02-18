package com.abzave.finances;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToAddExpenditure(View view){
        Intent intent = new Intent(this, AddEntry.class);
        startActivity(intent);
    }

    public void goToExpenditureView(View view){
        Intent intent = new Intent(this, ExpenditureView.class);
        startActivity(intent);
    }

    public void goToStadistics(View view){
        Intent intent = new Intent(this, Stadistics.class);
        startActivity(intent);
    }

}
