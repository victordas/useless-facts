package fact.useless.api.config

import fact.useless.api.config.jwt.JwtAuthenticationFilter
import fact.useless.api.config.jwt.JwtUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.web.cors.reactive.CorsConfigurationSource
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.util.Base64

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
  @Value("\${APP_USER}") private val username: String,
  @Value("\${APP_PASSWORD}") private val password: String,
  @Value("\${JWT_SECRET}") private val secret: String,
) {
  @Bean
  fun securityWebFilterChain(
    http: ServerHttpSecurity,
    jwtAuthenticationFilter: JwtAuthenticationFilter,
    basicAuthenticationFilter: AuthenticationWebFilter,
    corsConfigurationSource: CorsConfigurationSource): SecurityWebFilterChain {
    return http
      .cors { cors -> cors.configurationSource(corsConfigurationSource) }
      .authorizeExchange { exchanges ->
        exchanges
          .pathMatchers("/admin/**").hasRole("ADMIN")
          .anyExchange().permitAll()
      }
      .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
      .addFilterAt(basicAuthenticationFilter, SecurityWebFiltersOrder.HTTP_BASIC)
      .httpBasic { httpBasic ->
          httpBasic.disable()
      }
      // Disable CSRF protection for REST API
      .csrf { csrf -> csrf.disable() }
      // Disable form login for REST API
      .formLogin { formLogin -> formLogin.disable() }
      .build()
  }

  @Bean
  fun userDetailsService(): ReactiveUserDetailsService {
    val user = User
      .withUsername(username)
      .password(passwordEncoder().encode(password))
      .roles("ADMIN")
      .build()
    return MapReactiveUserDetailsService(user)
  }

  @Bean
  fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

  @Bean
  fun jwtUtil(): JwtUtil {
    return JwtUtil(secret)
  }

  @Bean
  fun reactiveAuthenticationManager(
    userDetailsService: ReactiveUserDetailsService,
    passwordEncoder: PasswordEncoder
  ): ReactiveAuthenticationManager {
    val manager = UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService)
    manager.setPasswordEncoder(passwordEncoder)
    return manager
  }

  @Bean
  fun basicAuthenticationFilter(
    reactiveAuthenticationManager: ReactiveAuthenticationManager,
    jwtUtil: JwtUtil
  ): AuthenticationWebFilter {
    val authenticationFilter = AuthenticationWebFilter(reactiveAuthenticationManager)

    // Set the converter that extracts the Basic Auth credentials
    authenticationFilter.setServerAuthenticationConverter { exchange ->
      val request = exchange.request
      val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)

      if (authHeader != null && authHeader.startsWith("Basic ")) {
        try {
          val base64Credentials = authHeader.substring("Basic ".length)
          val credentialsBytes = Base64.getDecoder().decode(base64Credentials)
          val credentials = String(credentialsBytes, StandardCharsets.UTF_8)

          val parts = credentials.split(":", limit = 2)
          if (parts.size == 2) {
            val username = parts[0]
            val password = parts[1]

            return@setServerAuthenticationConverter Mono.just(
              UsernamePasswordAuthenticationToken(username, password)
            )
          }
        } catch (e: Exception) {
          // Log the error but don't expose details to client
        }
      }

      Mono.empty()
    }

    // Set success handler that generates a JWT token and adds it as a cookie
    // but doesn't terminate the filter chain
    authenticationFilter.setAuthenticationSuccessHandler { exchange, authentication ->
      val roles = authentication.authorities.map { it.authority.removePrefix("ROLE_") }
      val token = jwtUtil.generateToken(authentication.name, roles)

      // Add JWT token to response cookie
      exchange.exchange.response.cookies.add("JWT_TOKEN", org.springframework.http.ResponseCookie.from("JWT_TOKEN", token)
        .maxAge(86400) // 24 hours in seconds
        .httpOnly(true)
        .path("/")
        .secure(false) // Disabled for dev only
        .build())

      // Store authentication in the security context
      val context = SecurityContextImpl(authentication)
      exchange.exchange.attributes["SPRING_SECURITY_CONTEXT"] = context

      exchange.chain.filter(exchange.exchange)
        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
    }

    authenticationFilter.setAuthenticationFailureHandler { exchange, ex ->
      Mono.error(BadCredentialsException(ex.message)) // Or custom error handling
    }

    // Set what URL this filter should apply to

      authenticationFilter.setRequiresAuthenticationMatcher(
        ServerWebExchangeMatcher { serverWebExchange ->
          if (serverWebExchange.request.uri.path == "/admin/statistics") {
            ServerWebExchangeMatcher.MatchResult.match()
          } else {
            ServerWebExchangeMatcher.MatchResult.notMatch()
          }
        }
      )

    return authenticationFilter
  }
}
