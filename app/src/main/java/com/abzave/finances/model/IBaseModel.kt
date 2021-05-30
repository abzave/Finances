package com.abzave.finances.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.icu.util.Freezable
import com.abzave.finances.model.database.IDataBaseConnection
import org.json.JSONObject

abstract class IBaseModelStatic: IDataBaseConnection {

    abstract val tableName: String
    abstract val primaryKey: String

    protected inline fun <reified Model> findGeneric(context: Context, findValue: Any): ArrayList<Model> {
        val database = getDataBaseReader(context)
        val query = "SELECT * FROM $tableName WHERE $primaryKey IN " + getInList(findValue)
        val value = getElementsArray(findValue)

        val results = database.rawQuery(query, value)
        val params = elementsFromCursor(results)
        results.close()

        val elements = arrayListOf<Model>()
        for (elementParams in params) {
            elements.add(Model::class.constructors.first().call(elementParams))
        }
        return elements
    }

    fun delete(context: Context, deleteValue: Any) {
        val database = getDataBaseWriter(context)
        val whereCondition = "$primaryKey = ?"
        val whereValues = arrayOf(deleteValue as String)

        database.delete(tableName, whereCondition, whereValues)
    }

    fun create(context: Context, values: HashMap<String, Any?>) {
        val database = getDataBaseWriter(context)
        val valuesToInsert = columnsToValues(values)

        database.insert(this.tableName, IDataBaseConnection.NO_NULL_COLUMNS, valuesToInsert)
    }

    protected inline fun <reified Model> allGeneric(context: Context): ArrayList<Model> {
        val database = getDataBaseReader(context)
        val query = "SELECT * FROM $tableName"
        val results = database.rawQuery(query, IDataBaseConnection.NO_SELECTION_ARGUMENTS)
        val params = elementsFromCursor(results)
        results.close()

        val elements = arrayListOf<Model>()
        for (elementParams in params) {
            elements.add(Model::class.constructors.first().call(elementParams))
        }
        return elements
    }

    protected inline fun <reified Model> findOneGeneric(context: Context, findValue: Any): Model {
        val results = findGeneric<Model>(context, findValue)
        return results.first()
    }

    protected inline fun <reified Model> firstGeneric(context: Context): Model {
        val database = getDataBaseReader(context)
        val query = "SELECT * FROM $tableName LIMIT 1"
        val results = database.rawQuery(query, IDataBaseConnection.NO_SELECTION_ARGUMENTS)
        results.moveToFirst()
        val params = columnsFromCursor(results)
        results.close()

        return Model::class.constructors.first().call(params)
    }

    protected inline fun <reified Model> findByGeneric(context: Context, findValue: Pair<String, Any?>): ArrayList<Model> {
        val (column, columnValue) = findValue
        val database = getDataBaseReader(context)
        val query = "SELECT * FROM $tableName WHERE $column IN " + getInList(columnValue)
        val value = getElementsArray(findValue)

        val results = database.rawQuery(query, value)
        val params = elementsFromCursor(results)
        results.close()

        val elements = arrayListOf<Model>()
        for (elementParams in params) {
            elements.add(Model::class.constructors.first().call(elementParams))
        }
        return elements
    }

    protected inline fun <reified Model> whereGeneric(context: Context, whereValue: HashMap<String, Any?>): ArrayList<Model> {
        val database = getDataBaseReader(context)
        val query = "SELECT * FROM $tableName WHERE " + getColumnAssignmentList(whereValue.keys)
        val value = getElementsArray(whereValue.values as Array<*>)

        val results = database.rawQuery(query, value)
        val params = elementsFromCursor(results)
        results.close()

        val elements = arrayListOf<Model>()
        for (elementParams in params) {
            elements.add(Model::class.constructors.first().call(elementParams))
        }
        return elements
    }

