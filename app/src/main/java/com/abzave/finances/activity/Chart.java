package com.abzave.finances.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import java.util.StringJoiner;

public class Chart extends AppCompatActivity implements IDataBaseConnection {

    private AlertDialog dialog;
    private PieChart chartColones;
    private PieChart chartDollars;
    private short context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        chartColones = findViewById(R.id.chartColones);
        chartDollars = findViewById(R.id.chartDollars);
        context = getIntent().getShortExtra(CONTEXT, ENTRIES_CONTEXT);
        setUpChart();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.chart_overlow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        createFilterPopUp();
        return super.onOptionsItemSelected(item);
    }

    private void setUpChart(){
        setChartDescription(chartColones, COLONES);
        setChartDescription(chartDollars, DOLLARS);
        setData(chartColones, COLONES, getAllDescriptionsByCurrency(COLONES));
        setData(chartDollars, DOLLARS, getAllDescriptionsByCurrency(DOLLARS));
    }

    private void setChartDescription(PieChart chart, String currency){
        Description description = new Description();
        description.setText(getContextName() + " " + currency);
        description.setTextColor(Color.WHITE);
        chart.setDescription(description);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.setHoleColor(BACKGROUND_COLOR);
        chart.setTransparentCircleColor(BACKGROUND_COLOR);
    }

    private String getContextName(){
        return context == ENTRIES_CONTEXT ? ENTRIES_CHART_NAME : EXPENDITURES_CHART_NAME;
    }

    private void setData(PieChart chart, String currency, ArrayList<String> descriptions){
        Cursor data = getData(currency, descriptions);
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (!data.moveToFirst()){
            Toast.makeText(this, NO_REGISTERS_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }
        while (!data.isAfterLast()){
            entries.add(new PieEntry(data.getFloat(SUM_COLUMN), data.getString(DESCRIPTION_COLUMN_IN_SUM)));
            data.moveToNext();
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(new PieData(dataSet));
    }

    private Cursor getData(String currency, ArrayList<String> descriptions){
        SQLiteDatabase database = getDataBaseReader(this);
        String currencyId = String.valueOf(getId(database, CURRENCY_TYPE_QUERY, currency));
        String[] whereValues = {currencyId};
        String query = getContextQuery();
        query += " " + parseArrayListToSqlList(descriptions) + " " + GROUP_BY_DESCRIPTION;
        return database.rawQuery(query, whereValues);
    }

    private String getContextQuery(){
        return context == ENTRIES_CONTEXT ? SUM_OF_ENTRIES_QUERY_BY_DESCRIPTION : SUM_OF_EXPENDITURES_QUERY_BY_DESCRIPTION;
    }

    private String getDescriptionQueryByContext(){
        return context == ENTRIES_CONTEXT ? ALL_ENTIRES_DESCRIPTIONS_FOR_CURRENCY : ALL_EXPENDITURES_DESCRIPTIONS_FOR_CURRENCY;
    }

    private ArrayList<String> getAllDescriptionsByCurrency(String currency){
        SQLiteDatabase database = getDataBaseReader(this);
        String currencyId = String.valueOf(getId(database, CURRENCY_TYPE_QUERY, currency));
        String[] whereValues = {currencyId};
        Cursor data = database.rawQuery(getDescriptionQueryByContext(), whereValues);
        ArrayList<String> descriptions = new ArrayList<>();
        if (!data.moveToFirst()){
            Toast.makeText(this, NO_REGISTERS_MESSAGE, Toast.LENGTH_SHORT).show();
            return descriptions;
        }
        while (!data.isAfterLast()){
            descriptions.add(data.getString(DESCRIPTION_COLUMN));
            data.moveToNext();
        }
        data.close();
        return descriptions;
    }

    private String parseArrayListToSqlList(ArrayList<String> items){
        StringJoiner list = new StringJoiner("\",\"", "(\"", "\")");
        items.forEach(list::add);
        return list.toString();
    }

    private void createFilterPopUp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popup = getLayoutInflater().inflate(R.layout.filter_popup, null);
        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        addDescriptionCheckboxes(popup);
        setUpPopupActions(popup);
        dialog.show();
    }

    private void addDescriptionCheckboxes(View popup){
        LinearLayout layout = popup.findViewById(R.id.descriptionsLayout);
        addDescriptionCheckboxesByCurrency(layout, COLONES);
        addDescriptionCheckboxesByCurrency(layout, DOLLARS);
    }

    private void addDescriptionCheckboxesByCurrency(LinearLayout layout, String currency){
        ArrayList<String> descriptions = getAllDescriptionsByCurrency(currency);
        int startIndex = layout.indexOfChild(getCurrencyLabel(currency, layout)) + 1;
        for (String description : descriptions){
            CheckBox descriptionCheckbox = new CheckBox(new ContextThemeWrapper(getApplicationContext(), R.style.RadioButtonStyle));
            descriptionCheckbox.setText(description);
            descriptionCheckbox.setTextColor(Color.WHITE);
            layout.addView(descriptionCheckbox, startIndex);
            startIndex++;
        }
    }

    private View getCurrencyLabel(String currency, LinearLayout layout){
        return currency.equals(DOLLARS) ? layout.findViewById(R.id.dollars_label) : layout.findViewById(R.id.colones_label);
    }

    private void setUpPopupActions(View popup){
        Button cancelButton = popup.findViewById(R.id.cancel_button);
        Button filterButton = popup.findViewById(R.id.filter_button);
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        filterButton.setOnClickListener(v -> {
            filterByName(popup);
            dialog.dismiss();
        });
    }

    private void filterByName(View popup){
        LinearLayout layout = popup.findViewById(R.id.descriptionsLayout);
        ArrayList<String> colonesDescriptions = getCheckedDescriptions(layout, COLONES);
        ArrayList<String> dollarsDescriptions = getCheckedDescriptions(layout, DOLLARS);
        colonesDescriptions = colonesDescriptions.isEmpty() ? getAllDescriptionsByCurrency(COLONES) : colonesDescriptions;
        dollarsDescriptions = dollarsDescriptions.isEmpty() ? getAllDescriptionsByCurrency(DOLLARS) : dollarsDescriptions;
        chartColones.clear();
        chartDollars.clear();
        setData(chartColones, COLONES, colonesDescriptions);
        setData(chartDollars, DOLLARS, dollarsDescriptions);
    }

    private ArrayList<String> getCheckedDescriptions(LinearLayout layout, String currency){
        ArrayList<String> descriptions = new ArrayList<>();
        int startIndex = layout.indexOfChild(getCurrencyLabel(currency, layout)) + 1;
        View view = layout.getChildAt(startIndex);
        while(view instanceof CheckBox && startIndex < layout.getChildCount()){
            if(((CheckBox)view).isChecked()){
                descriptions.add(((CheckBox) view).getText().toString());
            }
            startIndex++;
            view = layout.getChildAt(startIndex);
        }
        return descriptions;
    }

}
