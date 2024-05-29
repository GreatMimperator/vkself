package ru.miron.vkself.api.controller

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.miron.vkself.api.dto.RequestDto
import java.util.UUID

@RestController
@Profile("confirmation")
class ConfirmationController(
    @Value("\${vk.group.id}") val groupId: Long,
    @Value("\${vk.confirmation-code}") val confirmationCode: String,
    @Value("\${vk.api-version}") val apiVersion: String
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PostMapping
    fun confirm(@RequestBody requestDto: RequestDto): ResponseEntity<String> {
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
        if (requestDto.type != RequestDto.Type.CONFIRMATION) {
            log.debug("(%s) Query has illegal type (expected %s): %s".format(
                requestUUID,
                RequestDto.Type.CONFIRMATION,
                requestDto.type
            ))
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        if (requestDto.groupId != groupId) {
            log.debug("(%s) Query has different groupId (expected %d): %d".format(
                requestUUID,
                groupId,
                requestDto.groupId
            ))
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        log.debug("(%s) Got query is ok, sending confirmation code...".format(requestUUID))
        return ResponseEntity<String>(confirmationCode, HttpStatus.OK)
    }
}