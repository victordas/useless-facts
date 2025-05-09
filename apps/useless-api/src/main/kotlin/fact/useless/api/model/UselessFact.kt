package fact.useless.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

// Response model from Useless Facts API
data class UselessFactAPIResponse(
  val id: String,
  val text: String,
  val source: String,
  @JsonProperty("source_url") val sourceUrl: String,
  val language: String,
  val permalink: String
)

/*
 *  In-memory model for storing useless facts with additional timestamp and shortened url
 *  Setting initial accessCount to zero
 */
data class CachedUselessFact(
  val id: String,
  val text: String,
  val source: String,
  val sourceUrl: String,
  val language: String,
  val permalink: String,
  val shortenedUrl: String,
  val createdAt: LocalDateTime = LocalDateTime.now(),
  var accessCount: Int = 0
)
