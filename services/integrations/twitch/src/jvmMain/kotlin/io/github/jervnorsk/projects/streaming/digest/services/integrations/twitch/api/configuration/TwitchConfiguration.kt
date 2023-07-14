package io.github.jervnorsk.projects.streaming.digest.services.integrations.twitch.api.configuration

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.nimbusds.oauth2.sdk.ParseException
import com.nimbusds.oauth2.sdk.TokenResponse
import net.minidev.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ParameterizedTypeReference
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2AuthorizationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@Configuration
class TwitchConfiguration {

    val log = LoggerFactory.getLogger(TwitchConfiguration::class.java);

    @Bean
    fun twitchSecurityConfiguration(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .authorizeExchange {
                it.pathMatchers("/login", "/login/**", "/error").permitAll()
                it.anyExchange().authenticated()
            }
            .oauth2Client(Customizer.withDefaults())
            .oauth2Login(Customizer.withDefaults())
            .build()

    @Bean
    fun twitchOAuth2AccessTokenAdapter(): WebClientReactiveAuthorizationCodeTokenResponseClient =
        WebClientReactiveAuthorizationCodeTokenResponseClient()
            .also { client ->
                client.setBodyExtractor { inputMessage, context ->
                    BodyExtractors.toMono(object : ParameterizedTypeReference<Map<String?, Any?>?>() {})
                        .extract(inputMessage, context)
                        .onErrorMap {
                            OAuth2AuthorizationException(
                                OAuth2Error(
                                    "invalid_token_response",
                                    "An error occurred parsing the Access Token response: " + it.message,
                                    null
                                )
                            )
                        }
                        .switchIfEmpty(
                            Mono.error(
                                OAuth2AuthorizationException(
                                    OAuth2Error(
                                        "invalid_token_response",
                                        "Empty OAuth 2.0 Access Token Response",
                                        null
                                    )
                                )
                            )
                        )
                        .handle { it, sink ->
                            val map = JSONObject(it)
                            map["scope"] = map.getAsString("scope").removeSurrounding("[", "]")
                            try {
                                sink.next(TokenResponse.parse(map));
                            } catch (ex: ParseException) {
                                sink.error(
                                    OAuth2AuthorizationException(
                                        OAuth2Error(
                                            "invalid_token_response",
                                            "An error occurred parsing the Access Token response: " + ex.message,
                                            null
                                        )
                                    )
                                )
                            }
                        }
                        .handle { it, sink ->
                            if (it.indicatesSuccess()) {
                                sink.next(
                                    it as AccessTokenResponse
                                )
                            } else {
                                sink.error(
                                    OAuth2AuthorizationException(
                                        it.toErrorResponse().errorObject.let {
                                            OAuth2Error(
                                                it.code ?: OAuth2ErrorCodes.SERVER_ERROR,
                                                it.description,
                                                it.uri?.toString()
                                            )
                                        }
                                    )
                                )
                            }
                        }
                        .map { response ->
                            response.tokens.accessToken.let { accessToken ->
                                OAuth2AccessTokenResponse
                                    .withToken(
                                        accessToken.value
                                    )
                                    .tokenType(
                                        OAuth2AccessToken.TokenType.BEARER.let {
                                            if (it.value.equals(accessToken.type.value)) {
                                                it
                                            } else {
                                                null
                                            }
                                        }
                                    )
                                    .expiresIn(
                                        accessToken.lifetime
                                    )
                                    .scopes(
                                        accessToken.scope.toStringList().toSet()
                                    )
                                    .refreshToken(
                                        response.tokens.refreshToken?.value
                                    )
                                    .additionalParameters(
                                        response.customParameters
                                    )
                                    .build()
                            }
                        }
                }
            }

    @Bean
    fun twitchWebClientConfiguration(
        clientRegistrationRepository: ReactiveClientRegistrationRepository,
        authorizedClientRepository: ServerOAuth2AuthorizedClientRepository,
    ): WebClient =
        WebClient.builder()
            .filter(
                ServerOAuth2AuthorizedClientExchangeFilterFunction(
                    clientRegistrationRepository,
                    authorizedClientRepository
                ).also {
                    it.setDefaultOAuth2AuthorizedClient(true)
                }
            )
            .filter(ExchangeFilterFunction.ofRequestProcessor {request ->
                clientRegistrationRepository.findByRegistrationId("twitch")
                    .map {
                        ClientRequest.from(request)
                            .header("Client-Id", it.clientId)
                            .build()
                    }
            })
            .baseUrl("https://api.twitch.tv/helix")
            .build()
}
