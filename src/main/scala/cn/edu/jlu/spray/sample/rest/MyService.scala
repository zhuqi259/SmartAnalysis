package cn.edu.jlu.spray.sample.rest

import java.sql.Timestamp
import java.util.TimeZone

import akka.actor.Actor
import akka.event.slf4j.SLF4JLogging
import cn.edu.jlu.spray.sample.DefaultDirectives
import cn.edu.jlu.spray.sample.dao.{PowerDAOImpl, MeterDAOImpl}
import cn.edu.jlu.spray.sample.model.{Meter, MeterSearchParameters, MeterSearchResult}
import org.joda.time.{DateTime, Hours, Days, Weeks, Months, Years}
import com.github.nscala_time.time.Imports._
import spray.routing.{Route, HttpService}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService with DefaultDirectives with SLF4JLogging {
  val meterService = new MeterDAOImpl
  val powerService = new PowerDAOImpl

  def staticPrefixes = List("css", "js", "img", "lib", "static") map {
    pathPrefix(_)
  } reduce {
    _ | _
  }

  def stripLeadingSlash(path: String) = if (path startsWith "/") path.tail else path

  val staticPath =
    staticPrefixes &
      extract(ctx => stripLeadingSlash(ctx.request.uri.path.toString))

  val staticRoutes =
    get {
      staticPath { path =>
        getFromResource(path)
      }
    }

  implicit def str2Timestamp(str: String): Timestamp = {
    val localTimeZone = TimeZone.getTimeZone("GMT+8")
    val sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    sdf.setTimeZone(localTimeZone)
    new Timestamp(sdf.parse(str).getTime)
  }

  def meterSearch(data: Seq[Meter], begin: DateTime, end: DateTime, count: Int, period: Int => Period): Route = {
    val results = for {
      i <- 0 to count
      b = begin + period(i)
      e = b + period(1)
      split = data.filter {
        m =>
          val current = new DateTime(m.time.getTime)
          current >= b && current < e
      }.toVector
      num = if (split.length < 2) 0.0 else split(split.length - 1).num.toDouble - split(0).num.toDouble
      result = MeterSearchResult(i, f"$num%.2f", new Timestamp(b.getMillis))
    } yield result
    val realResults = if (end > (begin + period(count))) results else results.take(count)
    responseJson(realResults)
  }

  val myRoute =
    path("meter") {
      get {
        parameters('begin.as[Timestamp], 'end.as[Timestamp], 'step.as[Int]).as(MeterSearchParameters) {
          searchParameters: MeterSearchParameters => {
            // println(searchParameters)
            val data: Seq[Meter] = Await.result(meterService.search(searchParameters), Duration.Inf)
            val begin: DateTime = new DateTime(searchParameters.begin.getTime)
            val end: DateTime = new DateTime(searchParameters.end.getTime)
            searchParameters.step match {
              case 0 => // 小时
                val count: Int = Hours.hoursBetween(begin, end).getHours
                meterSearch(data, begin, end, count, Period.hours)
              case 1 => // 天
                val count: Int = Days.daysBetween(begin, end).getDays
                meterSearch(data, begin, end, count, Period.days)
              case 2 => // 周
                val count: Int = Weeks.weeksBetween(begin, end).getWeeks
                meterSearch(data, begin, end, count, Period.weeks)
              case 3 => // 月
                val count: Int = Months.monthsBetween(begin, end).getMonths
                meterSearch(data, begin, end, count, Period.months)
              case 4 => // 年
                val count: Int = Years.yearsBetween(begin, end).getYears
                meterSearch(data, begin, end, count, Period.years)
              case _ => responseHtml("Oops!!!")
            }
          }
        }
      }
    } ~
      path("meter" / Segment) {
        command => {
          get {
            log.debug("command => %s".format(command))
            command match {
              case "single" =>
                val meters = Await.result(meterService.topN(1), Duration.Inf)
                responseJson(meters)
              case _ => responseHtml("Oops!!!")
            }
          }
        }
      } ~
      path("power" / Segment) {
        command => {
          get {
            log.debug("command => %s".format(command))
            command match {
              case "list" =>
                val powers = Await.result(powerService.topN(10), Duration.Inf)
                responseJson(powers)
              case _ => responseHtml("Oops!!!")
            }
          }
        }
      } ~ staticRoutes
}