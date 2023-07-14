package io.github.jervnorsk.projects.streaming.digest.services.integrations.twitch

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(
    scanBasePackages = ["io.github.jervnorsk"]
)
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
