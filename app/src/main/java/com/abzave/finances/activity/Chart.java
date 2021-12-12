package com.abzave.finances.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.abzave.finances.controller.CurrencyController;
import com.abzave.finances.controller.EntriesController;
import com.abzave.finances.model.CurrencyType;
import com.abzave.finances.model.Expenditure;
import com.abzave.finances.model.QueryModel;
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

    /**
     * Creates a pie chart with the information given
     * @param chart Chart object to be handled
     * @param currency Currency in which the data should be
     * @param descriptions Labels of each record
     */
    private void setDataPie(PieChart chart, String currency, ArrayList<String> descriptions){
        // Style
        chart.setBackgroundColor(BACKGROUND_COLOR);
        chart.setHoleColor(BACKGROUND_COLOR);
        chart.setTransparentCircleColor(BACKGROUND_COLOR);

        // Retrieve chart data
        PieDataSet entries = getPieData(currency, descriptions);

        if (entries == null){
            Toast.makeText(this, NO_REGISTERS_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the data
        entries.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(new PieData(entries));
    }

    /**
     * Creates a line chart with the information given
     * @param chart Chart object to be handled
     * @param currency Currency in which the data should be
     * @param descriptions Labels of each record
     */
    private void setDataLine(LineChart chart, String currency, ArrayList<String> descriptions){
        // Retrieve chart data
        ArrayList<ILineDataSet> entries = getDataLine(currency, descriptions);

        if (entries == null){
            Toast.makeText(this, NO_REGISTERS_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }

        LineData lineData = new LineData(entries);
        lineData.setValueTextColor(Color.WHITE);
        chart.setData(lineData);
    }

    private void styleLineChart(LineChart chart) {
        chart.setBackgroundColor(BACKGROUND_COLOR);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getAxis(YAxis.AxisDependency.LEFT).setTextColor(Color.WHITE);
    }

    /**
     * Fetches all the required data for a pie chart based on currency and descriptions
     * @param currency Currency in which the records should be
     * @param descriptions Filtered records
     * @return Array of entries/expenditures to be charted
     */
    private PieDataSet getPieData(String currency, ArrayList<String> descriptions){
        int currencyId = CurrencyController.Companion.getId(this, currency);

        String condition = String.format(
                "currency = %s AND description IN %s",
                currencyId,
                toList(descriptions)
        );

        ArrayList<ArrayList<Object>> data = EntriesController.Companion.get(
                this,
                context == ENTRIES_CONTEXT,
                "SUM(amount), description",
                condition,
                "description"
        );

        ArrayList<PieEntry> entries = new ArrayList<>();
        data.forEach(row -> {
            Float sum = (Float) row.get(SUM_COLUMN);
            String description = (String) row.get(DESCRIPTION_COLUMN_IN_SUM);

            entries.add(new PieEntry(sum, description));
        });

        if (entries.isEmpty()) {
            return null;
        }

        return new PieDataSet(entries, "");
    }

    /**
     * Fetches all the required data for a line chart based on currency and descriptions
     * @param currency Currency in which the records should be
     * @param descriptions Filtered records
     * @return Array of entries/expenditures to be charted
     */
    private ArrayList<ILineDataSet> getDataLine(String currency, ArrayList<String> descriptions){
        int currencyId = CurrencyController.Companion.getId(this, currency);

        String condition = String.format(
                "currency = %s AND description IN %s",
                currencyId,
                toList(descriptions)
        );

        ArrayList<ArrayList<Object>> data = EntriesController.Companion.get(
                this,
                context == ENTRIES_CONTEXT,
                "SUM(amount), description, strftime('%Y-%m',date)",
                condition,
                "strftime('%Y-%m',REPLACE(date,'/','-'))"
        );

        ArrayList<Entry> entries = new ArrayList<>();
        data.forEach(row -> {
            int position = data.indexOf(row);
            entries.add(new Entry(position, (Float) row.get(SUM_COLUMN)));
        });

        if (entries.isEmpty()) {
            return null;
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(new LineDataSet(entries, ""));

        return dataSets;
    }

    /**
     * Fetches all the existing description for a currency
     * @param currency currency to filter with
     * @return All the different descriptions found
     */
    private ArrayList<String> getAllDescriptionsByCurrency(String currency){
        int currencyId = CurrencyController.Companion.getId(this, currency);

        ArrayList<ArrayList<Object>> records = EntriesController.Companion.get(
                this,
                context != ENTRIES_CONTEXT,
                "description",
                "currency = " + currencyId,
                "description"
        );

        // Maps the fetched records to an array list
        ArrayList<String> descriptions = new ArrayList<>();
        records.forEach(row -> descriptions.add((String) row.get(0)));
        return descriptions;
    }

    /**
     * Join an array list of strings
     * @param items array list to joined
     * @return All the items in the array list joined by quotes and comma
     */
    private String toList(ArrayList<String> items){
        StringJoiner list = new StringJoiner("\",\"", "(\"", "\")");
        items.forEach(list::add);
        return list.toString();
    }

    /**
     * Builds the pop up menu to filter descriptions
     */
    private void createFilterPopUp(){
        // Creates the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View popup = getLayoutInflater().inflate(R.layout.filter_popup, null);
        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();

        // Fill the information
        addDescriptionCheckboxes(popup);

        // Adds buttons functionality
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

    /**
     * Add a checkbox for each description in the pop up menu for each currency
     * @param layout Layout where the checkboxes will be inserted
     * @param currency currency to handle
     */
    private void addDescriptionCheckboxesByCurrency(LinearLayout layout, String currency){
        // Get descriptions
        ArrayList<String> descriptions = getAllDescriptionsByCurrency(currency);

        // Get the index where the checkboxes will be inserted
        int startIndex = layout.indexOfChild(getCurrencyLabel(currency, layout)) + 1;

        // Create the checkboxes
        for (String description : descriptions){
            CheckBox descriptionCheckbox = new CheckBox(
                    new ContextThemeWrapper(getApplicationContext(), R.style.RadioButtonStyle)
            );

            descriptionCheckbox.setText(description);
            descriptionCheckbox.setTextColor(Color.WHITE);
            layout.addView(descriptionCheckbox, startIndex);
            startIndex++;
        }
    }

    private View getCurrencyLabel(String currency, LinearLayout layout){
        return currency.equals(DOLLARS)
                ? layout.findViewById(R.id.dollars_label)
                : layout.findViewById(R.id.colones_label);
    }

    /**
     * Gets all the selected checkboxes and reload the charts data based on those. If none selected
     * then all descriptions are loaded
     * @param popup menu where the information come from
     */
    private void filterByName(View popup){
        // Gets the checked data
        LinearLayout layout = popup.findViewById(R.id.descriptionsLayout);
        ArrayList<String> colonesDescriptions = getCheckedDescriptions(layout, COLONES);
        ArrayList<String> dollarsDescriptions = getCheckedDescriptions(layout, DOLLARS);

        // Filter the descriptions, if none selected all are loaded
        colonesDescriptions = colonesDescriptions.isEmpty()
                ? getAllDescriptionsByCurrency(COLONES)
                : colonesDescriptions;

        dollarsDescriptions = dollarsDescriptions.isEmpty()
                ? getAllDescriptionsByCurrency(DOLLARS)
                : dollarsDescriptions;

        // Reset data
        chartColones.clear();
        chartDollars.clear();
        lineChartColones.clear();
        lineChartDollars.clear();

        setDataPie(chartColones, COLONES, colonesDescriptions);
        setDataPie(chartDollars, DOLLARS, dollarsDescriptions);
        setDataLine(lineChartColones, COLONES, colonesDescriptions);
        setDataLine(lineChartDollars, DOLLARS, dollarsDescriptions);
    }

    /**
     * Returns all the descriptions checked by the user
     * @param layout layout with the checkboxes to be consider
     * @param currency currency in which the records are
     * @return List with all the descriptions with a checked checkbox
     */
    private ArrayList<String> getCheckedDescriptions(LinearLayout layout, String currency){
        ArrayList<String> descriptions = new ArrayList<>();

        // Gets the index in the layout where the checkboxes are and the check box
        int startIndex = layout.indexOfChild(getCurrencyLabel(currency, layout)) + 1;
        View view = layout.getChildAt(startIndex);

        // Iterate while we inside the same currency and there is more items
        while(view instanceof CheckBox && startIndex < layout.getChildCount()){
            // If we found one checked checkbox then add it to the list
            if(((CheckBox)view).isChecked()){
                descriptions.add(((CheckBox) view).getText().toString());
            }

            startIndex++;
            view = layout.getChildAt(startIndex);
        }
        return descriptions;
    }

}
