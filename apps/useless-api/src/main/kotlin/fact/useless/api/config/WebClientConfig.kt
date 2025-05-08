package fact.useless.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import org.slf4j.LoggerFactory

@Configuration
class WebClientConfig {

  private val logger = LoggerFactory.getLogger(WebClientConfig::class.java)

  @Bean
  fun webClient(builder: WebClient.Builder): WebClient {
    // Configure HttpClient to follow redirects including 308 Permanent Redirect
    val httpClient = HttpClient.create()
      .followRedirect(true)  // Enable redirect following with default settings that include 308

    // Configure memory limits for responses
    val exchangeStrategies = ExchangeStrategies.builder()
      .codecs { configurer ->
        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) // 16MB buffer for responses
      }
      .build()

    // Create logging filter for debugging
    val loggingFilter = ExchangeFilterFunction.ofRequestProcessor { request ->
      logger.debug("Sending HTTP {} request to: {}", request.method(), request.url())
      request.headers().forEach { name, values ->
        values.forEach { value ->
          logger.debug("Request header: $name=$value")
        }
      }
      Mono.just(request)
    }

    // Build the WebClient with all configurations
    return builder
      .clientConnector(ReactorClientHttpConnector(httpClient))
      .exchangeStrategies(exchangeStrategies)
      .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
      .filter(loggingFilter)
      .build()
  }
}
