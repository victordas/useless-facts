package fact.useless.api.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfig {
  @Value("\${cache.expiry.duration:86400}") // Default to 1 day
  private val cacheDuration: Long = 86400

  @Value("\${cache.expiry.unit:SECONDS}")
  private val cacheTimeUnitStr: String = "SECONDS"

  @Value("\${cache.max.size:1000}")
  private val cacheMaxSize: Long = 1000

  @Bean
  fun cacheManager(): CacheManager {
    val cacheManager = CaffeineCacheManager("cached-useless-facts")

    // Parse the time unit from configuration
    val timeUnit = TimeUnit.valueOf(cacheTimeUnitStr)

    val caffeine = Caffeine.newBuilder()
      .maximumSize(cacheMaxSize)
      .expireAfterWrite(cacheDuration, timeUnit)
      .recordStats()

    cacheManager.setCaffeine(caffeine)
    return cacheManager
  }
}
