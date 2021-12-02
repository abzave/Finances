package com.abzave.finances.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.abzave.finances.R;
import com.abzave.finances.model.database.IDataBaseConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity implements IDataBaseConnection {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
    }

    /**
     * Ask user permission to use external storage
     * We need write access externally in order to backup/restore the database locally
     */
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
        boolean isBackup = item.getItemId() == R.id.backup;

        // Backups/restores the database
        moveDataBase(isBackup);
        return super.onOptionsItemSelected(item);
    }

    /**
     * Copies a database dump from or to the device storage based on backup flag
     * isBackup = true, means copy to the storage
     * isBackup = false, means copy from the storage
     * @param isBackup Determines if the file is moved from or to storage
     */
    private void moveDataBase(boolean isBackup){
        try {
            FileChannel destinationChannel = getDestinationChannel(isBackup);
            FileChannel sourceChannel = getSourceChannel(isBackup);

            if(isBackup) {
                destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            }else {
                sourceChannel.transferFrom(destinationChannel, 0, destinationChannel.size());
            }

            sourceChannel.close();
            destinationChannel.close();

            Toast.makeText(this, CREATED_TEXT, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens a file input/output stream for the database dump path in the data directory
     * isBackup = true, means input stream
     * isBackup = false, means output stream
     * @param isBackup determines if the stream is input or output
     * @return File input/output stream for the database dump path in the data directory
     * @throws FileNotFoundException The database dump might not exists
     */
    private FileChannel getSourceChannel(boolean isBackup) throws FileNotFoundException {
        File source = Environment.getDataDirectory();
        String sourcePath = DATABASE_PATH + DATABASE_NAME;

        source = new File(source, sourcePath);
        if (isBackup) {
            return new FileInputStream(source).getChannel();
        }
        return new FileOutputStream(source).getChannel();
    }

    /**
     * Opens a file input/output stream for the database dump path in the device storage
     * isBackup = true, means input stream
     * isBackup = false, means output stream
     * @param isBackup determines if the stream is input or output
     * @return File input/output stream for the database dump path in the device storage
     * @throws FileNotFoundException The database dump might not exists
     */
    private FileChannel getDestinationChannel(boolean isBackup) throws FileNotFoundException {
        File destination = getExternalFilesDir(ROOT_DIRECTORY);

        destination = new File(destination, DATABASE_NAME);
        if(isBackup) {
            return new FileOutputStream(destination).getChannel();
        }
        return new FileInputStream(destination).getChannel();
    }

    public void goToAddExpenditure(View view){
        goToActivity(AddEntry.class, ENTRY_STRING, view.getId() == R.id.addEntry);
    }

    public void goToExpenditureView(View view){
        goToActivity(ExpenditureView.class, ENTRY_STRING, view.getId() == R.id.checkEntries);
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
