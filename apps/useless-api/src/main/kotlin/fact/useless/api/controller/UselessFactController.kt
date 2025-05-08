package fact.useless.api.controller

import fact.useless.api.model.CachedUselessFact
import fact.useless.api.service.UselessFactService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/facts")
class FactController(private val uselessFactService: UselessFactService) {

  @GetMapping("/random")
  fun getRandomFact(): Mono<CachedUselessFact> {
    return uselessFactService.fetchRandomFact()
  }

  @GetMapping
  fun getAllFacts(): Flux<CachedUselessFact> {
    return Flux.fromIterable(uselessFactService.getAllCachedFacts())
  }

}
