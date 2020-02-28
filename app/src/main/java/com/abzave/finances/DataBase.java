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
        database.execSQL("create table CurrencyType(id tinyint primary key autoincrement, " +
                         "type text)");
        database.execSQL("create table Expenditure(id int primary key autoincrement, " +
                         "amount real, description text, currency tinyint,  " +
                         "foreign key(currency) references CurrencyType(id))");
        database.execSQL("create table Entry(id int primary key autoincrement, amount real, " +
                         "description text, currency tinyint,  foreign key(currency) " +
                         "references currencyType(id))");
        database.execSQL("create table ReserveType(id tinyint primary key autoincrement, " +
                         "type text)");
        database.execSQL("create table Reserve(id int primary key autoincrement, type tinyint, " +
                         "amount real, entry int, foreign key(type) references ReserveType(id), " +
                         "foreign key(entry) references Entry(id))");
    }

    private void insertValues(SQLiteDatabase database){
        database.execSQL("insert into CurrencyType(type) values (\"Colones\")");
        database.execSQL("insert into CurrencyType(type) values (\"Dolares\")");
        database.execSQL("insert into ReserveType(type) values (\"Retiro\")");
        database.execSQL("insert into ReserveType(type) values (\"Emergencias\")");
        database.execSQL("insert into ReserveType(type) values (\"Caprichos\")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}
}
