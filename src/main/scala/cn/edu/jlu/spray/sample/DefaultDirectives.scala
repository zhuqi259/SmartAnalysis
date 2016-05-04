package cn.edu.jlu.spray.sample

import java.util.TimeZone

import akka.actor.ActorRefFactory
import org.json4s.jackson.Serialization
import org.json4s.{DefaultFormats, Formats}
import spray.http.ContentTypes
import spray.http.MediaTypes._
import spray.httpx.encoding.Gzip
import spray.httpx.marshalling.Marshaller
import spray.routing.{Route, Directives}
import spray.routing.directives.RespondWithDirectives

/**
  * Created by zhuqi259 on 2015/12/10.
  */
class ThreadLocal[A](init: => A) extends java.lang.ThreadLocal[A] with (() => A) {
  override def initialValue = init

  def apply = get
}

object ZQDefaultFormats extends DefaultFormats {
  val localTimeZone = TimeZone.getTimeZone("GMT+8")
  val losslessDate = {
    def createSdf = {
      val f = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      f.setTimeZone(localTimeZone)
      f
    }
    new ThreadLocal(createSdf)
  }
}

trait DefaultDirectives extends Directives {

  this: RespondWithDirectives =>

  implicit def json4sFormats: Formats = new DefaultFormats {
    override def dateFormatter = ZQDefaultFormats.losslessDate()
  }

  def responseJson(obj: AnyRef): Route = {
    respondWithMediaType(`application/json`) {
      implicit def json4sMarshaller[T <: AnyRef] =
        Marshaller.delegate[T, String](ContentTypes.`application/json`)(Serialization.write(_))
      complete {
        obj
      }
    }
  }

  def responseHtml(html: String)(implicit actorRefFactory: ActorRefFactory): Route = {
    respondWithMediaType(`text/html`) {
      encodeResponse(Gzip) {
        complete(html)
      }
    }
  }
}
