package com.abzave.finances.controller

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.abzave.finances.lib.IConstants.*
import com.abzave.finances.model.*
import com.abzave.finances.model.database.IDataBaseConnection
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance

class EntriesController : IDataBaseConnection {

    companion object : BaseController() {

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
            val currency: Int = CurrencyController.getId(context, currencyName)
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
         * Returns a new query with the information
         * isEntry = true, use model Entry
         * isEntry = false, use model Expenditure
         * @param context Application context
         * @param isEntry Model to use
         * @param selection Column to select on the query
         * @param condition Condition for where on the query
         * @param group Grouping for query
         */
        fun get(
                context: Context,
                isEntry: Boolean,
                selection: String? = null,
                condition: String? = null,
                group: String? = null
        ) : ArrayList<ArrayList<Any?>> {
            val query: QueryModel = (if (isEntry) Entry else Expenditure)
                    .select(selection ?: "*")

            if (!condition.isNullOrBlank()) {
                query.where(condition)
            }
            if (!group.isNullOrBlank()) {
                query.group(group)
            }

            return query.getAll(context)
        }

    }

}