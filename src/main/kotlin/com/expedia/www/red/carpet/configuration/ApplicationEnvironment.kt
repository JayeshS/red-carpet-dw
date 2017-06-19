package com.expedia.www.red.carpet.configuration

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.Validate
import java.util.*

class ApplicationEnvironment @JvmOverloads constructor(properties: Properties = System.getProperties(), private val awsEnvironment: AWSEnvironment = AWSEnvironment()) {

    val applicationName: String
    val applicationHome: String
    val applicationEnvironment: String
    val applicationVersion: String
    val applicationBuildBranch: String
    val applicationBuildTime: String

    init {
        Validate.notNull(properties)
        Validate.notNull(awsEnvironment)

        this.applicationName = properties.getProperty(APPLICATION_NAME)
        this.applicationHome = properties.getProperty(APPLICATION_HOME)
        this.applicationEnvironment = properties.getProperty(APPLICATION_ENVIRONMENT)

        Validate.isTrue(StringUtils.isNotBlank(this.applicationName),
                applicationName + " is a required property")
        Validate.isTrue(StringUtils.isNotBlank(this.applicationHome),
                APPLICATION_HOME + " is a required property")
        Validate.isTrue(StringUtils.isNotBlank(this.applicationEnvironment),
                APPLICATION_ENVIRONMENT + " is a required property")

        this.applicationVersion = checkAndSet(properties, APPLICATION_BUILD_VERSION, "not-available")
        this.applicationBuildBranch = checkAndSet(properties, APPLICATION_BUILD_BRANCH, "unknown")
        this.applicationBuildTime = checkAndSet(properties, APPLICATION_BUILD_TIME, "unknown")
    }

    private fun checkAndSet(properties: Properties, propertyName: String, defaultValue: String): String {
        val value = properties.getProperty(propertyName)
        if (StringUtils.isEmpty(value)) {
            return defaultValue
        }
        return value
    }

    val shortApplicationVersion: String
        get() {
            if (applicationVersion.length > 6) {
                return applicationVersion.substring(0, 6)
            }
            return applicationVersion
        }

    val isAWSEnvironment: Boolean
        get() = awsEnvironment.isValid

    fun getAwsEnvironment(): AWSEnvironment {
        if (isAWSEnvironment) {
            return awsEnvironment
        }
        throw NotAnAWSEnvironmentException()
    }

    val isProduction: Boolean
        get() = applicationEnvironment == "prod"

    val isDevelopment: Boolean
        get() = applicationEnvironment == "dev"

    class NotAnAWSEnvironmentException : RuntimeException("Application is not running in an AWS Environment. Please use isAWSEnvironment() before accessing cloud environment")

    companion object {

        val APPLICATION_NAME = "application.name"
        val APPLICATION_ENVIRONMENT = "application.environment"
        val APPLICATION_HOME = "application.home"
        val APPLICATION_BUILD_VERSION = "application.build.version"
        val APPLICATION_BUILD_TIME = "application.build.time"
        val APPLICATION_BUILD_BRANCH = "application.build.branch"
    }
}