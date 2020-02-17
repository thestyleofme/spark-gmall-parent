package org.abigballofmud.gmall.realtime.utils

import java.util.Objects

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.{Jedis, JedisPool, Protocol}


/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2019/10/17 15:47
 * @since 1.0
 */
object RedisUtil extends Serializable {

  @transient private var pool: JedisPool = _

  /**
   * 创建JedisPool
   *
   * @param redisHost     redisHost
   * @param redisPort     redisPort
   * @param redisPassword redisPassword
   * @param redisTimeout  redisTimeOut
   * @param maxTotal      maxTotal
   * @param maxIdle       maxIdle
   * @param minIdle       minIdle
   * @param onBorrow      onBorrow
   * @param onReturn      onReturn
   * @param maxWaitMillis maxWaitMillis
   */
  def makePool(redisHost: String = "localhost",
               redisPort: Int = 6379,
               redisPassword: String = null,
               redisDatabase: Int = Protocol.DEFAULT_DATABASE,
               redisTimeout: Int = 3000,
               maxTotal: Int = 16,
               maxIdle: Int = 8,
               minIdle: Int = 2,
               onBorrow: Boolean = true,
               onReturn: Boolean = false,
               maxWaitMillis: Long = 10000): Unit = {
    if (pool == null) {
      val poolConfig = new GenericObjectPoolConfig()
      poolConfig.setMaxTotal(maxTotal)
      poolConfig.setMaxIdle(maxIdle)
      poolConfig.setMinIdle(minIdle)
      poolConfig.setTestOnBorrow(onBorrow)
      poolConfig.setTestOnReturn(onReturn)
      poolConfig.setMaxWaitMillis(maxWaitMillis)
      pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout, redisPassword, redisDatabase)

      val hook: Thread = new Thread {
        override def run(): Unit = pool.destroy()
      }
      sys.addShutdownHook(hook.run())
    }
  }

  private def getPool: JedisPool = {
    Objects.requireNonNull(pool)
  }

  def getResource: Jedis = {
    getPool.getResource
  }

  def recycleResource(jedis: Jedis): Unit = {
    if (jedis != null) {
      jedis.close()
    }
  }

}
