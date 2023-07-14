package io.github.jervnorsk.projects.streaming.digest.services.integrations.twitch.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@RequestMapping(
    path = ["/vods"]
)
@RestController
class TwitchVoidController(
    @Autowired
    private val webClient: WebClient
) {
    @GetMapping("{vodId}")
    fun consumeTwitchVoid(@PathVariable("vodId") voidId: String) =
        webClient.get()
            .uri("/videos?id={id}", voidId)
            .retrieve()
            .bodyToMono(String::class.java)
            .log()
}
