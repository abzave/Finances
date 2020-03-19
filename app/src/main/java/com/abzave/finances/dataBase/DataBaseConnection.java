package com.abzave.finances;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

public interface DataBaseConnection extends IConstants {

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
