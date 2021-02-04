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
import com.abzave.finances.model.CurrencyType;
import com.abzave.finances.model.database.IDataBaseConnection;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.StringJoiner;

import kotlin.Pair;

public class Chart extends AppCompatActivity implements IDataBaseConnection {

    private AlertDialog dialog;
    private PieChart chartColones;
    private PieChart chartDollars;
    private LineChart lineChartColones;
    private LineChart lineChartDollars;
    private short context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        chartColones = findViewById(R.id.chartColones);
        chartDollars = findViewById(R.id.chartDollars);
        lineChartColones = findViewById(R.id.lineChartColones);
        lineChartDollars = findViewById(R.id.lineChartDollars);
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
        setDataPie(chartColones, COLONES, getAllDescriptionsByCurrency(COLONES));
        setDataPie(chartDollars, DOLLARS, getAllDescriptionsByCurrency(DOLLARS));
        setDataLine(lineChartColones, COLONES, getAllDescriptionsByCurrency(COLONES));
        setDataLine(lineChartDollars, DOLLARS, getAllDescriptionsByCurrency(DOLLARS));
        styleLineChart(lineChartColones);
        styleLineChart(lineChartDollars);
    }

    private void setDataPie(PieChart chart, String currency, ArrayList<String> descriptions){
        chart.setBackgroundColor(BACKGROUND_COLOR);
        chart.setHoleColor(BACKGROUND_COLOR);
        chart.setTransparentCircleColor(BACKGROUND_COLOR);
        Cursor data = getDataPie(currency, descriptions);
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (data == null || !data.moveToFirst()){
            Toast.makeText(this, NO_REGISTERS_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }
        while (!data.isAfterLast()) {
            entries.add(new PieEntry(data.getFloat(SUM_COLUMN), data.getString(DESCRIPTION_COLUMN_IN_SUM)));
            data.moveToNext();
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(new PieData(dataSet));
        data.close();
    }

    private void setDataLine(LineChart chart, String currency, ArrayList<String> descriptions){
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        Cursor data = getDataLine(currency, descriptions);

        if (data == null || !data.moveToFirst()){
            Toast.makeText(this, NO_REGISTERS_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }

        while (!data.isAfterLast()) {
            entries.add(new Entry(data.getPosition(), data.getFloat(SUM_COLUMN)));
            if (data.getString(DATE_COLUMN) != null) {
                labels.add(data.getString(DATE_COLUMN));
            }
            data.moveToNext();
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData lineData = new LineData(dataSets);
        lineData.setValueTextColor(Color.WHITE);
        chart.setData(lineData);

        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setValueFormatter((value, axis) -> labels.get((int) value));

        chart.invalidate();
        data.close();
    }

    private void styleLineChart(LineChart chart) {
        chart.setBackgroundColor(BACKGROUND_COLOR);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getAxis(YAxis.AxisDependency.LEFT).setTextColor(Color.WHITE);
    }

    private Cursor getDataPie(String currency, ArrayList<String> descriptions){
        SQLiteDatabase database = getDataBaseReader(this);

        Pair<String, ?> currencyQuery = new Pair<>("type", currency);
        ArrayList<CurrencyType> types = CurrencyType.Companion.findBy(this, currencyQuery);
        if (types.isEmpty()){
            return null;
        }

        String currencyId = String.valueOf( types.get(0).get("id"));
        String[] whereValues = {currencyId};
        String query = getContextQueryPie();
        query += " " + parseArrayListToSqlList(descriptions) + " " + GROUP_BY_DESCRIPTION;
        return database.rawQuery(query, whereValues);
    }

    private Cursor getDataLine(String currency, ArrayList<String> descriptions){
        SQLiteDatabase database = getDataBaseReader(this);

        Pair<String, ?> currencyQuery = new Pair<>("type", currency);
        ArrayList<CurrencyType> types = CurrencyType.Companion.findBy(this, currencyQuery);
        if (types.isEmpty()){
            return null;
        }

        String currencyId = String.valueOf(types.get(0).get("id"));
        String[] whereValues = {currencyId};
        String query = getContextQueryLine();
        query += " " + parseArrayListToSqlList(descriptions) + " " + GROUP_BY_DATE;
        return database.rawQuery(query, whereValues);
    }

    private String getContextQueryPie(){
        return context == ENTRIES_CONTEXT ? SUM_OF_ENTRIES_QUERY_BY_DESCRIPTION : SUM_OF_EXPENDITURES_QUERY_BY_DESCRIPTION;
    }

    private String getContextQueryLine(){
        return context == ENTRIES_CONTEXT ? SUM_OF_ENTRIES_QUERY_BY_DESCRIPTION_AND_DATE : SUM_OF_EXPENDITURES_QUERY_BY_DESCRIPTION_AND_DATE;
    }

    private String getDescriptionQueryByContext(){
        return context == ENTRIES_CONTEXT ? ALL_ENTIRES_DESCRIPTIONS_FOR_CURRENCY : ALL_EXPENDITURES_DESCRIPTIONS_FOR_CURRENCY;
    }

    private ArrayList<String> getAllDescriptionsByCurrency(String currency){
        SQLiteDatabase database = getDataBaseReader(this);

        Pair<String, ?> currencyQuery = new Pair<>("type", currency);
        ArrayList<CurrencyType> types = CurrencyType.Companion.findBy(this, currencyQuery);
        if (types.isEmpty()){
            return null;
        }

        String currencyId = String.valueOf((Integer) types.get(0).get("id"));
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
        Button cancelButton = popup.findViewById(R.id.cancel_button);
        Button filterButton = popup.findViewById(R.id.filter_button);
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        filterButton.setOnClickListener(v -> {
            filterByName(popup);
            dialog.dismiss();
        });
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

    private void filterByName(View popup){
        LinearLayout layout = popup.findViewById(R.id.descriptionsLayout);
        ArrayList<String> colonesDescriptions = getCheckedDescriptions(layout, COLONES);
        ArrayList<String> dollarsDescriptions = getCheckedDescriptions(layout, DOLLARS);
        colonesDescriptions = colonesDescriptions.isEmpty() ? getAllDescriptionsByCurrency(COLONES) : colonesDescriptions;
        dollarsDescriptions = dollarsDescriptions.isEmpty() ? getAllDescriptionsByCurrency(DOLLARS) : dollarsDescriptions;
        chartColones.clear();
        chartDollars.clear();
        lineChartColones.clear();
        lineChartDollars.clear();
        setDataPie(chartColones, COLONES, colonesDescriptions);
        setDataPie(chartDollars, DOLLARS, dollarsDescriptions);
        setDataLine(lineChartColones, COLONES, colonesDescriptions);
        setDataLine(lineChartDollars, DOLLARS, dollarsDescriptions);
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
