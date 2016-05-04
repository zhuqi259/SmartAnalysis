package cn.edu.jlu.spray.sample

import cn.edu.jlu.spray.sample.rest.MyService
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class MyServiceSpec extends Specification with Specs2RouteTest with MyService {
  def actorRefFactory = system

  "MyService" should {

    "GET requests to root path unhandled" in {
      Get() ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }

    "GET requests to meter path" in {
      Get("/meter?begin=2016-01-01%2000:00:00&end=2016-01-02%2000:00:00&step=0") ~> myRoute ~> check {
        status === OK
      }
    }

  }
}
