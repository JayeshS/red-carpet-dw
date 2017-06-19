package com.expedia.www.red.carpet

import com.expedia.www.platform.isactive.providers.FileBasedActiveVersionProvider
import com.expedia.www.platform.isactive.resources.BuildInfoResource
import com.expedia.www.platform.isactive.resources.IsActiveResource
import com.expedia.www.red.carpet.configuration.ServiceConfiguration
import com.expedia.www.red.carpet.resources.RuleParser
import com.expedia.www.red.carpet.resources.TryResource
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.views.ViewBundle
import org.apache.commons.io.IOUtils
import java.io.IOException
import java.io.StringWriter

class Service : Application<ServiceConfiguration>() {

    override fun initialize(bootstrap: Bootstrap<ServiceConfiguration>) {
        bootstrap.addBundle(ViewBundle())
    }

    @Throws(Exception::class)
    override fun run(serviceConfiguration: ServiceConfiguration, environment: Environment) {
        addCommonResources(environment)
        addApplicationResources(environment)
    }

    private fun addApplicationResources(environment: Environment) {
        environment.objectMapper.registerModule(KotlinModule())
        environment.jersey().register(TryResource(RuleParser()))

    }

    @Throws(IOException::class)
    fun addCommonResources(environment: Environment) {
        val activeVersionProvider = FileBasedActiveVersionProvider()

        var buildInfoJson: String = ""
        val classLoader = Service::class.java.classLoader
        classLoader.getResourceAsStream("buildInfo.json").use { inputStream ->
            val writer = StringWriter()
            IOUtils.copy(inputStream, writer, "UTF-8")
            buildInfoJson = writer.toString()
        }

        val buildInfoResource = BuildInfoResource(buildInfoJson)
        environment.jersey().register(buildInfoResource)

        val isActiveResource = IsActiveResource(buildInfoResource, activeVersionProvider)
        environment.jersey().register(isActiveResource)
    }

    companion object {

        @Throws(Exception::class)
        @JvmStatic fun main(args: Array<String>) {
            Service().run(*args)
        }
    }
}
