package com.expedia.www.red.carpet.configuration

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.Validate

class AWSEnvironment @JvmOverloads constructor(private val properties: Map<String, String> = System.getenv()) {

    init {
        Validate.notNull(properties)
    }

    val awsRegion: String
        get() = properties[AWS_REGION] ?: ""

    val awsDnsZone: String
        get() = properties[AWS_DNS_ZONE] ?: ""

    val isValid: Boolean
        get() = StringUtils.isNotEmpty(awsRegion) && StringUtils.isNotEmpty(awsDnsZone)

    fun buildApplicationFQDN(appName: String): String {
        check()
        Validate.notEmpty(appName)
        return String.format("%s.%s.%s", appName, awsRegion,
                awsDnsZone)
    }

    private fun check() {
        if (!isValid) {
            throw IllegalStateException("This is not a valid AWS environment")
        }
    }

    companion object {
        val AWS_REGION = "AWS_REGION"
        val AWS_DNS_ZONE = "EXPEDIA_DNS_ZONE"
    }
}