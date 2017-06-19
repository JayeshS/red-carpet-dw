package com.expedia.www.red.carpet.resources

import com.codahale.metrics.annotation.Timed
import com.expedia.www.red.carpet.parser.Expression
import com.expedia.www.red.carpet.parser.Parser
import com.expedia.www.red.carpet.view.TryView
import io.dropwizard.views.View
import org.parboiled.Parboiled
import org.parboiled.parserunners.ReportingParseRunner
import org.slf4j.LoggerFactory
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap

@Path("/try")
@Produces(MediaType.TEXT_HTML)
class TryResource(private val parser: RuleParser) {

    private val log = LoggerFactory.getLogger(TryResource::class.java)
    private val ruleParser = RuleParser()

    @GET @Path("/")
    @Timed
    fun get(): View {
        log.info("Get received")
        return TryView()
    }

    @POST @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Timed @Path("/")
    fun post(data: MultivaluedMap<String, String>): View {
        log.info("data is $data")
        val varNames = data.get("varname")
        val varValues = data.get("value")

        val zip = varNames?.zip(varValues!!.toTypedArray())
        val mp = zip!!.associateBy({ it.first }, { p -> p.second })

        val result = ruleParser.createRule(data.getFirst("ruleText")).eval(mp)
        log.info("Result was $result")
        return TryView()
    }
}

class RuleParser {
    fun createRule(input: String): Expression {
        val prsr: Parser = Parboiled.createParser(Parser::class.java)
        val reportingParseRunner = ReportingParseRunner<Expression>(prsr.Expr())
        val result = reportingParseRunner.run(input)
        if (result.hasErrors()) println(result.parseErrors)
        return result.resultValue
    }
}
