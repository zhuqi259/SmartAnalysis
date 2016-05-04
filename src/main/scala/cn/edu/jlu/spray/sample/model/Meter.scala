package cn.edu.jlu.spray.sample.model

import java.sql.Timestamp

import slick.driver.MySQLDriver.api._

case class Meter(num: String, id: Option[Long] = None, time: Timestamp = new Timestamp(System.currentTimeMillis()))

class Meters(tag: Tag) extends Table[Meter](tag, "t_meter") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def time = column[Timestamp]("m_time")

  def num = column[String]("m_num")

  override def * = (num, id.?, time) <>((Meter.apply _).tupled, Meter.unapply)
}

case class MeterSearchParameters(begin: Timestamp, end: Timestamp, step: Int)

case class MeterSearchResult(id: Long, num: String, time: Timestamp)