    fun updateAll(context: Context, values: HashMap<String, Any?>) {
        val database = getDataBaseWriter(context)
        val whereCondition = null
        val contentValues = columnsToValues(values)

        database.update(this.tableName, contentValues, whereCondition, IDataBaseConnection.NO_SELECTION_ARGUMENTS)
    }

    fun destroyBy(context: Context, findValue: Pair<String, Any?>) {
        val (column, columnValue) = findValue
        val database = getDataBaseWriter(context)
        val whereCondition = "$column IN " + getInList(columnValue)
        val whereValues = getElementsArray(findValue)

        database.delete(tableName, whereCondition, whereValues)
    }

    fun destroyAll(context: Context) {
        val database = getDataBaseWriter(context)
        val whereCondition = null

        database.delete(tableName, whereCondition, IDataBaseConnection.NO_SELECTION_ARGUMENTS)
    }

    fun sum(columns: ArrayList<String>): QueryModel {
        return QueryModel(tableName).select(getSumColumns(columns))
    }

    fun select(selection: String): QueryModel {
        return QueryModel(tableName).select(selection)
    }

    protected fun columnsToValues(columns: HashMap<String, Any?>): ContentValues {
        val values = ContentValues()

        for ((name, value) in columns) {
            val canConvert = value != null
            if ( !canConvert ) {
                continue
            }

            when (value) {
                is Boolean -> values.put(name, value)
                is Byte -> values.put(name, value)
                is String -> values.put(name, value)
                is Int -> values.put(name, value)
                is Short -> values.put(name, value)
                is Long -> values.put(name, value)
                is Float -> values.put(name, value)
                is Double -> values.put(name, value)
            }
        }

        return values
    }

    protected fun columnsFromCursor(cursor: Cursor): HashMap<String, Any?> {
        val hash = hashMapOf<String, Any?>()
        for (column in 0 until cursor.columnCount) {
            val name = cursor.getColumnName(column)

            when (cursor.getType(column)) {
                Cursor.FIELD_TYPE_NULL -> hash[name] = null
                Cursor.FIELD_TYPE_BLOB -> hash[name] = cursor.getBlob(column)
                Cursor.FIELD_TYPE_FLOAT -> hash[name] = cursor.getFloat(column)
                Cursor.FIELD_TYPE_INTEGER -> hash[name] = cursor.getInt(column)
                Cursor.FIELD_TYPE_STRING -> hash[name] = cursor.getString(column)
            }
        }
        return hash
    }

    protected fun elementsFromCursor(cursor: Cursor): ArrayList<HashMap<String, Any?>> {
        val arrayList = arrayListOf<HashMap<String, Any?>>()
        cursor.moveToFirst()
        for (column in 0 until cursor.count) {
            arrayList.add(columnsFromCursor(cursor))
            cursor.moveToNext()
        }
        return arrayList
    }

    protected fun getInList(elements: Any?): String {
        var list = ""
        when (elements){
            is Array<*> -> for (element in 0 until elements.size) {
                list += "?,"
            }
            else -> list = "?,"
        }
        list = list.dropLast(1)
        return "($list)"
    }

    protected fun getElementsArray(elements: Any): Array<String> {
        val array: Array<String>
        when (elements){
            is Array<*> -> array = Array(elements.size) { elementIndex -> elements[elementIndex] as String}
            is Pair<*, *> -> array = arrayOf(elements.second as String)
            else -> array = arrayOf(elements as String)
        }
        return array
    }

    protected fun getColumnAssignmentList(columns: MutableSet<String>): String {
       return columns.joinToString(" = ? AND", "", " = ?")
    }

    protected fun getSumColumns(columns: ArrayList<String>): String {
        val sumColumns = columns.map { column -> "SUM($column)" }
        return sumColumns.joinToString(",")
    }

}

abstract class IBaseModel : IDataBaseConnection, Freezable<IBaseModel> {
    abstract val columns: HashMap<String, Any?>
    abstract val tableName: String
    abstract val primaryKey: String
    private val latestChanges: HashMap<String, Any?> = hashMapOf()

