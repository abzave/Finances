package com.abzave.finances.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abzave.finances.R;
import com.abzave.finances.model.CurrencyType;
import com.abzave.finances.model.Entry;
import com.abzave.finances.model.Expenditure;
import com.abzave.finances.model.IBaseModel;

import java.util.ArrayList;
import java.util.HashMap;

import kotlin.Pair;

public class EditRecordActivity extends AppCompatActivity {

    private TextView idText;
    private TextView amountText;
    private TextView descriptionText;
    private RadioGroup currencyGroup;
    private TextView dateText;
    private Button saveButton;

    private int id;
    private boolean isEntry;
    private IBaseModel record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        id = getIntent().getIntExtra("id", 0);
        isEntry = getIntent().getBooleanExtra("entry", false);

        getData();
        getUi();
        setUi();
    }

    private void getUi() {
        idText = findViewById(R.id.idTextField);
        amountText = findViewById(R.id.amountTextField);
        descriptionText = findViewById(R.id.descriptionTextField);
        currencyGroup = findViewById(R.id.currenciesRadioGroup);
        dateText = findViewById(R.id.dateTextField);
        saveButton = findViewById(R.id.saveButton);
    }

    @SuppressLint("SetTextI18n")
    private void setUi() {
        idText.setText(Integer.toString(id));

        amountText.setText(Float.toString((Float) (record.get("amount"))));
        descriptionText.setText((String) (record.get("description")));
        currencyGroup.check(getCurrencyRadioId());
        dateText.setText((String) record.get("date") );
    }

    private int getCurrencyRadioId() {
        int currencyId = (Integer) record.get("currency");
        CurrencyType dollarsType = CurrencyType.Companion.findBy(this, new Pair<>("type", "Dolares")).get(0);

        boolean isDollars = ((Integer) dollarsType.get("id")) == currencyId;
        return isDollars ? R.id.dollarsButton : R.id.colonesButton;
    }

    private void getData() {
        if (isEntry) {
            record = Entry.Companion.findOne(this, new String[] {Integer.toString(id)});
        } else {
            record = Expenditure.Companion.findOne(this, new String[] { Integer.toString(id)});
        }
    }

    public void saveData(View view) {
        HashMap<String, Object> values = new HashMap<>();

        values.put("amount", Float.valueOf(amountText.getText().toString()));
        values.put("description", descriptionText.getText().toString());
        values.put("currency", getCurrentCurrencyId());

        record.update(values);
        if (!record.save(this)) {
            Toast.makeText(this, "Error guardando el registro.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private int getCurrentCurrencyId() {
        RadioButton checked = findViewById(currencyGroup.getCheckedRadioButtonId());
        CurrencyType currency = CurrencyType.Companion.findBy(this, new Pair<>("type", checked.getText().toString())).get(0);

        return (Integer) currency.get("id");
    }

}
