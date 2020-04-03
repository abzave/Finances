package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.abzave.finances.R;
import com.abzave.finances.dataBase.IDataBaseConnection;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

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
        setData();
    }

    private void setChartDescription(){
        Description description = new Description();
        description.setText(ENTRIES_CHART_NAME);
        chart.setDescription(description);
    }

    private void setData(){
        Cursor data = getData();
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (!data.moveToFirst()){
            Toast.makeText(this, NO_REGISTERS_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }
        while (!data.isAfterLast()){
            entries.add(new PieEntry(data.getFloat(SUM_COLUMN), data.getString(DESCRIPTION_COLUMN)));
            data.moveToNext();
        }
        PieDataSet dataSet = new PieDataSet(entries, DESCRIPTION);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(new PieData(dataSet));
    }

    private Cursor getData(){
        SQLiteDatabase database = getDataBaseReader(this);
        return database.rawQuery(SUM_OF_ENTRIES_QUERY_BY_DESCRIPTION, NO_SELECTION_ARGUMENTS);
    }

}
