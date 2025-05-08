package fact.useless.api.service

import fact.useless.api.model.CachedUselessFact
import fact.useless.api.model.UselessFactAPIResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheConfig
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service
@CacheConfig(cacheNames = ["cached-useless-facts"])
class UselessFactService(
  private val webClient: WebClient,
  private val cacheManager: CacheManager,
  @Value("\${uselessfacts.api.url}") private val apiUrl: String
) {
  // Track access counts separately as they aren't inherently part of the cache mechanism
  private val accessCounters = ConcurrentHashMap<String, AtomicInteger>()

  /**
   * Fetches a random fact from the external API, caches it, and returns it
   */
  fun fetchRandomFact(): Mono<CachedUselessFact> {
    return webClient.get()
      .uri(apiUrl)
      .retrieve()
      .onStatus({ status -> status.is3xxRedirection }, { response ->
        // Log redirection and follow (this is handled by the WebClient config,
        // but we can add additional logging or handling here if needed)
        Mono.empty()
      })
      .bodyToMono(UselessFactAPIResponse::class.java)
      .switchIfEmpty(Mono.error(RuntimeException("Failed to fetch fact from external API")))
      .map { response ->
        // Generate shortened URL
        val shortenedUrl = generateShortenedUrl(response.id)

        // Create the fact
        val fact = CachedUselessFact(
          id = response.id,
          text = response.text,
          source = response.source,
          sourceUrl = response.sourceUrl,
          language = response.language,
          permalink = response.permalink,
          shortenedUrl = shortenedUrl,
          createdAt = LocalDateTime.now()
        )

        // Manually cache the fact since we can't use @CachePut with Mono
        val cache = cacheManager.getCache("cached-useless-facts")
        cache?.put(shortenedUrl, fact)

        fact
      }
  }

  /**
   * Generates a shortened URL from the fact ID
   */
  private fun generateShortenedUrl(id: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
      .digest(id.toByteArray())
      .take(6)
      .joinToString("") { "%02x".format(it) }

    return digest
  }
}
