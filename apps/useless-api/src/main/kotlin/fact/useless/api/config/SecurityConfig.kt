package fact.useless.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
  @Value("\${APP_USER}") private val username: String,
  @Value("\${APP_PASSWORD}") private val password: String
) {
  @Bean
  fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    return http
      .authorizeExchange { exchanges ->
        exchanges
          .pathMatchers("/admin/**").hasRole("ADMIN")
          .anyExchange().permitAll()
      }
      .httpBasic { httpBasic ->
        // Basic HTTP authentication configuration if needed
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
}
