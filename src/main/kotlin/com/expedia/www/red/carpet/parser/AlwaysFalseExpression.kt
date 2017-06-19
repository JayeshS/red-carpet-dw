package com.expedia.www.red.carpet.parser

class AlwaysFalseExpression : Expression() {
    override fun eval(input: Map<String, String>): Boolean {
        return false
    }
}