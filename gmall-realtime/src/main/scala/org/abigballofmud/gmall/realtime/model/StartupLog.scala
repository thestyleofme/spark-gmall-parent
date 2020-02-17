package org.abigballofmud.gmall.realtime.model

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/17 1:38
 * @since 1.0
 */
case class StartupLog(mid: String,
                      uid: String,
                      appid: String,
                      area: String,
                      os: String,
                      ch: String,
                      var logType: String,
                      vs: String,
                      var logDate: String,
                      var logHour: String,
                      ts: Long
                     ) extends Serializable
