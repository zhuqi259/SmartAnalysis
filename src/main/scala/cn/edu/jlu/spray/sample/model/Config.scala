package cn.edu.jlu.spray.sample.model

import slick.driver.MySQLDriver.api._

case class Config(id: Option[Long] = None, ip: String, longPort: Int, shortPort: Int, control: String, meter: String)

class Configs(tag: Tag) extends Table[Config](tag, "t_config") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def ip = column[String]("c_ip")

  def longPort = column[Int]("c_longport")

  def shortPort = column[Int]("c_shortport")

  def control = column[String]("c_control")

  def meter = column[String]("c_meter")

  override def * = (id.?, ip, longPort, shortPort, control, meter) <>((Config.apply _).tupled, Config.unapply)
}
