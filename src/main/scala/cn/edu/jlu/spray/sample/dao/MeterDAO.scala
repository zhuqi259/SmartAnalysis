package cn.edu.jlu.spray.sample.dao

import cn.edu.jlu.spray.sample.model.{Meter, MeterSearchParameters, Meters}
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by zhuqi259 on 2015/12/12.
  */
trait MeterDAO {
  def createTable: Future[Unit]

  def findAll: Future[Seq[Meter]]

  def insert(meter: Meter): Future[Int]

  def getById(id: Long): Future[Option[Meter]]

  def topN(n: Int): Future[Seq[Meter]]

  def search(params: MeterSearchParameters): Future[Seq[Meter]]
}

class MeterDAOImpl extends MeterDAO {
  val db = Database.forConfig("mysql")
  lazy val meters = TableQuery[Meters]

  override def createTable: Future[Unit] = {
    db.run(DBIO.seq(meters.schema.create))
  }

  override def findAll: Future[Seq[Meter]] = {
    db.run(meters.result)
  }

  override def insert(meter: Meter): Future[Int] = {
    db.run(meters += meter)
  }

  override def getById(id: Long): Future[Option[Meter]] = {
    db.run(meters.filter(_.id === id).result.headOption)
  }

  override def topN(n: Int): Future[Seq[Meter]] = {
    db.run(meters.result.map(_ takeRight n))
  }

  override def search(params: MeterSearchParameters): Future[Seq[Meter]] = {
    val data = meters.filter {
      m => m.time >= params.begin && m.time < params.end
    }.sortBy(_.time)
    db.run(data.result)
  }
}

