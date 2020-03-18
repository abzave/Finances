package com.abzave.finances;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {


    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createTables(database);
        insertValues(database);
    }

    private void createTables(SQLiteDatabase database){
        database.execSQL("CREATE TABLE IF NOT EXISTS CurrencyType(id INTEGER PRIMARY KEY " +
                         "AUTOINCREMENT, type TEXT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS Expenditure(id INTEGER PRIMARY KEY " +
                         "AUTOINCREMENT, amount REAL, description TEXT, currency INTEGER,  " +
                         "FOREIGN KEY(currency) REFERENCES CurrencyType(id))");
        database.execSQL("CREATE TABLE IF NOT EXISTS Entry(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "amount REAL, description TEXT, currency INTEGER,  FOREIGN KEY(currency) " +
                         "REFERENCES currencyType(id))");
        database.execSQL("CREATE TABLE IF NOT EXISTS ReserveType(id INTEGER PRIMARY KEY " +
                         "AUTOINCREMENT, type TEXT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS Reserve(id INTEGER PRIMARY KEY " +
                         "AUTOINCREMENT, type INT, amount REAL, entry INT, FOREIGN KEY(type) " +
                         "REFERENCES ReserveType(id), FOREIGN KEY(entry) REFERENCES Entry(id))");
    }

    private void insertValues(SQLiteDatabase database){
        database.execSQL("INSERT INTO CurrencyType(type) VALUES (\"Colones\")");
        database.execSQL("INSERT INTO CurrencyType(type) VALUES (\"Dolares\")");
        database.execSQL("INSERT INTO ReserveType(type) VALUES (\"Retiro\")");
        database.execSQL("INSERT INTO ReserveType(type) VALUES (\"Emergencias\")");
        database.execSQL("INSERT INTO ReserveType(type) VALUES (\"Caprichos\")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}
}
