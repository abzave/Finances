package com.abzave.finances;

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
    String CURRENCY_TYPE_QUERY = "select id from CurrencyType where type = ";
    int DATABASE_VERSION = 1;
    int ID_COLUMN = 0;
    boolean IS_ENTRY = true;

}