    fun save(context: Context): Boolean {
        if (this.isFrozen) {
            this.deleteOnSave(context)
            return false
        }

        val database = getDataBaseWriter(context)
        val whereCondition = "$primaryKey = ?"
        val whereValue = arrayOf(if (this.columns[this.primaryKey] != null) this.columns[this.primaryKey].toString() else "NULL")
        val contentValues = columnsToValues(if (this.latestChanges.isNotEmpty()) this.latestChanges else this.columns)

        val cursor = database.rawQuery("SELECT $primaryKey FROM $tableName WHERE $whereCondition", whereValue)
        val exists = cursor.moveToFirst()
        cursor.close()
        return if(exists) {
            val rows = database.update(this.tableName, contentValues, whereCondition, whereValue)
            rows > 0
        } else {
            val id = database.insert(this.tableName, IDataBaseConnection.NO_NULL_COLUMNS, contentValues)
            val success = id != (-1).toLong()
            this.columns[this.primaryKey] = if (success) id else null
            success
        }
    }

    fun update(values: HashMap<String, Any?>){
        if (this.isFrozen) {
            return
        }

        for ((column, value) in values) {
            this.latestChanges[column] = value
        }
    }

    fun destroy() {
        for (column in this.columns.keys) {
            this.latestChanges[column] = null
        }
        this.freeze()
    }

    fun asJson(): JSONObject {
        val json = JSONObject()

        for ((column, value) in this.columns) {
            json.put(column, value)
        }
        return json
    }

    fun changed(): Boolean {
        return this.latestChanges.isEmpty() || this.isFrozen
    }

    fun changes(): HashMap<String, Array<Any?>> {
        val changes = hashMapOf<String, Array<Any?>>()
        for ((column, value) in this.latestChanges) {
            changes[column] = arrayOf(this.columns[column], value)
        }
        return changes
    }

    fun fromJson(json: JSONObject) {
        for (key in json.keys()) {
            this.columns[key] = json[key]
        }
    }

    fun attributes(): MutableSet<String> {
        return this.columns.keys
    }

    fun get(column: String): Any? {
        if (this.latestChanges.containsKey(column)) {
            return this.latestChanges[column]
        }
        return this.columns[column]
    }

    protected fun columnsToValues(columns: HashMap<String, Any?>): ContentValues {
        val values = ContentValues()

        for ((name, value) in columns) {
            val canConvert = value != null
            if ( !canConvert ) {
                continue
            }

            when (value) {
                is Boolean -> values.put(name, value)
                is Byte -> values.put(name, value)
                is String -> values.put(name, value)
                is Int -> values.put(name, value)
                is Short -> values.put(name, value)
                is Long -> values.put(name, value)
                is Float -> values.put(name, value)
                is Double -> values.put(name, value)
            }
        }

        return values
    }

    protected fun columnsFromCursor(cursor: Cursor): HashMap<String, Any?> {
        val hash = hashMapOf<String, Any?>()
        for (column in 0 until cursor.columnCount) {
            val name = cursor.getColumnName(column)

            when (cursor.getType(column)) {
                Cursor.FIELD_TYPE_NULL -> hash[name] = null
                Cursor.FIELD_TYPE_BLOB -> hash[name] = cursor.getBlob(column)
                Cursor.FIELD_TYPE_FLOAT -> hash[name] = cursor.getFloat(column)
                Cursor.FIELD_TYPE_INTEGER -> hash[name] = cursor.getInt(column)
                Cursor.FIELD_TYPE_STRING -> hash.put(name, cursor.getString(column))
            }
        }
        return hash
    }

    private fun deleteOnSave(context: Context) {
        val database = getDataBaseWriter(context)
        val whereCondition = "$primaryKey = ?"
        val whereValues = arrayOf(this.columns[this.primaryKey] as String)

        database.delete(tableName, whereCondition, whereValues)
    }
}
