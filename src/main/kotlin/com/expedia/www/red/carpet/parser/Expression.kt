package com.expedia.www.red.carpet.parser

abstract class Expression {

    abstract fun eval(input: Map<String, String>): Boolean
}