package com.abzave.finances.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.abzave.finances.lib.IConstants;

public interface IDataBaseConnection extends IConstants {

    String SUM_OF_ENTRIES_QUERY = "SELECT SUM(amount) FROM Entry GROUP BY currency";
    String SUM_OF_EXPENDITURES_QUERY = "SELECT SUM(amount) FROM Expenditure GROUP BY currency";
    String CURRENCY_TYPE_QUERY = "SELECT id FROM CurrencyType WHERE type = ";
    String EXPENDITURES_QUERY = "SELECT Expenditure.amount, Expenditure.description, " +
                                "CurrencyType.type FROM Expenditure INNER JOIN CurrencyType on " +
                                "Expenditure.currency = CurrencyType.id";

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

}
