package com.abzave.finances.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.abzave.finances.lib.IConstants;

public interface IDataBaseConnection extends IConstants {

    String DATABASE_NAME = "Finances";
    String CREATE_CURRENCY_TYPE_TABLE = "CREATE TABLE IF NOT EXISTS CurrencyType(id INTEGER " +
                                        "PRIMARY KEY AUTOINCREMENT, type TEXT)";
    String CREATE_EXPENDITURE_TABLE = "CREATE TABLE IF NOT EXISTS Expenditure(id INTEGER " +
                                      "PRIMARY KEY AUTOINCREMENT, amount REAL, description TEXT, " +
                                      "currency INTEGER, FOREIGN KEY(currency) REFERENCES " +
                                      "CurrencyType(id)), date DATE DEFAULT DATE('now')";
    String CREATE_ENTRY_TABLE = "CREATE TABLE IF NOT EXISTS Entry(id INTEGER PRIMARY KEY " +
                                "AUTOINCREMENT, amount REAL, description TEXT, currency INTEGER, " +
                                "FOREIGN KEY(currency) REFERENCES currencyType(id)), date DATE " +
                                "DEFAULT DATE('now')";
    String INSERT_COLONES_TYPE = "INSERT INTO CurrencyType(type) VALUES (\"Colones\")";
    String INSERT_DOLLARS_TYPE = "INSERT INTO CurrencyType(type) VALUES (\"Dolares\")";
    String DATABASE_PATH = "/data/com.abzave.finances/databases/";
    String[] NO_SELECTION_ARGUMENTS = null;
    int DATABASE_VERSION = 1;
    int DESCRIPTION_COLUMN_IN_SUM = 1;
    int AMOUNT_COLUMN = 0;
    int CURRENCY_COLUMN = 2;
    int SUM_COLUMN = 0;
    int EXPENDITURES_DESCRIPTION_COLUMN = 1;
    int DATE_COLUMN = 2;
    int ID_VIEW_COLUMN = 3;

    @RequiresApi(api = Build.VERSION_CODES.N)
    default SQLiteDatabase getDataBaseReader(Context context){
        DataBase dataBase = new DataBase(context, DATABASE_NAME, null, DATABASE_VERSION);
        return dataBase.getReadableDatabase();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    default SQLiteDatabase getDataBaseWriter(Context context){
        DataBase dataBase = new DataBase(context, DATABASE_NAME, null, DATABASE_VERSION);
        return dataBase.getWritableDatabase();
    }

}
