package com.abzave.finances.lib;

public interface IConstants {

    String ENTRY_STRING = "entry";
    String IMCOMPLETE_CONTENT_MESSAGE = "Debe completar todos los campos";
    String AMOUNT = "amount";
    String DESCRIPTION = "description";
    String CURRENCY = "currency";
    String DATE = "date";
    String NO_NULL_COLUMNS = null;
    String COLONES = "Colones";
    String DOLLARS = "Dolares";
    String NO_REGISTERS_MESSAGE = "No hay registros aún";
    String MONEY_FORMAT = "%,.2f";
    String CONTEXT = "context";
    String ROOT_DIRECTORY = null;
    String CREATED_TEXT = "¡Creado!";
    int WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    int BACKGROUND_COLOR = 0x111A23;
    short TOTAL_AMOUNT_CONTEXT = 0;
    short EXPENDED_CONTEXT = 1;
    short REMAINING_CONTEXT = 2;
    short RETIREMENT_CONTEXT = 3;
    short EMERGENCIES_CONTEXT = 4;
    short WHIMS_CONTEXT = 5;
    short ENTRIES_CONTEXT = 6;
    short EXPENDITURES_CONTEXT = 7;
    boolean IS_ENTRY = true;
}
