package com.abzave.finances.model

import android.content.Context
import android.database.Cursor
import com.abzave.finances.model.database.IDataBaseConnection

class QueryModel(private val table: String): IDataBaseConnection {

    private var selections: String = ""
    private var conditions: String = ""
    private var groups: String = ""
    private var joins: String = ""
    private var limitValue: Int = -1
    private var offsetValue: Int = -1

    fun select(selection: String): QueryModel {
        selections += getNewData(selections, selection, ", ")
        return this
    }

    fun where(condition: String): QueryModel {
        conditions += getNewData(conditions, condition, " AND ")
        return this
    }

    fun group(by: String): QueryModel {
        groups += getNewData(groups, by, ", ")
        return this
    }

    fun join(type: String): QueryModel {
        joins += getNewData(joins, type, " ")
        return this
    }

    /**
     * Adds a limit instruction to the currently stored query
     * @param value number of records to retrieve
     * @return current object so we can concatenate methods
     */
    fun limit(value: Int): QueryModel {
        limitValue = value
        return this
    }

    /**
     * Adds a offset instruction to the currently stored query
     * @param value number of records to skip
     * @return current object so we can concatenate methods
     */
    fun offset(value: Int): QueryModel {
        offsetValue = value
        return this
    }

    fun <Type> get(context: Context, columnName: String): ArrayList<Type?> {
        if (selections.isEmpty()) {
            return arrayListOf()
        }

        val columns = selections.split(", ")
        val selectColumn = columns.find { column -> column.contains(columnName)}
        val columnIndex = columns.indexOf(selectColumn)

        val database = getDataBaseWriter(context)
        val cursor = database.rawQuery(toString(), null)
        val values = arrayListOf<Type?>()

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            values.add(valueFromCursor(cursor, columnIndex) as Type?)
            cursor.moveToNext()
        }
        cursor.close()

        return values
    }

    override fun toString(): String {
        var query = "SELECT $selections FROM $table "

        if (joins.isNotEmpty()) {
            query += "$joins "
        }
        if (conditions.isNotEmpty()) {
            query += "WHERE $conditions "
        }
        if (groups.isNotEmpty()) {
            query += "GROUP BY $groups "
        }
        if (limitValue > 0) {
            query += "LIMIT $limitValue "
        }
        if (offsetValue > 0) {
            query += "OFFSET $offsetValue "
        }

        return query
    }

    private fun getNewData(section: String, content: String, querySeparator: String): String {
        val separator = if (section.isEmpty()) "" else querySeparator
        return "$separator$content"
    }

    private fun valueFromCursor(cursor: Cursor, columnIndex: Int): Any? {

        when (cursor.getType(columnIndex)) {
            Cursor.FIELD_TYPE_NULL -> return null
            Cursor.FIELD_TYPE_BLOB -> return cursor.getBlob(columnIndex)
            Cursor.FIELD_TYPE_FLOAT -> return  cursor.getFloat(columnIndex)
            Cursor.FIELD_TYPE_INTEGER -> return  cursor.getInt(columnIndex)
            Cursor.FIELD_TYPE_STRING -> return cursor.getString(columnIndex)
        }
        return null
    }

}