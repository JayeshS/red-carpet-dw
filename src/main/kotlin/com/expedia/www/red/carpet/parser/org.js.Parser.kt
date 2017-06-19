package com.expedia.www.red.carpet.parser

import org.parboiled.BaseParser
import org.parboiled.Rule
import org.parboiled.annotations.BuildParseTree

@BuildParseTree
open class Parser : BaseParser<Expression>() {

    open fun Expr() = Sequence(factor(), ZeroOrMore(
            push(ConjunctionExpression()),
            Conjunction(),
            ConjunctionExpression.setConjunction(match(), peek()),
            factor(),
            ConjunctionExpression.setExprs(pop(), peek(), pop(1))))

    open fun Conjunction() = FirstOf(or(), and())

    open fun factor() = FirstOf(Condition(), Parens())

    open fun Condition() = FirstOf(Percentage(), InCondition())

    open fun InCondition() = Sequence(
            push(InListExpression()),
            VarName(),
            InListExpression.setVarname(match(), peek()),
            colon(),
            list())

    open fun Percentage() = Sequence(
            push(PercentExpression()),
            VarName(),
            PercentExpression.setVarname(match(), peek()),
            colon(),
            Digits(),
            PercentExpression.setPerc(match(), peek()),
            '%'
    )

    open fun Parens(): Rule = Sequence('(', Expr(), ')')

    open fun list() = Sequence(
            Quote(),
            String(),
            InListExpression.addToList(match(), peek()),
            Quote(),
            ZeroOrMore(
                    COMMA(),
                    Quote(),
                    String(),
                    InListExpression.addToList(match(), peek()),
                    Quote()
            )
    )

    open fun Digits() = OneOrMore(CharRange('0', '9'))

    open fun Alnum() = OneOrMore(AnyOf("0123456789abcdefghijklmnopqrstuvwxyz"))

    open fun String(): Rule {
        return ZeroOrMore(
                Sequence(TestNot(AnyOf("\r\n\"\\")), BaseParser.ANY)
        ).suppressSubnodes()
    }

    open fun Quote() = fromCharLiteral('"')

    open fun VarName(): Rule = Sequence(
            alphabets(),
            ZeroOrMore(Alnum())
    )

    open fun or() = fromStringLiteral(" or ")
    open fun and() = fromStringLiteral(" and ")

    open fun alphabets(): Rule = OneOrMore(alphabet())

    open fun alphabet(): Rule = CharRange('a', 'z')

    open fun colon() = Sequence(fromCharLiteral(':'), WhiteSpace())

    open fun COMMA() = Sequence(AnyOf(","), WhiteSpace())

    open fun WhiteSpace(): Rule {
        return ZeroOrMore(AnyOf(" \t"))
    }
}
