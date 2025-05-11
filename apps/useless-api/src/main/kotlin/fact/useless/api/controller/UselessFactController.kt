package fact.useless.api.controller

import fact.useless.api.config.jwt.JwtUtil
import fact.useless.api.model.CachedUselessFact
import fact.useless.api.model.PaginatedResponse
import fact.useless.api.model.UselessStatistics
import fact.useless.api.service.UselessFactService
import io.jsonwebtoken.security.Request
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping
class UselessFactController(
  private val uselessFactService: UselessFactService,
  private val authenticationManager: ReactiveAuthenticationManager,
  private val jwtUtil: JwtUtil
) {

  companion object {
    private const val BEARER_PREFIX = "Bearer "
    private const val JWT_COOKIE_NAME = "JWT_TOKEN"
  }

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
  fun getStatistics(request: ServerHttpRequest): Mono<UselessStatistics> {
    return ReactiveSecurityContextHolder.getContext()
      .map { securityContext ->
        val authentication = securityContext.authentication
        val username = authentication.name
        val roles = authentication.authorities.map { it.authority.removePrefix("ROLE_") }
        uselessFactService.getUselessStatistics()
      }.switchIfEmpty(Mono.error(BadCredentialsException("Not authenticated")))
  }

  private fun extractJwtToken(request: ServerHttpRequest): String? {
    // First check in cookies
    val cookies = request.cookies[JWT_COOKIE_NAME]
    if (!cookies.isNullOrEmpty()) {
      val jwtCookie = cookies[0]
      if (jwtCookie.value.isNotBlank()) {
        return jwtCookie.value
      }
    }

    // Then check in Authorization header for Bearer token
    val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
    if (!authHeader.isNullOrBlank() && authHeader.startsWith(BEARER_PREFIX)) {
      return authHeader.substring(BEARER_PREFIX.length)
    }

    return null
  }
}
