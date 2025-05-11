package fact.useless.api.controller

import fact.useless.api.model.CachedUselessFact
import fact.useless.api.model.PaginatedResponse
import fact.useless.api.model.UselessStatistics
import fact.useless.api.service.UselessFactService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping
class UselessFactController(private val uselessFactService: UselessFactService) {

  @PostMapping("/facts")
  fun getRandomFact(): Mono<CachedUselessFact> {
    return uselessFactService.fetchRandomFact()
  }

  @GetMapping("/facts")
  fun getAllFacts(
    @RequestParam(defaultValue = "1") page: Int,
    @RequestParam(defaultValue = "1") size: Int
  ): Mono<PaginatedResponse> {
    return Mono.fromCallable { uselessFactService.getCachedFactsPage(page, size) }
  }

  @GetMapping("/facts/{shortenedUrl}")
  fun getFactByShortenedUrl(@PathVariable shortenedUrl: String): Mono<CachedUselessFact> {
    return uselessFactService.getFactAndTrackAccess(shortenedUrl)
      .onErrorResume { error ->
        when (error) {
          is ResponseStatusException -> Mono.error(error)
          else -> Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Fact not found"))
        }
      }
  }

  @GetMapping("/admin/statistics")
  fun getStatistics(): Mono<UselessStatistics> {
    return Mono.just(uselessFactService.getUselessStatistics())
  }
}
