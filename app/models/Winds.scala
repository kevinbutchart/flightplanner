package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.Play.current
import play.api.data.Forms._
/**
 * Created by kevin on 24/03/15.
 */
case class Wind(altitude: Option[Int], direction: Int, speed: Int, oat: Int){//}, id: Option[Int]){
  def ias = 85
  def tempDiffISA = oat - (15 - 2* altitude.get/1000)
  def tas = ias + ias * (0.01  *altitude.get/600) + ias * (0.01*tempDiffISA/5)
  def maxdrift = 60/tas *speed
}

class Winds(tag: Tag) extends Table[Wind](tag, "WINDS") {
  // Auto Increment the id primary key column
  //def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def altitude = column[Int]("ALTITUDE", O.PrimaryKey)
  def direction = column[Int]("DIR",O.NotNull)
  def speed = column[Int]("SPEED",O.NotNull)
  def oat = column[Int]("OAT",O.NotNull)


  def * = (altitude.?, direction, speed, oat) <> (Wind.tupled, Wind.unapply)
}

object Winds {
  val db = play.api.db.slick.DB
  val winds = TableQuery[Winds]
  def all: List[Wind] = db.withSession { implicit session =>
    winds.sortBy(_.altitude.asc.nullsFirst).list
  }
  def create(newWind: Wind) = db.withTransaction{ implicit session =>
    var wind = find(newWind.altitude.get)
    if (wind!=null) {
      delete(wind.altitude.get)
    }
    winds += newWind
  }

  def find(altitude: Int): Wind = db.withSession{ implicit session =>
    var lw = winds.filter(_.altitude === altitude).list
    if (lw.isEmpty) null else lw.head
  }

//  def findAltitude(altitude: Int): Wind = db.withSession{ implicit session =>
//    val lw = winds.filter(_.altitude === altitude)
//    System.out.println(lw)
//    if (lw.size==0) {
//      null
//    } else {
//      lw.first
//    }
//  }
  def update(upd: Wind) = db.withTransaction{ implicit session =>
    winds.filter(_.altitude === upd.altitude).update(upd)


  }
  def delete(altitude: Int) = db.withTransaction{ implicit session =>
    winds.filter(_.altitude === altitude).delete
  }
}
