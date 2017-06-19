package com.expedia.www.red.carpet.parser

class ConjunctionExpression(var expr1: Expression = AlwaysFalseExpression(), var expr2: Expression = AlwaysFalseExpression(), var conjunction: String = "") : Expression() {

    override fun eval(input: Map<String, String>): Boolean {
        if (conjunction == " or ") {
            return expr1.eval(input) || expr2.eval(input)
        }
        if (conjunction == " and ") {
            return expr1.eval(input) && expr2.eval(input)
        }
        throw IllegalArgumentException("Unexpected conjunction : $conjunction")
    }

    companion object Builder {

        fun setExprs(left: Expression, conjunctionExpr: Expression, right: Expression): Boolean {
            if (conjunctionExpr is ConjunctionExpression) {
                conjunctionExpr.expr1 = left
                conjunctionExpr.expr2 = right
                return true
            }
            throw IllegalStateException()
        }

        fun setConjunction(value: String, expr: Expression): Boolean {
            if (expr is ConjunctionExpression) {
                expr.conjunction = value
                return true
            }
            return false
        }

    }

}