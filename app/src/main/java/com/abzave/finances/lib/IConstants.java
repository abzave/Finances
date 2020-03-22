package com.abzave.finances.lib;

import android.database.sqlite.SQLiteDatabase;

public interface IConstants {

    String ENTRY_STRING = "entry";
    String DATABASE_NAME = "Finances";
    String IMCOMPLETE_CONTENT_MESSAGE = "Debe completar todos los campos";
    String AMOUNT = "amount";
    String DESCRIPTION = "description";
    String CURRENCY = "currency";
    String ENTRY_TABLE = "Entry";
    String EXPENDITURE_TABLE = "Expenditure";
    String NO_NULL_COLUMNS = null;
    String INSERTION_FAILED_MESSAGE = "Falló la inserción";
    String COLONES = "Colones";
    String DOLLARS = "Dolares";
    String NO_REGISTERS_MESSAGE = "No hay registros aún";
    String MONEY_FORMAT = "%,.2f";
    int DATABASE_VERSION = 1;
    int ID_COLUMN = 0;
    int DESCRIPTION_COLUMN = 1;
    int AMOUNT_COLUMN = 0;
    int CURRENCY_COLUMN = 2;
    int SUM_COLUMN = 0;
    boolean IS_ENTRY = true;
    SQLiteDatabase.CursorFactory CURSOR_FACTORY = null;

}
