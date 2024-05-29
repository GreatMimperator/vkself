package ru.miron.vkself.entity

import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import java.io.Serializable

@Entity
data class ProcessedMessage(
    @EmbeddedId
    var pk: PK? = null
) {
    @Embeddable
    data class PK (
        var userId: Int? = null,
        var messageId: Int? = null
    ): Serializable
}