package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.abzave.finances.R;
import com.abzave.finances.lib.IConstants;

public class MainActivity extends AppCompatActivity implements IConstants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
    }

    private void requestPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        createBackUp();
        return super.onOptionsItemSelected(item);
    }

    private void createBackUp(){

    }

    public void goToAddExpenditure(View view){
        goToActivity(AddEntry.class, ENTRY_STRING, view.getId() == R.id.addEntry);
    }

    public void goToExpenditureView(View view){
        goToActivity(ExpenditureView.class);
    }

    public void goToStadistics(View view){
        goToActivity(Stadistics.class);
    }

    private void goToActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void goToActivity(Class activity, String name, Boolean value){
        Intent intent = new Intent(this, activity);
        intent.putExtra(name, value);
        startActivity(intent);
    }

}
