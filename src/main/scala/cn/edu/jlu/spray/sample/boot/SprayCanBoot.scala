package cn.edu.jlu.spray.sample.boot

import akka.io.IO
import akka.pattern.ask
import spray.can.Http

object SprayCanBoot extends BasicBoot {
  
  def main(args: Array[String]) {
    // start a new HTTP server on port 8080 with our service actor as the handler
    IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
  }
}
