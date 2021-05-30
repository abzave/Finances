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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.abzave.finances.R;
import com.abzave.finances.model.Entry;
import com.abzave.finances.model.Expenditure;
import com.abzave.finances.model.QueryModel;
import com.abzave.finances.model.database.IDataBaseConnection;

import java.util.ArrayList;

public class ExpenditureView extends AppCompatActivity implements IDataBaseConnection {

    private LinearLayout layout;
    private LinearLayout baseLayout;
    private TextView baseLabel;
    private boolean isEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure_view);
        getUiElements();
        loadExpenditures();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenditures();
    }

    private void getUiElements(){
        layout = findViewById(R.id.descriptionsLayout);
        baseLayout = findViewById(R.id.baseLayout);
        baseLabel = findViewById(R.id.baseLabel);
        isEntry = getIntent().getBooleanExtra(ENTRY_STRING, IS_ENTRY);
    }

    private void loadExpenditures(){
        layout.removeAllViews();
        ArrayList<ArrayList<Object>> records = getList();
        if(records.isEmpty()){
            baseLabel.setText(NO_REGISTERS_MESSAGE);
            return;
        }
        addElements(records);
        layout.removeView(baseLayout);
    }

    private ArrayList<ArrayList<Object>> getList(){
        ArrayList<ArrayList<Object>> records = new ArrayList<>();

        String selection = "amount, description, type";
        QueryModel query;

        if (this.isEntry) {
            selection += ", Entry.id";
            String joins = "INNER JOIN CurrencyType on Entry.currency = CurrencyType.id";
            query = Entry.Companion.select(selection).join(joins);
        } else {
            selection += ", Expenditure.id";
            String joins = "INNER JOIN CurrencyType on Expenditure.currency = CurrencyType.id";
            query = Expenditure.Companion.select(selection).join(joins);
        }

        ArrayList<Object> ids = query.get(this, "id");
        ArrayList<Object> amounts = query.get(this, "amount");
        ArrayList<Object> descriptions = query.get(this, "description");
        ArrayList<Object> types = query.get(this, "type");

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

    private LinearLayout createCard(int id, String text) {
        Context context = getApplicationContext();

        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final int widthInDp = 44;

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView elementLabel = new TextView(context);
        elementLabel.setText(text);
        elementLabel.setTextColor(Color.WHITE);
        elementLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));

        Space spacer = new Space(context);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT));

        Button editButton = new Button(context);
        int buttonWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, metrics);
        editButton.setBackground(ContextCompat.getDrawable(context, android.R.drawable.ic_menu_edit));
        editButton.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
        editButton.setGravity(Gravity.TOP);
        editButton.setOnClickListener(view -> this.onEdit(view, id));

        card.addView(elementLabel);
        card.addView(spacer);
        card.addView(editButton);
        return card;
    }

    private void onEdit(View view, int id) {
        Intent intent = new Intent(this, EditRecordActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("entry", isEntry);

        startActivity(intent);
    }

}
