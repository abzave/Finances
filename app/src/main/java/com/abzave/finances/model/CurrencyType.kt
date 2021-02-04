package com.abzave.finances.model

import android.content.Context

class CurrencyType(values: HashMap<String, Any?>) : IBaseModel() {

    override val columns: HashMap<String, Any?> = hashMapOf()
    override val tableName: String = "CurrencyType"
    override val primaryKey: String = "id"

    var frozen: Boolean = false

    init {
        for ((column, value) in values) {
            this.columns[column] = value
        }
    }

    override fun isFrozen(): Boolean {
        return this.frozen
    }

    override fun freeze(): IBaseModel {
        this.frozen = true
        return this
    }

    override fun cloneAsThawed(): IBaseModel {
        return this
    }

    companion object : IBaseModelStatic() {
        override val tableName: String = "CurrencyType"
        override val primaryKey: String = "id"

        fun find(context: Context, findValue: Any): ArrayList<CurrencyType> {
            return findGeneric(context, findValue)
        }

        fun all(context: Context): ArrayList<CurrencyType> {
            return allGeneric(context)
        }

        fun findOne(context: Context, findValue: Any): CurrencyType {
            return findOneGeneric(context, findValue)
        }

        fun first(context: Context): CurrencyType {
            return firstGeneric(context)
        }

        fun findBy(context: Context, findValue: Pair<String, Any?>): ArrayList<CurrencyType> {
            return findByGeneric(context, findValue)
        }

        fun where(context: Context, whereValue: HashMap<String, Any?>): ArrayList<CurrencyType> {
            return whereGeneric(context, whereValue)
        }
    }

}