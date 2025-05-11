package fact.useless.api.config.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtil(@Value("\${JWT_SECRET}") private val secret: String) {

  private val secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
  private val tokenValidity: Long = 3600 * 24 * 1000 // 24 hours

  fun generateToken(username: String, roles: List<String>): String {

    val now = Date()
    val validity = Date(now.time + tokenValidity)

    return Jwts.builder()
      .subject(username)
      .claim("roles", roles)
      .issuedAt(now)
      .expiration(validity)
      .signWith(secretKey)
      .compact()
  }

  fun validateToken(token: String): Boolean {
    try {
      val claims = getClaimsFromToken(token)
      val expirationDate = claims.expiration
      return !expirationDate.before(Date())
    } catch (e: Exception) {
      return false
    }
  }

  fun getClaimsFromToken(token: String): Claims {
    return Jwts.parser()
      .verifyWith(secretKey)
      .build()
      .parseSignedClaims(token)
      .payload
  }

  fun getUsernameFromToken(token: String): String {
    val claims = getClaimsFromToken(token)
    return claims.subject
  }

  fun getRolesFromToken(token: String): List<SimpleGrantedAuthority> {
    val claims = getClaimsFromToken(token)
    val roles = claims["roles"] as List<*>
    return roles.map { SimpleGrantedAuthority("ROLE_$it") }
  }
}
