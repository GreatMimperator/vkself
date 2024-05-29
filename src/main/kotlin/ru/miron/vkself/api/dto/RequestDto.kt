package ru.miron.vkself.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

data class RequestDto(
    @JsonProperty(required = true) val type: Type,
    @JsonProperty(required = true) val eventId: String,
    @JsonProperty(value = "v", required = true) val apiVersion: String,
    @JsonProperty("object") val obj: JsonNode?,
    @JsonProperty(required = true) val groupId: Long
) {
    enum class Type {
        CONFIRMATION,
        MESSAGE_NEW
    }
}