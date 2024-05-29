package ru.miron.vkself

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VkselfApplication

fun main(args: Array<String>) {
	runApplication<VkselfApplication>(*args)
}
