package fact.useless.api.config.jwt

import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtSecurityContextRepository(private val jwtUtil: JwtUtil) : ServerSecurityContextRepository {

  override fun save(exchange: ServerWebExchange, context: org.springframework.security.core.context.SecurityContext): Mono<Void> {
    // We don't need to save the context as the token itself represents it
    return Mono.empty()
  }

  override fun load(exchange: ServerWebExchange): Mono<org.springframework.security.core.context.SecurityContext> {
    val request = exchange.request
    val token = extractToken(request)

    if (token != null && jwtUtil.validateToken(token)) {
      val username = jwtUtil.getUsernameFromToken(token)
      val roles = jwtUtil.getRolesFromToken(token)

      val auth = UsernamePasswordAuthenticationToken(username, null, roles)
      val context = org.springframework.security.core.context.SecurityContextImpl(auth)
      return Mono.just(context)
    }

    return Mono.empty()
  }

  private fun extractToken(request: ServerHttpRequest): String? {
    // First check in cookies
    val cookies = request.cookies[JwtAuthenticationFilter.JWT_COOKIE_NAME]
    if (!cookies.isNullOrEmpty()) {
      val jwtCookie = cookies[0]
      if (jwtCookie.value.isNotBlank()) {
        return jwtCookie.value
      }
    }

    // Then check in Authorization header
    val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
    if (!authHeader.isNullOrBlank() && authHeader.startsWith(JwtAuthenticationFilter.BEARER_PREFIX)) {
      return authHeader.substring(JwtAuthenticationFilter.BEARER_PREFIX.length)
    }

    return null
  }
}
