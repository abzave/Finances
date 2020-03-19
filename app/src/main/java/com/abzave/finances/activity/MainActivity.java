package com.abzave.finances;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements IConstants{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToAddExpenditure(View view){
        goToActivity(AddEntry.class, ENTRY_STRING, view.getId() == R.id.addEntry);
    }

    public void goToExpenditureView(View view){
        goToActivity(ExpenditureView.class);
    }

    public void goToStadistics(View view){
        goToActivity(Stadistics.class);
    }

    private void goToActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void goToActivity(Class activity, String name, Boolean value){
        Intent intent = new Intent(this, activity);
        intent.putExtra(name, value);
        startActivity(intent);
    }

}
