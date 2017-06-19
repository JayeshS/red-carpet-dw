package com.expedia.www.red.carpet.resources

import com.expedia.www.red.carpet.PrimerDropwizardAppRule
import com.expedia.www.red.carpet.Service
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.dropwizard.testing.ConfigOverride.config
import io.dropwizard.testing.ResourceHelpers.resourceFilePath
import org.glassfish.jersey.client.JerseyClientBuilder.createClient
import org.glassfish.jersey.internal.util.Base64
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.ClassRule
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ThreadLocalRandom
import javax.ws.rs.client.Entity
import javax.ws.rs.core.HttpHeaders

class TryResourceTest {

    companion object {

        private val grafanaPort = ThreadLocalRandom.current().nextInt(1000, 50000)
        private val seyrenPort = ThreadLocalRandom.current().nextInt(1000, 50000)

        @ClassRule
        @JvmField
        val GRAFANA_RULE = WireMockRule(grafanaPort)

        @ClassRule
        @JvmField
        val SEYREN_RULE = WireMockRule(seyrenPort)

        @ClassRule
        @JvmField
        val APP_RULE = PrimerDropwizardAppRule(Service::class.java,
                resourceFilePath("config/dev.yml"),
                config("seyren.endpoint", "http://localhost:" + seyrenPort),
                config("grafana.endpoint", "http://localhost:" + grafanaPort))

    }

    private val APP_URL = "http://localhost:8080"

    private var client = createClient().target(APP_URL)

    private val API_USER = basicAuth("apiUser:apiKey")

    @Test
    fun shouldPostDashboardToGrafanaOnly() {

        // given
        GRAFANA_RULE.stubFor(
                post(urlPathEqualTo("/api/dashboards/db"))
                        .willReturn(ResponseDefinitionBuilder().withBody("""{"status": "success"}""").withStatus(200).withHeader("Content-Type", "application/json")))


        val payload = String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("basic-dashboard.yml").toURI())));

        // when
        val getDevicesResponse = client?.path("dashboard")?.request()?.header(HttpHeaders.AUTHORIZATION, API_USER)?.post(Entity.entity(payload, "text/plain"))

        // then
        assertThat(getDevicesResponse?.status, equalTo(200))

    }

    fun basicAuth(auth: String): String {
        return "Basic " + String(Base64.encode(auth.toByteArray()))
    }

}
