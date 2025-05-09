package fact.useless.api.controller

import fact.useless.api.model.CachedUselessFact
import fact.useless.api.model.UselessStatistics
import fact.useless.api.service.UselessFactService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser


@SpringBootTest
@AutoConfigureWebTestClient
@Import(UselessFactControllerTestConfig::class)
class UselessFactControllerIntegrationTest {

  @Autowired
  private lateinit var webTestClient: WebTestClient

  @Autowired
  private lateinit var service: UselessFactService

  @Test
  fun `POST facts should return random fact`() {
    val fact = sampleFact("1")
    `when`(service.fetchRandomFact()).thenReturn(Mono.just(fact))

    webTestClient.post()
      .uri("/facts")
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$.id").isEqualTo("1")
      .jsonPath("$.text").isEqualTo("Fact 1")
  }

  @Test
  fun `GET facts should return list of facts`() {
    val facts = listOf(sampleFact("1"), sampleFact("2"))
    `when`(service.getAllCachedFacts()).thenReturn(facts)

    webTestClient.get()
      .uri("/facts")
      .exchange()
      .expectStatus().isOk
      .expectBodyList(CachedUselessFact::class.java)
      .hasSize(2)
  }

  @Test
  fun `GET facts by shortenedUrl should return fact`() {
    val fact = sampleFact("1")
    `when`(service.getFactAndTrackAccess("abc1")).thenReturn(Mono.just(fact))

    webTestClient.get()
      .uri("/facts/abc1")
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$.shortenedUrl").isEqualTo("abc1")
  }

  @Test
  fun `GET facts by invalid shortenedUrl should return 404`() {
    `when`(service.getFactAndTrackAccess("bad")).thenReturn(Mono.error(RuntimeException("Fact not found")))

    webTestClient.get()
      .uri("/facts/bad")
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `GET admin statistics should return statistics`() {
    val stats = UselessStatistics(
      totalFacts = 2,
      factsAccessCount = mapOf("abc1" to 3),
      topAccessedFacts = listOf(sampleFact("1")),
      hitRate = 0.9,
      missRate = 0.1,
      requestCount = 10,
      evictionCount = 1
    )

    `when`(service.getUselessStatistics()).thenReturn(stats)

    webTestClient
      .mutateWith(mockUser("admin").roles("ADMIN"))
      .get()
      .uri("/admin/statistics")
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$.totalFacts").isEqualTo(2)
      .jsonPath("$.hitRate").isEqualTo(0.9)
  }

  private fun sampleFact(id: String) = CachedUselessFact(
    id = id,
    text = "Fact $id",
    source = "source",
    sourceUrl = "http://source",
    language = "en",
    permalink = "http://permalink",
    shortenedUrl = "abc$id",
    createdAt = LocalDateTime.now()
  )
}

@TestConfiguration
class UselessFactControllerTestConfig {

  @Bean
  fun uselessFactService(): UselessFactService = mock()
}
