package com.expedia.www.red.carpet.parser

class InListExpression : Expression() {
    var varname: String = ""
    var list: MutableList<String> = mutableListOf()

    override fun eval(input: Map<String, String>): Boolean = input.any { entry ->
        entry.key == varname && list.contains(entry.value)
    }

    companion object Builder {

        fun setVarname(varname: String, expr: Expression): Boolean {
            if (expr is InListExpression) {
                expr.varname = varname
                return true
            }
            return false
        }

        fun addToList(value: String, expr: Expression): Boolean {
            if (expr is InListExpression) {
                expr.list.add(value)
                return true
            }
            return false
        }

    }

}