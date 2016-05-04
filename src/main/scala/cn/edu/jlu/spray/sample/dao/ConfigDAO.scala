package cn.edu.jlu.spray.sample.dao

import cn.edu.jlu.spray.sample.model.{Config, Configs}
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future

trait ConfigDAO {
  def createTable: Future[Unit]

  def getOnlyOne: Future[Config]

  def getById(id: Long): Future[Option[Config]]

  def insert(config: Config): Future[Int]

  def update(config: Config): Future[Int]
}

class ConfigDAOImpl extends ConfigDAO {
  val db = Database.forConfig("mysql")
  lazy val configs = TableQuery[Configs]

  override def createTable: Future[Unit] = {
    db.run(DBIO.seq(configs.schema.create))
  }

  override def getOnlyOne: Future[Config] = {
    db.run(configs.result.head)
  }

  override def getById(id: Long): Future[Option[Config]] = {
    db.run(configs.filter(_.id === id).result.headOption)
  }

  override def insert(config: Config): Future[Int] = {
    db.run(configs += config)
  }

  override def update(config: Config): Future[Int] = {
    db.run(configs.filter(_.id === config.id).update(config))
  }
}

