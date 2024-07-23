package org.breizhcamp.konter.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationProvider
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.session.HttpSessionEventPublisher

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun sessionRegistry(): SessionRegistry = SessionRegistryImpl()

    @Bean
    fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy =
        RegisterSessionAuthenticationStrategy(sessionRegistry())

    @Bean
    fun httpSessionEventPublisher(): HttpSessionEventPublisher =
        HttpSessionEventPublisher()

    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientRepository: OAuth2AuthorizedClientRepository
    ): OAuth2AuthorizedClientManager {
        val authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
            .authorizationCode()
            .clientCredentials()
            .refreshToken()
            .build()

        val authorizedClientManager = DefaultOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientRepository
        )

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)

        return authorizedClientManager
    }

    @Bean
    fun authenticationProvider(): OAuth2AuthorizationCodeAuthenticationProvider =
        OAuth2AuthorizationCodeAuthenticationProvider(
            DefaultAuthorizationCodeTokenResponseClient()
        )

    @Bean
    fun resourceServerFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain =
        httpSecurity
            .oauth2ResourceServer {
                it.jwt { config ->
                    config.jwtAuthenticationConverter { jwt ->
                        JwtAuthenticationToken(jwt, getAuthoritiesFromJwt(jwt))
                    }
                }
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.GET, "/api/**").hasRole("KONTER") }
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.POST, "/api/**/filter/**").hasRole("KONTER") }
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN") }
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.PATCH, "/api/**").hasRole("ADMIN") }
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN") }
            .oauth2Client(Customizer.withDefaults())
            .build()

    private fun getAuthoritiesFromJwt(jwt: Jwt): Collection<GrantedAuthority> =
        generateAuthoritiesForClaim(getAuthoritiesFromClaims(jwt.claims))

    private fun getAuthoritiesFromRealmAccess(realmAccess: Map<String, Any>): Collection<String> {
        val authorities = realmAccess["roles"]

        if (authorities is Collection<*>) {
            return authorities.map {
                if (it is String) {
                    it
                } else throw IllegalArgumentException("Role was not of type String")
            }
        }

        throw IllegalArgumentException("Authorities were not a Collection of String")
    }

    private fun getAuthoritiesFromClaims(attributes: MutableMap<String, Any>): Collection<String> {
        val realmAccess = attributes["realm_access"]

        if (realmAccess is MutableMap<*, *>) {
            if (realmAccess.all { (k, _) -> (k is String) }) {
                return getAuthoritiesFromRealmAccess(realmAccess
                    .mapKeys { (k, _) -> k as String }
                    .mapValues { (_, v) -> v as Any }
                )
            }
        }

        throw IllegalArgumentException("Realm access was not a Map of String to Collection of String")
    }

    private fun generateAuthoritiesForClaim(authorities: Collection<String>): Collection<GrantedAuthority> =
        authorities.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }
}

@Configuration
@Profile("test")
@EnableWebSecurity
class NoAuthSecurityConfig {
    @Bean
    fun resourceServerFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .csrf { it.disable() }
            .authorizeHttpRequests{ it.anyRequest().permitAll() }
            .build()
    }
}