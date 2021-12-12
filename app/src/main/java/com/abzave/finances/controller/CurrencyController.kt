package com.abzave.finances.controller

import android.content.Context
import com.abzave.finances.model.CurrencyType

class CurrencyController {

    companion object : BaseController() {

        /**
         *  Returns the record id of a currency base on the currency name
         *  @param context Application context
         *  @param currencyName Name of the currency to get the id of
         *  @return id of record for the currency given or -1 if it was not found
         */
        fun getId(context: Context, currencyName: String): Int {
            // Gets all the currency available
            val currencyQuery: Pair<String, String> = Pair("type", currencyName)
            val types: ArrayList<CurrencyType> = CurrencyType.findBy(context, currencyQuery)

            // Returns -1 if the currency does not exist
            if(types.isEmpty()) {
                return -1;
            }

            return types[0].get("id") as Int
        }

    }

}