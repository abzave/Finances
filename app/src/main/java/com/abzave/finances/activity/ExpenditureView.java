package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.abzave.finances.R;
import com.abzave.finances.model.Entry;
import com.abzave.finances.model.Expenditure;
import com.abzave.finances.model.QueryModel;
import com.abzave.finances.model.database.IDataBaseConnection;

import java.util.ArrayList;

public class ExpenditureView extends AppCompatActivity implements IDataBaseConnection {

    private final int PAGE_SIZE = 15;
    private final int MAX_PAGES_TO_SHOW = 5;

    private LinearLayout layout;
    private LinearLayout buttonsLayout;
    private TextView baseLabel;
    private boolean isEntry;
    private int totalRecords;
    private int totalPages;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure_view);

        calculatePages();
        getUiElements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenditures();
    }

    /**
     * Calculates the total amount of pages that will be required based on the amount of records
     */
    private void calculatePages() {
        totalRecords = this.isEntry
                ? Entry.Companion.count(this)
                : Expenditure.Companion.count(this);

        totalPages = (totalRecords / PAGE_SIZE) + 1;
    }

    private void getUiElements(){
        layout = findViewById(R.id.descriptionsLayout);
        baseLabel = findViewById(R.id.baseLabel);
        buttonsLayout = findViewById(R.id.page_layout);
        isEntry = getIntent().getBooleanExtra(ENTRY_STRING, IS_ENTRY);
    }

    /**
     * Clears the current view and loads the new information
     */
    private void loadExpenditures(){
        // Reset the layout
        layout.removeAllViews();

        // Load the new records
        ArrayList<ArrayList<Object>> records = getAllRecords();
        if(records.isEmpty()){
            baseLabel.setText(NO_REGISTERS_MESSAGE);
            return;
        }

        addElements(records);
        loadPagingBar();
    }

    /**
     * Fetches all the entry/expenditure records that fits in the current page
     * and map them to a Java structure
     * @return Array list with all the fetched records
     */
    private ArrayList<ArrayList<Object>> getAllRecords(){
        ArrayList<ArrayList<Object>> records = new ArrayList<>();
        int recordsOffset = (currentPage - 1) * PAGE_SIZE;

        // Builds the query
        String table = this.isEntry ? "Entry" : "Expenditure";
        String selection = String.format("amount, description, type, %s.id", table);
        String joins = String.format(
                "INNER JOIN CurrencyType on %s.currency = CurrencyType.id", table
        );

        // Gets the records
        QueryModel query = this.isEntry
                ? Entry.Companion.select(selection)
                : Expenditure.Companion.select(selection);
        query.join(joins).limit(PAGE_SIZE).offset(recordsOffset);

        // Separate the records to its own array list
        ArrayList<Object> ids = query.get(this, "id");
        ArrayList<Object> amounts = query.get(this, "amount");
        ArrayList<Object> descriptions = query.get(this, "description");
        ArrayList<Object> types = query.get(this, "type");

        // Maps the array lists to a Java structure
        for (int recordIndex = 0; recordIndex < amounts.size(); recordIndex++) {
            ArrayList<Object> row = new ArrayList<>();

            row.add(amounts.get(recordIndex));
            row.add(descriptions.get(recordIndex));
            row.add(types.get(recordIndex));
            row.add(ids.get(recordIndex));

            records.add(row);
        }

        return records;
    }

    /**
     * Handle the records fetched to be added in the UI
     * @param records Array list with the records to be displayed
     */
    @SuppressLint("DefaultLocale")
    private void addElements(ArrayList<ArrayList<Object>> records){
        String message;

        for (ArrayList<Object> record : records) {
            String description = (String) record.get(EXPENDITURES_DESCRIPTION_COLUMN);
            String currency = (String) record.get(CURRENCY_COLUMN);
            float amount = (Float) record.get(AMOUNT_COLUMN);
            int id = (Integer) record.get(ID_VIEW_COLUMN);

            message = String.format("%s: %s %s", description, String.format(MONEY_FORMAT, amount), currency);
            layout.addView(createCard(id, message));
        }
    }

    /**
     * Creates a row in the UI that contains the record information and an edit button
     * @param id id of the record being handled
     * @param text message to be displayed in the row
     * @return New layout ready to be added in the UI
     */
    private LinearLayout createCard(int id, String text) {
        Context context = getApplicationContext();

        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final int widthInDp = 44;       // 44 is the point where the edit icon is ok

        // Sets up the layout
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Sets up the text to be displayed
        TextView elementLabel = new TextView(context);
        elementLabel.setText(text);
        elementLabel.setTextColor(Color.WHITE);
        elementLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));

        // Adds a space between the text and the edit button
        Space spacer = new Space(context);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Sets up the edit button
        Button editButton = new Button(context);
        int buttonWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, metrics);
        editButton.setBackground(ContextCompat.getDrawable(context, android.R.drawable.ic_menu_edit));
        editButton.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
        editButton.setGravity(Gravity.TOP);
        editButton.setOnClickListener(view -> this.onEdit(view, id));

        // Adds the elements to the new layout
        card.addView(elementLabel);
        card.addView(spacer);
        card.addView(editButton);
        return card;
    }

    /**
     * Opens a new edit record activity
     * @param view
     * @param id id of the record to be edited
     */
    private void onEdit(View view, int id) {
        Intent intent = new Intent(this, EditRecordActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("entry", isEntry);

        startActivity(intent);
    }

    /**
     * Adds to the page navigation bar the buttons page index buttons and adds the navigation
     * to the main layout
     */
    private void loadPagingBar() {
        // 2 is accounting for the go to begin and previous page buttons
        int elementsBeforePageIndex = 2;
        resetPageIndexesButtons();

        /*
         * Start a couple of indexes before so the current page is on the middle
         * if the index is too high then we only show the last 5 pages (last - 4)
         */
        int startPage = Math.max(currentPage - 2, 1);
        startPage = Math.min(startPage, totalPages - 4);

        // Show only 5 pages or all the pages if there are less than 5
        int lastIndexToShow = Math.min(totalPages, MAX_PAGES_TO_SHOW + startPage - 1);

        for (int page = startPage; page <= lastIndexToShow; page++) {
            int insertIndex = elementsBeforePageIndex + (page - startPage);
            buttonsLayout.addView(createPageIndexButton(page), insertIndex);
        }

        layout.addView(buttonsLayout);
    }

    /**
     * Clears all the existing index buttons in the page layout
     */
    private void resetPageIndexesButtons() {
        int currentChildrenCount = buttonsLayout.getChildCount();

        /*
         * 4 is the default number of buttons in the layout
         * Go to begin, previous, next, go to end
         */
        while (currentChildrenCount > 4) {
            /*
             * Index 2 is the index where should start the index buttons
             * Index 0 is go to begin and index 1 is previous page
             */
            buttonsLayout.removeViewAt(2);
            currentChildrenCount = buttonsLayout.getChildCount();
        }
    }

    /**
     * Creates a new button with the style expected for paging
     * Style:
     *   No width, match parent height and 1 of weight
     *   Transparent background
     *   A white number a text of size 24 sp
     * @param pageIndex text to put in the button
     * @return new button with the index provided
     */
    private Button createPageIndexButton(int pageIndex) {
        Button pageButton = new Button(getApplicationContext());

        // Layout setting
        pageButton.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));

        // Button style
        pageButton.setBackgroundColor(Color.TRANSPARENT);
        pageButton.setText(String.valueOf(pageIndex));
        pageButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        pageButton.setTextColor(
                currentPage == pageIndex
                        ? Color.WHITE
                        : Color.parseColor("#C7C5CF")
        );

        // Functionality
        pageButton.setOnClickListener(view -> this.onPageChange(view, pageIndex));

        return pageButton;
    }

    /**
     * Changes the current page loaded to a given one
     * @param view
     * @param pageIndex page to load
     */
    public void onPageChange(View view, int pageIndex) {
        currentPage = pageIndex;
        loadExpenditures();
    }

    /**
     * Changes the current page loaded to the first one
     * @param view
     */
    public void goToBegin(View view){
        onPageChange(view, 1);
    }

    /**
     * Changes the current page loaded to the previous one
     * @param view
     */
    public void previousPage(View view){
        onPageChange(view, currentPage - 1);
    }

    /**
     * Changes the current page loaded to the next one
     * @param view
     */
    public void nextPage(View view){
        onPageChange(view, currentPage + 1);
    }

    /**
     * Changes the current page loaded to the last one
     * @param view
     */
    public void goToEnd(View view){
        onPageChange(view, totalPages);
    }

}
