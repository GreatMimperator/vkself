package ru.miron.vkself.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.miron.vkself.api.dto.RequestDto
import ru.miron.vkself.api.dto.obj.MessageDto
import ru.miron.vkself.api.dto.obj.MessageNewDto
import ru.miron.vkself.service.MessageService
import java.util.*


@RestController
@Profile("!confirmation")
class MainController(
    @Value("\${vk.group.id}") val groupId: Long,
    @Value("\${vk.api-version}") val apiVersion: String,
    val messageService: MessageService,
    val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PostMapping
    fun serve(@RequestBody requestDto: RequestDto): ResponseEntity<out Any> {
        val requestUUID = UUID.randomUUID()
        log.debug("(%s) Got query".format(requestUUID))
        if (requestDto.apiVersion != apiVersion) {
            log.debug("(%s) Query api version is wrong: %s instead of %s".format(
                requestUUID,
                requestDto.apiVersion,
                apiVersion
            ))
            return ResponseEntity(HttpStatus.METHOD_NOT_ALLOWED)
        }
        if (requestDto.groupId != groupId) {
            log.debug("(%s) Query has different groupId (expected %d): %d".format(
                requestUUID,
                groupId,
                requestDto.groupId
            ))
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        log.debug("(%s) Got query type: %s".format(
            requestUUID,
            requestDto.type
        ))
        when (requestDto.type) {
            RequestDto.Type.CONFIRMATION -> {
                log.debug("(%s) Confirmation should be accessed with confirmation profile ".format(requestUUID))
                return ResponseEntity(HttpStatus.METHOD_NOT_ALLOWED)
            }
            RequestDto.Type.MESSAGE_NEW -> {
                log.debug("(%s) %s query type processing...".format(requestUUID, RequestDto.Type.MESSAGE_NEW))
                val messageDto: MessageDto
                try {
                    val messageNewDto = objectMapper.treeToValue(requestDto.obj, MessageNewDto::class.java)
                    messageDto = messageNewDto.message
                } catch (_: Exception) {
                    log.debug("(%s) got message has illegal format: %s".format(requestUUID, requestDto.obj))
                    return ResponseEntity(HttpStatus.BAD_REQUEST)
                }
                messageService.processMessage(messageDto, requestUUID)
                return ResponseEntity("ok", HttpStatus.OK)
            }
        }
    }
}