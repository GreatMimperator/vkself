package ru.miron.vkself.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import ru.miron.vkself.api.dto.ResponseDto
import ru.miron.vkself.api.dto.obj.MessageDto
import ru.miron.vkself.entity.ProcessedMessage
import ru.miron.vkself.repository.ProcessedMessageRepository
import java.util.UUID

@Service
@Profile("!confirmation")
class MessageService(
    @Value("\${send-back.format}") val sendBackFormat: String,
    @Value("\${vk.api-version}") val apiVersion: String,
    @Value("\${vk.url.protocol}") val protocol: String,
    @Value("\${vk.url.host}") val host: String,
    @Value("\${vk.url.methods-path}") val methodsPath: String,
    val processedMessageRepository: ProcessedMessageRepository,
    val restClient: RestClient
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun processMessage(messageDto: MessageDto, requestUUID: UUID): Boolean {
        log.debug("(%s) Message text to process: \"%s\"".format(requestUUID, messageDto.text))
        val processedMessagePK = ProcessedMessage.PK(messageDto.fromId, messageDto.id)
        log.debug("(%s) Message's to process id: \"%s\"".format(requestUUID, processedMessagePK))
        try {
            processedMessageRepository.save(ProcessedMessage(processedMessagePK))
        } catch (e: Exception) {
            log.debug("(%s) Message has already processed, do nothing".format(requestUUID))
            return false
        }
        val answerText = sendBackFormat.format(messageDto.text)
        log.debug("(%s) Processed message text: \"%s\"".format(requestUUID, answerText))
        val queryParams = mapOf(
            "user_id" to messageDto.fromId,
            "random_id" to 0,
            "message" to answerText,
            "v" to apiVersion
        )
        val queryParamsInBody = queryParams.map { "${it.key}=${it.value}" }.joinToString("&")
        log.debug("(%s) Query params in body: %s".format(requestUUID, queryParamsInBody))
        try {
            log.debug("(%s) Trying to send query...".format(requestUUID))
            val response = restClient.post()
                .uri { uriBuilder -> uriBuilder
                    .scheme(protocol)
                    .host(host)
                    .path(methodsPath + "/" + UriMethodName.MESSAGE_SEND.value)
                    .build() }
                .body(queryParamsInBody)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
            val responseDto = response.body(ResponseDto::class.java)!!
            val hasError = responseDto.error != null
            if (hasError) {
                log.debug("(%s) Error got: %s".format(requestUUID, responseDto.error))
                processedMessageRepository.deleteById(processedMessagePK)
                return false
            }
            log.debug("(%s) Successfully sent message".format(requestUUID))
            return true
        } catch (e: Exception) {
            log.debug("(%s) An unexpected exception got: ".format(requestUUID, e.message))
            processedMessageRepository.deleteById(processedMessagePK)
            return false
        }
    }
}