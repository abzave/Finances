package com.abzave.finances.controller

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.abzave.finances.lib.IConstants.*
import com.abzave.finances.model.CurrencyType
import com.abzave.finances.model.Entry
import com.abzave.finances.model.Expenditure
import com.abzave.finances.model.IBaseModel
import com.abzave.finances.model.database.IDataBaseConnection
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EntriesController : IDataBaseConnection {

    companion object {

        /**
         *  Returns the record id of a currency base on the currency name
         *  @param context Application context
         *  @param currencyName Name of the currency to get the id of
         *  @return id of record for the currency given or -1 if it was not found
         */
        fun getCurrencyId(context: Context, currencyName: String): Int {
            // Gets all the currency available
            val currencyQuery: Pair<String, String> = Pair("type", currencyName)
            val types: ArrayList<CurrencyType> = CurrencyType.findBy(context, currencyQuery)

            // Returns -1 if the currency does not exist
            if(types.isEmpty()) {
                return -1;
            }

            return types[0].get("id") as Int
        }

        /**
         * Creates a new record in the entry table based on UI information
         * isEntry = true, use model Entry
         * isEntry = false, use model Expenditure
         * @param context Application context
         * @param amount Amount to be added in the entry record
         * @param description Description to be added in the entry record
         * @param currencyName Currency in which the report should be
         * @param isEntry Model to use
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun createNewEntry(
                context: Context,
                amount: String,
                description: String,
                currencyName: String,
                isEntry: Boolean
        ) {
            val currency: Int = getCurrencyId(context, currencyName)
            val values: HashMap<String, Any?> = hashMapOf(
                    AMOUNT to amount,
                    DESCRIPTION to description,
                    CURRENCY to currency,
                    DATE to getDate()
            )

            val model: IBaseModel = if (isEntry) Entry(values) else Expenditure(values)
            model.save(context)
        }

        /**
         * Get the current date with the format yyyy-mm-dd
         * @return today's date formatted as yyyy-mm-dd
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun getDate(): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val now = LocalDateTime.now()
            return formatter.format(now)
        }

    }

}