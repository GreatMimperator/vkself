package ru.miron.vkself.api.dto.obj

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageDto(
    @JsonProperty(required = true) val id: Int,
    @JsonProperty(required = true) val peerId: Int,
    @JsonProperty(required = true) val fromId: Int,
    @JsonProperty(required = true) val text: String
)