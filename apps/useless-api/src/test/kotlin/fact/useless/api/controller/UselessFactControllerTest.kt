package fact.useless.api.controller

import fact.useless.api.model.CachedUselessFact
import fact.useless.api.model.UselessStatistics
import fact.useless.api.service.UselessFactService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class UselessFactControllerTest {

    private val service = mock(UselessFactService::class.java)
    private val controller = UselessFactController(service)

    @Test
    fun `should fetch a random fact`() {
        val expectedFact = sampleFact()
        `when`(service.fetchRandomFact()).thenReturn(Mono.just(expectedFact))

        StepVerifier.create(controller.getRandomFact())
            .expectNext(expectedFact)
            .verifyComplete()
    }

    @Test
    fun `should fetch all facts`() {
        val facts = listOf(sampleFact(), sampleFact(id = "2"))
        `when`(service.getAllCachedFacts()).thenReturn(facts)

        StepVerifier.create(controller.getAllFacts())
            .expectNextSequence(facts)
            .verifyComplete()
    }

    @Test
    fun `should fetch fact by shortened url`() {
        val fact = sampleFact()
        `when`(service.getFactAndTrackAccess("abc123")).thenReturn(Mono.just(fact))

        StepVerifier.create(controller.getFactByShortenedUrl("abc123"))
            .expectNext(fact)
            .verifyComplete()
    }

    @Test
    fun `should return not found for invalid shortened url`() {
        `when`(service.getFactAndTrackAccess("invalid")).thenReturn(
            Mono.error(RuntimeException("Not found"))
        )

        StepVerifier.create(controller.getFactByShortenedUrl("invalid"))
            .expectErrorSatisfies {
                assert(it is ResponseStatusException && it.statusCode == HttpStatus.NOT_FOUND)
            }
            .verify()
    }

    @Test
    fun `should fetch statistics`() {
        val stats = UselessStatistics(1, emptyMap(), emptyList(), 0.0, 0.0, 0, 0)
        `when`(service.getUselessStatistics()).thenReturn(stats)

        StepVerifier.create(controller.getStatistics())
            .expectNext(stats)
            .verifyComplete()
    }

    private fun sampleFact(id: String = "1") = CachedUselessFact(
        id = id,
        text = "Fact $id",
        source = "source",
        sourceUrl = "http://source",
        language = "en",
        permalink = "http://permalink",
        shortenedUrl = "abc$id",
        createdAt = java.time.LocalDateTime.now()
    )
}
