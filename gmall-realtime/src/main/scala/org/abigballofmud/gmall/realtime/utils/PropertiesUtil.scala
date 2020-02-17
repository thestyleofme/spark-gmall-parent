package org.abigballofmud.gmall.realtime.utils

import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Properties

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/16 23:32
 * @since 1.0
 */
object PropertiesUtil {

  def main(args: Array[String]): Unit = {
    val properties: Properties = PropertiesUtil.load("config.properties")
    println(properties.getProperty("kafka.broker.list"))
  }

  def load(propertiesName: String): Properties = {
    val prop = new Properties()
    prop.load(
      new InputStreamReader(Thread.currentThread().getContextClassLoader.getResourceAsStream(propertiesName),
        StandardCharsets.UTF_8))
    prop
  }
}
