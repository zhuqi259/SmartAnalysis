package cn.edu.jlu.spray.sample.model

import java.sql.Timestamp

import slick.driver.MySQLDriver.api._

case class Power(num: String, id: Option[Long] = None, time: Timestamp = new Timestamp(System.currentTimeMillis()))

class Powers(tag: Tag) extends Table[Power](tag, "t_power") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def time = column[Timestamp]("p_time")

  def num = column[String]("p_num")

  override def * = (num, id.?, time) <>((Power.apply _).tupled, Power.unapply)
}
