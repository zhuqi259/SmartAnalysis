package cn.edu.jlu.spray.sample.dao

import cn.edu.jlu.spray.sample.model.Config

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by zhuqi259 on 2016/1/1.
  */
object ConfigDAOTest extends App {
  val configService = new ConfigDAOImpl
  val config: Config = Await.result(configService.getOnlyOne, Duration.Inf)
  println(config)
  val updateConfig = Config(Some(1), "192.168.1.125", 8000, 8001, "022282253522", "467710040000")
  val updated: Int = Await.result(configService.update(updateConfig), Duration.Inf)
  println("更新数据 => " + updated)
  val config2: Config = Await.result(configService.getOnlyOne, Duration.Inf)
  println(config2)
}
