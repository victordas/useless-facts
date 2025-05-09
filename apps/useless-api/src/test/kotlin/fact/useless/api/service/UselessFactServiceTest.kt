package fact.useless.api.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import fact.useless.api.model.CachedUselessFact
import fact.useless.api.model.UselessFactAPIResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class UselessFactServiceTest {

  private lateinit var cache: CaffeineCache
  private lateinit var cacheManager: CacheManager
  private lateinit var webClient: WebClient
  private lateinit var service: UselessFactService

  @BeforeEach
  fun setUp() {
    // Real Caffeine cache for testing
    val caffeineCache = Caffeine.newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .recordStats()
      .build<String, CachedUselessFact>()

    @Suppress("UNCHECKED_CAST")
    cache = CaffeineCache("cached-useless-facts", caffeineCache as Cache<in Any, in Any>)

    // Mock CacheManager with correct typing for String and CachedUselessFact
    cacheManager = mock()
    whenever(cacheManager.getCache("cached-useless-facts")).thenReturn(cache)

    webClient = mock()

    service = UselessFactService(webClient, cacheManager, "http://fake-api.com")
  }

  @Test
  fun `should fetch and cache a random fact`() {
    val response = UselessFactAPIResponse(
      id = "abc123",
      text = "A random fact",
      source = "source",
      sourceUrl = "http://source",
      language = "en",
      permalink = "http://permalink"
    )

    val responseSpec = mock<WebClient.ResponseSpec>()
    val requestHeadersUriSpec = mock<WebClient.RequestHeadersUriSpec<*>>()
    val requestHeadersSpec = mock<WebClient.RequestHeadersSpec<*>>()

    whenever(webClient.get()).thenReturn(requestHeadersUriSpec)
    whenever(requestHeadersUriSpec.uri(any<String>())).thenReturn(requestHeadersSpec)
    whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
    whenever(responseSpec.onStatus(any(), any())).thenReturn(responseSpec)
    whenever(responseSpec.bodyToMono(UselessFactAPIResponse::class.java)).thenReturn(Mono.just(response))

    StepVerifier.create(service.fetchRandomFact())
      .expectNextMatches {
        it.id == response.id && it.shortenedUrl.isNotEmpty()
      }
      .verifyComplete()
  }

  @Test
  fun `should return error when fact not found in cache`() {
    StepVerifier.create(service.getFactByShortenedUrl("invalid"))
      .expectErrorMatches { it is RuntimeException && it.message!!.contains("not found") }
      .verify()
  }

  @Test
  fun `should track access count correctly`() {
    val fact = sampleFact("abc")
    cache.put("abc", fact)

    StepVerifier.create(service.getFactAndTrackAccess("abc"))
      .expectNextMatches {
        it.shortenedUrl == "abc" && it.accessCount == 1
      }
      .verifyComplete()

    StepVerifier.create(service.getFactAndTrackAccess("abc"))
      .expectNextMatches {
        it.accessCount == 2
      }
      .verifyComplete()
  }

  private fun sampleFact(id: String) = CachedUselessFact(
    id = id,
    text = "Text",
    source = "src",
    sourceUrl = "http://src",
    language = "en",
    permalink = "http://perma",
    shortenedUrl = id,
    createdAt = LocalDateTime.now()
  )
}
