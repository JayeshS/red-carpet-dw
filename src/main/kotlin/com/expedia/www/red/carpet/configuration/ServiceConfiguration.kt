package com.expedia.www.red.carpet.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore
import io.dropwizard.Configuration

class ServiceConfiguration : io.dropwizard.Configuration() {

    @com.fasterxml.jackson.annotation.JsonIgnore
    var appEnvironment: ApplicationEnvironment

    init {
        appEnvironment = ApplicationEnvironment()
    }

}
