package com.abzave.finances.lib;

public interface IConstants {

    String ENTRY_STRING = "entry";
    String DATABASE_NAME = "Finances";
    String IMCOMPLETE_CONTENT_MESSAGE = "Debe completar todos los campos";
    String AMOUNT = "amount";
    String DESCRIPTION = "description";
    String CURRENCY = "currency";
    String NO_NULL_COLUMNS = null;
    String INSERTION_FAILED_MESSAGE = "Falló la inserción";
    String COLONES = "Colones";
    String DOLLARS = "Dolares";
    String NO_REGISTERS_MESSAGE = "No hay registros aún";
    String MONEY_FORMAT = "%,.2f";
    String CONTEXT = "context";
    String TYPE = "type";
    String RETIREMENT = "Retiro";
    String EMERGENCIES = "Emergencias";
    String WHIMS = "Caprichos";
    String ENTRIES_CHART_NAME = "Ingresos";
    float RETIREMENT_PERCENTAGE = 0.05f;
    float EMERGENCIES_PERCENTAGE = 0.1f;
    float WHIMS_PERCENTAGE = 0.1f;
    short TOTAL_AMOUNT_CONTEXT = 0;
    short EXPENDED_CONTEXT = 1;
    short REMAINING_CONTEXT = 2;
    short RETIREMENT_CONTEXT = 3;
    short EMERGENCIES_CONTEXT = 4;
    short WHIMS_CONTEXT = 5;
    short ENTRIES_CONTEXT = 6;
    boolean IS_ENTRY = true;

}
