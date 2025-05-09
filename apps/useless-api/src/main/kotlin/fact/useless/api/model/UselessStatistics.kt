package fact.useless.api.model

data class UselessStatistics (
  val totalFacts: Int,
  val factsAccessCount: Map<String, Int>,
  val topAccessedFacts: List<CachedUselessFact>,
  val hitRate: Double,
  val missRate: Double,
  val requestCount: Long,
  val evictionCount: Long
)
