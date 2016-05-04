package cn.edu.jlu.spray.sample.dao

import cn.edu.jlu.spray.sample.model.{Powers, Power}
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait PowerDAO {
  def createTable: Future[Unit]

  def findAll: Future[Seq[Power]]

  def insert(power: Power): Future[Int]

  def getById(id: Long): Future[Option[Power]]

  def topN(n: Int): Future[Seq[Power]]
}

class PowerDAOImpl extends PowerDAO {
  val db = Database.forConfig("mysql")
  lazy val powers = TableQuery[Powers]

  override def createTable: Future[Unit] = {
    db.run(DBIO.seq(powers.schema.create))
  }

  override def findAll: Future[Seq[Power]] = {
    db.run(powers.result)
  }

  override def insert(power: Power): Future[Int] = {
    db.run(powers += power)
  }

  override def getById(id: Long): Future[Option[Power]] = {
    db.run(powers.filter(_.id === id).result.headOption)
  }

  override def topN(n: Int): Future[Seq[Power]] = {
    db.run(powers.result.map(_ takeRight n))
  }
}

