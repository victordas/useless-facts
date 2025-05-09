package fact.useless.api.service

import fact.useless.api.model.CachedUselessFact
import fact.useless.api.model.UselessFactAPIResponse
import fact.useless.api.model.UselessStatistics
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
import kotlin.collections.containsKey
import kotlin.text.get

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

  /**
   * Returns all cached facts without incrementing access counts
   * @return List of all cached facts
   */
  fun getAllCachedFacts(): List<CachedUselessFact> {
    val cache = cacheManager.getCache("cached-useless-facts")
      ?: return emptyList()

    val facts = mutableListOf<CachedUselessFact>()
    val nativeCache = cache.nativeCache

    if (nativeCache is com.github.benmanes.caffeine.cache.Cache<*, *>) {
      @Suppress("UNCHECKED_CAST")
      val caffeineCache = nativeCache as com.github.benmanes.caffeine.cache.Cache<String, CachedUselessFact>
      facts.addAll(caffeineCache.asMap().values)
    }

    return facts
  }

  /**
   * Retrieves a cached fact by its shortened URL
   * @return Mono that emits the found fact or error if not found
   */
  fun getFactByShortenedUrl(shortenedUrl: String): Mono<CachedUselessFact> {
    val cache = cacheManager.getCache("cached-useless-facts")
      ?: return Mono.error(RuntimeException("Cache 'cached-useless-facts' not found"))

    val fact = cache.get(shortenedUrl, CachedUselessFact::class.java)
      ?: return Mono.error(RuntimeException("Fact not found with shortened URL: $shortenedUrl"))

    return Mono.just(fact)
  }

  fun getFactAndTrackAccess(shortenedUrl: String): Mono<CachedUselessFact> {
    return getFactByShortenedUrl(shortenedUrl)
      .flatMap { cachedFact ->
        // Increment access count
        val counter = accessCounters.computeIfAbsent(shortenedUrl) { AtomicInteger(0) }
        val newCount = counter.incrementAndGet()

        // Update the cached fact with new access count
        cachedFact.accessCount = newCount

        Mono.just(cachedFact)
      }
  }

  /**
   * Provides statistics about fact access
   * @return Statistics object with cache and access information
   */
  fun getUselessStatistics(): UselessStatistics {
    val facts = getAllCachedFacts()
    val totalFacts = facts.size

    // Map access counts from our counter
    val factsAccessCount = accessCounters.entries.associate {
      it.key to it.value.get()
    }

    // Get top accessed facts
    val topAccessedFacts = facts
      .filter { accessCounters.containsKey(it.shortenedUrl) }
      .sortedByDescending { accessCounters[it.shortenedUrl]?.get() ?: 0 }
      .take(5)

    // Get cache statistics if available
    val cache = cacheManager.getCache("cached-useless-facts")
    var hitRate = 0.0
    var missRate = 0.0
    var requestCount = 0L
    var evictionCount = 0L

    if (cache?.nativeCache is com.github.benmanes.caffeine.cache.Cache<*, *>) {
      val caffeineCache = cache.nativeCache as com.github.benmanes.caffeine.cache.Cache<*, *>
      val stats = caffeineCache.stats()
      hitRate = stats.hitRate()
      missRate = stats.missRate()
      requestCount = stats.requestCount()
      evictionCount = stats.evictionCount()
    }

    return UselessStatistics(
      totalFacts = totalFacts,
      factsAccessCount = factsAccessCount,
      topAccessedFacts = topAccessedFacts,
      hitRate = hitRate,
      missRate = missRate,
      requestCount = requestCount,
      evictionCount = evictionCount
    )
  }
}
