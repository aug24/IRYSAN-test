package cache

import com.github.benmanes.caffeine.cache.{Cache, Caffeine}
import model.NeoData

import javax.inject.Singleton
import scala.concurrent.duration._

@Singleton
class CaffeineCache {

  // Create a Caffeine cache instance with a maximum size and expiration policy
  private val cache: Cache[String, NeoData] = Caffeine.newBuilder()
    .maximumSize(1000) // Maximum number of items in the cache
    .expireAfterWrite(10, MINUTES) // Expire entries 10 minutes after write
    .build()

  def get(key: String): Option[NeoData] = Option(cache.getIfPresent(key))

  def set(key: String, value: NeoData): Unit = cache.put(key, value)

  def remove(key: String): Unit = cache.invalidate(key)
}
