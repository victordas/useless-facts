package fact.useless.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class GlobalCorsConfig {

  // Create a CorsConfigurationSource bean that can be used by both the CorsWebFilter and SecurityWebFilterChain
  @Bean
  fun corsConfigurationSource(): CorsConfigurationSource {
    val corsConfiguration = CorsConfiguration().apply {
      addAllowedOrigin("http://localhost:4200") // Allow localhost:4200
      addAllowedHeader("*") // Allow any header
      addAllowedMethod("*") // Allow any HTTP method (GET, POST, etc.)
      allowCredentials = true
      exposedHeaders = listOf("Set-Cookie")
    }

    val source = UrlBasedCorsConfigurationSource().apply {
      registerCorsConfiguration("/**", corsConfiguration) // Apply CORS configuration to all endpoints
    }

    return source
  }

  // Create the CorsWebFilter using the same configuration source
  @Bean
  fun corsWebFilter(corsConfigurationSource: CorsConfigurationSource): CorsWebFilter {
    return CorsWebFilter(corsConfigurationSource)
  }
}

