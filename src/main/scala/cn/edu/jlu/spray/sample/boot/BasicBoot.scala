package cn.edu.jlu.spray.sample.boot

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import cn.edu.jlu.mina.client.SimpleClient
import cn.edu.jlu.mina.server.{MinaLongConnServer, MinaShortConnServer}
import cn.edu.jlu.spray.sample.dao.{ConfigDAOImpl, MeterDAOImpl, PowerDAOImpl}
import cn.edu.jlu.spray.sample.model.{Config, Meter, Power}
import cn.edu.jlu.spray.sample.rest.MyServiceActor

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by zhuqi259 on 2015/12/12.
  */
trait BasicBoot {
  implicit val system = ActorSystem("System")

  implicit val timeout = Timeout(5.seconds)

  val service = system.actorOf(Props[MyServiceActor], "service")

  val StartTCPService = "startTCPService"

  //  val StartKeepAliveService = "startKeepAliveService"

  val ReadMetaData = "readMetaData"

  val ReadPowerData = "readPowerData"

  val meterService = new MeterDAOImpl

  val powerService = new PowerDAOImpl

  val configService = new ConfigDAOImpl

  val config: Config = Await.result(configService.getOnlyOne, Duration.Inf)

  val ip: String = config.ip
  val shortPort: Int = config.shortPort
  val longPort: Int = config.longPort
  val control: String = config.control
  val meter: String = config.meter

  val readActor = system.actorOf(Props(new Actor {
    def receive = {
      case ReadMetaData =>
        val result: String = SimpleClient.INSTANCE.readElectricityMeter(ip, shortPort, control, meter)
        if (!"error".equals(result)) {
          val meter: Meter = Meter(result)
          val insert = meterService.insert(meter)
          insert.onSuccess {
            case e =>
              println("电量数据 => " + e)
          }
        }
      case ReadPowerData =>
        // val result: String = SimpleClient.INSTANCE.readElectricityPower("192.168.1.99", 1235, "022276106104", "696999010000")
        val result: String = SimpleClient.INSTANCE.readElectricityPower(ip, shortPort, control, meter)
        if (!"error".equals(result)) {
          val power: Power = Power(result)
          val insert = powerService.insert(power)
          insert.onSuccess {
            case e =>
              println("功率数据 => " + e)
          }
        }
      case StartTCPService =>
        val minaLongConnServer: MinaLongConnServer = new MinaLongConnServer(longPort)
        val minaShortConnServer: MinaShortConnServer = new MinaShortConnServer(shortPort)
        try {
          minaLongConnServer.start()
          minaShortConnServer.start()
        } catch {
          case _: Throwable => println("Oops~~~")
        }
      //      case StartKeepAliveService =>
      //        // TODO 不需要这部分,仅用于测试
      //        new Thread() {
      //          override def run() = {
      //            val client: TcpKeepAliveClient = new TcpKeepAliveClient(ip, longPort)
      //            client.receiveAndSend() // 别忘了开线程, receiveAndSend是while(true)死循环
      //          }
      //        }.start()
    }
  }))

  system.scheduler.scheduleOnce(
    10 seconds,
    readActor,
    StartTCPService
  )

  //  // TODO 不需要这部分,仅用于测试
  //  system.scheduler.scheduleOnce(
  //    15 seconds,
  //    readActor,
  //    StartKeepAliveService
  //  )

  system.scheduler.schedule(
    100 seconds,
    45 seconds, // 每45s读取实时功率
    readActor,
    ReadPowerData
  )

  system.scheduler.schedule(
    200 seconds,
    60 * 15 seconds, // 每15分钟统计电量
    readActor,
    ReadMetaData
  )
}
