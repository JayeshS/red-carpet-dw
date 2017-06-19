package com.expedia.www.red.carpet

import com.expedia.www.red.carpet.configuration.ApplicationEnvironment
import com.expedia.www.red.carpet.configuration.ServiceConfiguration
import io.dropwizard.Application
import io.dropwizard.testing.ConfigOverride
import io.dropwizard.testing.junit.DropwizardAppRule


class PrimerDropwizardAppRule(applicationClass: Class<out Application<ServiceConfiguration>>?, configPath: String?, vararg configOverrides: ConfigOverride?) : DropwizardAppRule<ServiceConfiguration>(applicationClass, configPath, *configOverrides) {

    override fun before() {
        //  Work around structure of ApplicationEnvironment using system properties,
        // Default it for tests before DW spins up
        val properties = System.getProperties()
        if (properties.getProperty(ApplicationEnvironment.APPLICATION_NAME) == null) {
            properties.setProperty(ApplicationEnvironment.APPLICATION_NAME, "primer-service")
        }
        if (properties.getProperty(ApplicationEnvironment.APPLICATION_HOME) == null) {
            properties.setProperty(ApplicationEnvironment.APPLICATION_HOME, ".")
        }
        if (properties.getProperty(ApplicationEnvironment.APPLICATION_ENVIRONMENT) == null) {
            properties.setProperty(ApplicationEnvironment.APPLICATION_ENVIRONMENT, "dev")
        }
        super.before()
    }

}