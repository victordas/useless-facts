package fact.useless.api.controller

import fact.useless.api.model.CachedUselessFact
import fact.useless.api.model.UselessStatistics
import fact.useless.api.service.UselessFactService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UselessFactControllerTest {

  @Mock
  private lateinit var uselessFactService: UselessFactService

  @InjectMocks
  private lateinit var controller: UselessFactController

  @Test
  fun `should fetch a random fact`() {
    // Arrange
    val expectedFact = createSampleFact()
    whenever(uselessFactService.fetchRandomFact()).thenReturn(Mono.just(expectedFact))

    // Act & Assert
    StepVerifier.create(controller.getRandomFact())
      .expectNext(expectedFact)
      .verifyComplete()
  }

  @Test
  fun `should fetch facts per page`() {
    // Arrange
    val facts = listOf(createSampleFact(), createSampleFact(id = "2"))
    whenever(uselessFactService.getCachedFactsPage(1, 2)).thenReturn(facts)

    // Act & Assert
    StepVerifier.create(controller.getAllFacts(page = 1, size = 2))
      .expectNext(facts)
      .verifyComplete()
  }

  @Test
  fun `should fetch fact by shortened url`() {
    // Arrange
    val fact = createSampleFact()
    whenever(uselessFactService.getFactAndTrackAccess("abc123")).thenReturn(Mono.just(fact))

    // Act & Assert
    StepVerifier.create(controller.getFactByShortenedUrl("abc123"))
      .expectNext(fact)
      .verifyComplete()
  }

  @Test
  fun `should return not found for invalid shortened url`() {
    // Arrange
    whenever(uselessFactService.getFactAndTrackAccess("invalid"))
      .thenReturn(Mono.error(RuntimeException("Not found")))

    // Act & Assert
    StepVerifier.create(controller.getFactByShortenedUrl("invalid"))
      .expectErrorMatches {
        it is ResponseStatusException && it.statusCode == HttpStatus.NOT_FOUND
      }
      .verify()
  }

  @Test
  fun `should handle specific ResponseStatusException`() {
    // Arrange
    val specificException = ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request")
    whenever(uselessFactService.getFactAndTrackAccess("bad-request"))
      .thenReturn(Mono.error(specificException))

    // Act & Assert
    StepVerifier.create(controller.getFactByShortenedUrl("bad-request"))
      .expectErrorMatches {
        it is ResponseStatusException && it.statusCode == HttpStatus.BAD_REQUEST
      }
      .verify()
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
    StepVerifier.create(controller.getStatistics())
      .expectNext(stats)
      .verifyComplete()
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
