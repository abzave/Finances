package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.abzave.finances.R;
import com.abzave.finances.dataBase.IDataBaseConnection;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;

public class Chart extends AppCompatActivity implements IDataBaseConnection {

    PieChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        chart = findViewById(R.id.chart);
        setUpChart();
    }

    private void setUpChart(){
        setChartDescription();
    }

    private void setChartDescription(){
        Description description = new Description();
        description.setText(ENTRIES_CHART_NAME);
        chart.setDescription(description);
    }

}
