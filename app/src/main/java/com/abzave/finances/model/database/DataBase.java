package com.abzave.finances.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper implements IDataBaseConnection{


    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createTables(database);
        insertValues(database);
    }

    private void createTables(SQLiteDatabase database){
        database.execSQL(CREATE_CURRENCY_TYPE_TABLE);
        database.execSQL(CREATE_EXPENDITURE_TABLE);
        database.execSQL(CREATE_ENTRY_TABLE);
    }

    private void insertValues(SQLiteDatabase database){
        database.execSQL(INSERT_COLONES_TYPE);
        database.execSQL(INSERT_DOLLARS_TYPE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}
}
