package com.abzave.finances.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.abzave.finances.R;
import com.abzave.finances.controller.EntriesController;
import com.abzave.finances.model.CurrencyType;
import com.abzave.finances.model.Entry;
import com.abzave.finances.model.Expenditure;
import com.abzave.finances.model.IBaseModel;
import com.abzave.finances.model.database.IDataBaseConnection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import kotlin.Pair;

public class AddEntry extends AppCompatActivity implements IDataBaseConnection {

    private EditText amountEdit;
    private EditText descriptionEdit;
    private RadioButton colonesButton;
    private Boolean isEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        amountEdit = findViewById(R.id.amountEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
        colonesButton = findViewById(R.id.colonesButton);
        isEntry = getIntent().getBooleanExtra(ENTRY_STRING, IS_ENTRY);
    }

    /**
     * Prepares a new record in the database with the information given in the UI
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void add(View view){
        String amount = amountEdit.getText().toString();
        String description = descriptionEdit.getText().toString();

        if(amount.isEmpty() || description.isEmpty()){
            Toast.makeText(this, IMCOMPLETE_CONTENT_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }

        // Creates the new record
        EntriesController.Companion.createNewEntry(
                this,
                amount,
                description,
                colonesButton.isChecked() ? COLONES : DOLLARS,
                this.isEntry
        );

        amountEdit.setText("");
        descriptionEdit.setText("");
    }

}
