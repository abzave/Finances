package com.abzave.finances;

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
    String CURRENCY_TYPE_QUERY = "SELECT id FROM CurrencyType WHERE type = ";
    String EXPENDITURES_QUERY = "SELECT Expenditure.amount, Expenditure.description, " +
                                "CurrencyType.type FROM Expenditure INNER JOIN CurrencyType on " +
                                "Expenditure.currency = CurrencyType.id";
    String NO_REGISTERS_MESSAGE = "No hay registros aún";
    int DATABASE_VERSION = 1;
    int ID_COLUMN = 0;
    int DESCRIPTION_COLUMN = 1;
    int AMOUNT_COLUMN = 0;
    int CURRENCY_COLUMN = 2;
    boolean IS_ENTRY = true;
    SQLiteDatabase.CursorFactory CURSOR_FACTORY = null;

}
