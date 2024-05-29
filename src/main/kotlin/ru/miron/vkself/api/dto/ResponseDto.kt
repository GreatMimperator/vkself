package ru.miron.vkself.api.dto

import com.fasterxml.jackson.databind.JsonNode

data class ResponseDto(
    val error: JsonNode?,
    val response: JsonNode?
)