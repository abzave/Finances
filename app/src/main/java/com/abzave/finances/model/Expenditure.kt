package com.abzave.finances.model

import android.content.Context

class Expenditure(values: HashMap<String, Any?>) : IBaseModel() {

    override val columns: HashMap<String, Any?> = hashMapOf()
    override val tableName: String = "Expenditure"
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
        override val tableName: String = "Expenditure"
        override val primaryKey: String = "id"

        fun find(context: Context, findValue: Any): ArrayList<Expenditure> {
            return findGeneric(context, findValue)
        }

        fun all(context: Context): ArrayList<Expenditure> {
            return allGeneric(context)
        }

        fun findOne(context: Context, findValue: Any): Expenditure {
            return findOneGeneric(context, findValue)
        }

        fun first(context: Context): Expenditure {
            return firstGeneric(context)
        }

        fun findBy(context: Context, findValue: Pair<String, Any?>): ArrayList<Expenditure> {
            return findByGeneric(context, findValue)
        }

        fun where(context: Context, whereValue: HashMap<String, Any?>): ArrayList<Expenditure> {
            return whereGeneric(context, whereValue)
        }
    }

}