package io.github.jervnorsk.projects.streaming.digest.services.integrations.twitch.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.*

@RestController
class TwitchController(
    @Autowired
    private val httpClient: WebClient.Builder
) {
    @GetMapping("/")
    fun getUserAttributes(@AuthenticationPrincipal user: OAuth2AuthenticatedPrincipal) =
        user
}
