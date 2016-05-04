package cn.edu.jlu.spray.sample.boot

import akka.actor.ActorRef
import spray.servlet.WebBoot

/**
  * Created by zhuqi259 on 2015/12/12.
  */
class ContainerBoot extends WebBoot with BasicBoot {
  override def serviceActor: ActorRef = service
}
