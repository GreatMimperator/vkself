package ru.miron.vkself.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.miron.vkself.entity.ProcessedMessage

interface ProcessedMessageRepository: JpaRepository<ProcessedMessage, ProcessedMessage.PK>