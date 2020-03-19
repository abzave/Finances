package com.abzave.finances;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class TotalAmount extends AppCompatActivity implements IConstants{

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
        DataBase dataBase = new DataBase(this, DATABASE_NAME, CURSOR_FACTORY, DATABASE_VERSION);
        SQLiteDatabase dataBaseReader = dataBase.getReadableDatabase();
        dataBaseReader.close();
        dataBase.close();
    }
}
