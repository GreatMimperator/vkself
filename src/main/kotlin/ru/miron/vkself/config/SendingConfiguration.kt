package ru.miron.vkself.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate

@Configuration
@Profile("!confirmation")
class SendingConfiguration(
    @Value("\${vk.access-token}") val accessToken: String,
    val jackson2HttpMessageConverter: MappingJackson2HttpMessageConverter
) {

    @Bean
    fun vkMethodPathRestClient(): RestClient {
        return RestClient.builder()
            .defaultHeader("Authorization", "Bearer %s".format(accessToken))
            .build()
    }

    @Bean
    fun vkMethodPathRestTemplate(): RestTemplate {
        return RestTemplate()
    }
}