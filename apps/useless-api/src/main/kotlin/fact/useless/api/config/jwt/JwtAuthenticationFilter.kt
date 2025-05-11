package fact.useless.api.config.jwt

import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(private val jwtUtil: JwtUtil) : WebFilter {

  companion object {
    const val JWT_COOKIE_NAME = "JWT_TOKEN"
    const val BEARER_PREFIX = "Bearer "
  }

  override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
    val request = exchange.request
    val cookies = request.cookies["JWT_TOKEN"] // Match your cookie name

    // 1. Check JWT in cookie first
    val jwtToken = cookies?.firstOrNull()?.value

    if (jwtToken != null && jwtUtil.validateToken(jwtToken)) {
      // Valid JWT → Authenticate & proceed
      val username = jwtUtil.getUsernameFromToken(jwtToken)
      val roles = jwtUtil.getRolesFromToken(jwtToken)
        .map { role ->
          val roleName = role.authority
          when {
            roleName.startsWith("ROLE_") -> role
            else -> SimpleGrantedAuthority("ROLE_$roleName")
          }
        }

      val authentication = UsernamePasswordAuthenticationToken(
        username,
        null,
        roles
      )

      return chain.filter(exchange)
        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
    }

    // 2. Fallback to Authorization: Bearer (optional)
    val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      val token = authHeader.substring(7)
      if (jwtUtil.validateToken(token)) {
        val username = jwtUtil.getUsernameFromToken(token)
        val roles = jwtUtil.getRolesFromToken(token)
          .map { SimpleGrantedAuthority("ROLE_$it") }

        val authentication = UsernamePasswordAuthenticationToken(
          username,
          null,
          roles
        )

        return chain.filter(exchange)
          .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
      }
    }

    // 3. No JWT → Continue to Basic Auth filter
    return chain.filter(exchange)
  }

  private fun extractToken(request: ServerHttpRequest): String? {
    // First check in cookies
    val cookies = request.cookies[JWT_COOKIE_NAME]
    if (!cookies.isNullOrEmpty()) {
      val jwtCookie = cookies[0]
      if (jwtCookie.value.isNotBlank()) {
        return jwtCookie.value
      }
    }

    // Then check in Authorization header
    val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
    if (!authHeader.isNullOrBlank() && authHeader.startsWith(BEARER_PREFIX)) {
      return authHeader.substring(BEARER_PREFIX.length)
    }

    return null
  }

}
