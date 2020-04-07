package com.abzave.finances.dataBase;

import android.content.Context;
import android.database.Cursor;
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
                                      "CurrencyType(id))";
    String CREATE_ENTRY_TABLE = "CREATE TABLE IF NOT EXISTS Entry(id INTEGER PRIMARY KEY " +
                                "AUTOINCREMENT, amount REAL, description TEXT, currency INTEGER, " +
                                "FOREIGN KEY(currency) REFERENCES currencyType(id))";
    String CREATE_RESERVE_TYPE_TABLE = "CREATE TABLE IF NOT EXISTS ReserveType(id INTEGER " +
                                       "PRIMARY KEY AUTOINCREMENT, type TEXT)";
    String CREATE_RESERVE_TABLE = "CREATE TABLE IF NOT EXISTS Reserve(id INTEGER PRIMARY KEY " +
                                  "AUTOINCREMENT, type INT, amount REAL, entry INT, " +
                                  "FOREIGN KEY(type) REFERENCES ReserveType(id), " +
                                   "FOREIGN KEY(entry) REFERENCES Entry(id))";
    String INSERT_COLONES_TYPE = "INSERT INTO CurrencyType(type) VALUES (\"Colones\")";
    String INSERT_DOLLARS_TYPE = "INSERT INTO CurrencyType(type) VALUES (\"Dolares\")";
    String INSERT_RETIREMENT_TYPE = "INSERT INTO ReserveType(type) VALUES (\"Retiro\")";
    String INSERT_EMERGENCIES_TYPE = "INSERT INTO ReserveType(type) VALUES (\"Emergencias\")";
    String INSERT_WHIMS_TYPE = "INSERT INTO ReserveType(type) VALUES (\"Caprichos\")";
    String SUM_OF_ENTRIES_QUERY = "SELECT SUM(amount) FROM Entry GROUP BY currency";
    String SUM_OF_EXPENDITURES_QUERY = "SELECT SUM(amount) FROM Expenditure GROUP BY currency";
    String SUM_OF_RESERVE_QUERY = "SELECT SUM(Reserve.amount) FROM Reserve INNER JOIN Entry " +
                                  "ON Reserve.entry = Entry.id WHERE type = ? GROUP BY " +
                                  "Entry.currency";
    String SUM_OF_ENTRIES_QUERY_BY_DESCRIPTION = "SELECT SUM(amount), description FROM Entry GROUP BY " +
                                                 "currency, description";
    String SUM_OF_EXPENDITURES_QUERY_BY_DESCRIPTION = "SELECT SUM(amount), description FROM " +
                                                      "Expenditure GROUP BY currency, description";
    String CURRENCY_TYPE_QUERY = "SELECT id FROM CurrencyType WHERE type = ?";
    String RESERVE_TYPE_QUERY = "SELECT id FROM ReserveType WHERE type = ?";
    String EXPENDITURES_QUERY = "SELECT Expenditure.amount, Expenditure.description, " +
                                "CurrencyType.type FROM Expenditure INNER JOIN CurrencyType on " +
                                "Expenditure.currency = CurrencyType.id";
    String ENTRY_TABLE = "Entry";
    String EXPENDITURE_TABLE = "Expenditure";
    String RESERVE_TABLE = "Reserve";
    String DATABASE_PATH = "/data/com.abzave.finances/databases/";
    Cursor NO_CURSOR = null;
    String[] NO_SELECTION_ARGUMENTS = null;
    long NO_INSERTION = -1;
    int DATABASE_VERSION = 1;
    int ID_COLUMN = 0;
    int DESCRIPTION_COLUMN = 1;
    int AMOUNT_COLUMN = 0;
    int CURRENCY_COLUMN = 2;
    int SUM_COLUMN = 0;
    SQLiteDatabase.CursorFactory CURSOR_FACTORY = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    default SQLiteDatabase getDataBaseReader(Context context){
        DataBase dataBase = new DataBase(context, DATABASE_NAME, CURSOR_FACTORY, DATABASE_VERSION);
        return dataBase.getReadableDatabase();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    default SQLiteDatabase getDataBaseWriter(Context context){
        DataBase dataBase = new DataBase(context, DATABASE_NAME, CURSOR_FACTORY, DATABASE_VERSION);
        return dataBase.getWritableDatabase();
    }

    default int getId(SQLiteDatabase database, String query, String value){
        String[] whereValues = {value};
        Cursor cursor = database.rawQuery(query, whereValues);
        cursor.moveToFirst();
        int id = cursor.getInt(ID_COLUMN);
        cursor.close();
        return id;
    }

}