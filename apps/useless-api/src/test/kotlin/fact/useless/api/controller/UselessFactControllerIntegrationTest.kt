package fact.useless.api.controller

import fact.useless.api.model.CachedUselessFact
import fact.useless.api.model.PaginatedResponse
import fact.useless.api.model.UselessStatistics
import fact.useless.api.service.UselessFactService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser


@ExtendWith(MockitoExtension::class)
@WebFluxTest(UselessFactController::class)
@Import(SecurityTestConfig::class)
class UselessFactControllerIntegrationTest {

  @MockitoBean
  private lateinit var uselessFactService: UselessFactService

  @Autowired
  private lateinit var webTestClient: WebTestClient

  @Test
  fun `should fetch random fact via POST endpoint`() {
    // Arrange
    val fact = createSampleFact()
    whenever(uselessFactService.fetchRandomFact()).thenReturn(Mono.just(fact))

    // Act & Assert
    webTestClient.post()
      .uri("/facts")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBody(CachedUselessFact::class.java)
      .isEqualTo(fact)
  }

  @Test
  fun `should fetch facts page`() {
    // Arrange
    val fact1 = createSampleFact()
    val fact2 = createSampleFact(id = "2")
    val facts = listOf(fact1, fact2)
    val paginatedResponse = PaginatedResponse(
      items = facts,
      totalCount = 2L,
      totalPages = 1
    )
    whenever(uselessFactService.getCachedFactsPage(1, 2)).thenReturn(paginatedResponse)

    // Act & Assert
    webTestClient.get()
      .uri("/facts?page=1&size=2")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBody(PaginatedResponse::class.java) // Expect the PaginatedResponse class
      .value { response ->
        // Assert the paginated response contains the expected facts
        assert(response.items.size == 2) // Expect 2 facts in the paginated response
        assert(response.items[0].shortenedUrl == fact1.shortenedUrl) // Verify the first fact
        assert(response.items[1].shortenedUrl == fact2.shortenedUrl) // Verify the second fact
      }
  }

  @Test
  fun `should use default pagination values when not specified`() {
    // Arrange
    val fact1 = createSampleFact()
    val facts = listOf(fact1)
    val paginatedResponse = PaginatedResponse(
      items = facts,
      totalCount = 1L,
      totalPages = 1
    )
    whenever(uselessFactService.getCachedFactsPage(1, 1)).thenReturn(paginatedResponse)

    // Act & Assert
    webTestClient.get()
      .uri("/facts")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBody(PaginatedResponse::class.java)
      .value { response ->
        assert(response.items.size == 1) // Expect 2 facts in the paginated response
        assert(response.items[0].shortenedUrl == fact1.shortenedUrl) // Verify the first fact
      }
  }

  @Test
  fun `should fetch fact by shortened url`() {
    // Arrange
    val fact = createSampleFact()
    whenever(uselessFactService.getFactAndTrackAccess("abc123")).thenReturn(Mono.just(fact))

    // Act & Assert
    webTestClient.get()
      .uri("/facts/abc123")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBody(CachedUselessFact::class.java)
      .isEqualTo(fact)
  }

  @Test
  fun `should return 404 for invalid shortened url`() {
    // Arrange
    whenever(uselessFactService.getFactAndTrackAccess("invalid"))
      .thenReturn(Mono.error(RuntimeException("Not found")))

    // Act & Assert
    webTestClient.get()
      .uri("/facts/invalid")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `should fetch statistics`() {
    // Arrange
    val stats = UselessStatistics(
      totalFacts = 10,
      factsAccessCount = mapOf("fact1" to 20, "fact2" to 10),
      topAccessedFacts = emptyList(),
      hitRate = 80.0,
      missRate = 20.0,
      requestCount = 100,
      evictionCount = 5
    )
    whenever(uselessFactService.getUselessStatistics()).thenReturn(stats)

    // Act & Assert
    webTestClient.mutateWith(mockUser("admin").roles("ADMIN"))
      .get()
      .uri("/admin/statistics")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBody(UselessStatistics::class.java)
      .isEqualTo(stats)
  }

  private fun createSampleFact(id: String = "1") = CachedUselessFact(
    id = id,
    text = "Fact $id",
    source = "source",
    sourceUrl = "http://source.com",
    language = "en",
    permalink = "http://permalink.com/$id",
    shortenedUrl = "abc$id",
    createdAt = LocalDateTime.now()
  )
}
