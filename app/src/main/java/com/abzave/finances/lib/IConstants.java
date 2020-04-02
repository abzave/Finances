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
    String CONTEXT= "context";
    String TOTAL_AMOUNT_CONTEXT = "totalAmount";
    String EXPENDED_CONTEXT = "expended";
    String REMAINING_CONTEXT = "remaining";
    String RETIREMENT_CONTEXT = "retirement";
    String EMERGENCIES_CONTEXT = "emergencies";
    String WHIMS_CONTEXT = "whims";
    String TYPE = "type";
    String RETIREMENT = "Retiro";
    String EMERGENCIES = "Emergencias";
    String WHIMS = "Caprichos";
    String ENTRIES_CHART_NAME = "Ingresos";
    double RETIREMENT_PERCENTAGE = 0.05;
    double EMERGENCIES_PERCENTAGE = 0.1;
    double WHIMS_PERCENTAGE = 0.1;
    boolean IS_ENTRY = true;

}
