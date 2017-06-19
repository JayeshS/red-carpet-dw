package com.expedia.www.red.carpet.parser

class PercentExpression : Expression() {
    var varname: String = ""
    var percent: Int = 0

    override fun eval(input: Map<String, String>): Boolean = input.any { entry ->
        entry.key == varname && percentMatches(entry)
    }

    private fun percentMatches(entry: Map.Entry<String, String>): Boolean {
        if (percent == 0) return false
        return entry.value.hashCode() % (100 / percent) == 0
    }

    companion object Builder {

        fun setVarname(varname: String, percExpr: Expression): Boolean {
            if (percExpr is PercentExpression) {
                percExpr.varname = varname
                return true
            }
            return false
        }

        fun setPerc(perc: String, percExpr: Expression): Boolean {
            if (percExpr is PercentExpression) {
                percExpr.percent = perc.toInt()
                return true
            }
            return false
        }

    }

}