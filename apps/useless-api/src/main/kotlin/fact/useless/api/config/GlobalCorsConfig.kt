package fact.useless.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class GlobalCorsConfig {

  @Bean
  fun corsWebFilter(): CorsWebFilter {
    val corsConfiguration = CorsConfiguration().apply {
      addAllowedOrigin("http://localhost:4200") // Allow localhost:4200
      addAllowedHeader("*") // Allow any header
      addAllowedMethod("*") // Allow any HTTP method (GET, POST, etc.)
      allowCredentials = true
      exposedHeaders = listOf("Set-Cookie")
    }

    val source: CorsConfigurationSource = UrlBasedCorsConfigurationSource().apply {
      registerCorsConfiguration("/**", corsConfiguration) // Apply CORS configuration to all endpoints
    }

    return CorsWebFilter(source)
  }
}

