package models

import scala.collection.mutable.HashMap
import scala.slick.driver.PostgresDriver.simple._
import play.Play
import play.api.Play.current
import play.api.data.Forms._
import java.io.FileNotFoundException
import java.io.File
import utils._

/**
 * Created by kevin on 06/04/15.
 */
case class MetarStation(latitude: Double, longitude : Double, icao : String,name :String, elevation: Int, id : Option[Int] = None) {
  def place = Place(icao, latitude, longitude)

  var metarDetail = ""
  var metarLastRead = 0L
  def getMetar : String = {
    if (System.currentTimeMillis - metarLastRead > 1000 * 60 *15)
    {
      try {
        var metarRaw = scala.io.Source.fromURL("http://weather.noaa.gov/pub/data/observations/metar/stations/"+icao +".TXT").getLines
        val metarDate = metarRaw.next
        metarDetail = metarRaw.next
      } catch {
        case ex : FileNotFoundException => println("no metar found for: " + icao)
      }
      metarLastRead = System.currentTimeMillis
    }
    metarDetail
  }

  def getParsedMetar = MetarParser.parseMetar(getMetar)
}


class MetarStations(tag: Tag) extends Table[MetarStation](tag, "METARSTATIONS") {
  // Auto Increment the id primary key column
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  // The name can't be null
  def name = column[String]("NAME", O.NotNull)
  def icao = column[String]("ICAO", O.NotNull)
  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a User
  def latitude = column[Double]("LATITUDE",O.NotNull)
  def longitude = column[Double]("LONGITUDE",O.NotNull)
  def elevation = column[Int]("ELEVATION",O.NotNull)



  def * = (latitude, longitude, icao, name, elevation, id.?) <> (MetarStation.tupled, MetarStation.unapply)
}

object MetarStations {
  val db = play.api.db.slick.DB
  val stations = TableQuery[MetarStations]

  def updateList(force: Boolean = false) {
    if (force || all.size == 0)
    {
      var stationList = new HashMap[String, MetarStation]
      //val stations = scala.io.Source.fromURL("https://www.aviationweather.gov/docs/metar/stations.txt").getLines
      //val stations = scala.io.Source.fromURL("/public/stations.txt").getLines
      //val stations = Play.resource("stations.txt")
      val stations = scala.io.Source.fromFile(Play.application().getFile("assets/stations.txt")).getLines
      for (s <- stations) {
        if (s.length==83) {
          val name = s.substring(3,19)
          val icao = s.substring(20,24)
          val met = s.substring(62,63)
          val taf = s.substring(68,69)
          var latitude = s.substring(39,41).toDouble + s.substring(42,44).toDouble/60
          if (s.substring(44,45)=="S") {latitude *= -1}
          var longitude = s.substring(48,50).toDouble + s.substring(51,53).toDouble/60
          if (s.substring(53,54)=="W") longitude *= -1
          var elevation = s.substring(55,59).trim.toInt
          println("name: "+ name + " elevation: "+elevation)

          if (met=="X" && taf=="T") {
            create(MetarStation(latitude, longitude, icao, name, elevation))
          }
        }
      }
    }
  }
  def all: List[MetarStation] = db.withSession { implicit session =>
    stations.sortBy(_.id.asc.nullsFirst).list
  }

  def allWith(latMin : Double, latMax : Double, longMin: Double, longMax: Double) : List[MetarStation]
    = db.withSession { implicit session =>
    stations.filter(_.latitude > latMin)
            .filter(_.latitude < latMax)
            .filter(_.longitude > longMin)
            .filter(_.longitude < longMax)
            .list
  }
  def create(newMs: MetarStation) = db.withTransaction{ implicit session =>
    stations += newMs
  }
  def find(id: Int): MetarStation = db.withSession{ implicit session =>
    stations.filter(_.id === id).first
  }
  def find(icao: String) : MetarStation = db.withSession{ implicit session =>
    stations.filter(_.icao === icao).first
  }
  def update(updateMetarStation: MetarStation) = db.withTransaction{ implicit session =>
    stations.filter(_.id === updateMetarStation.id).update(updateMetarStation)
  }
  def delete(id: Int) = db.withTransaction{ implicit session =>
    stations.filter(_.id === id).delete
  }
}
/*
class MetarStations(country : String) {


  def nearest(place : Place) = {
    var nearest : Place = null
    var nearestDistance = 0.0
    for (ms <- stationList) {
       if (nearest == null) {
         nearest = ms._2.place
         nearestDistance = place.distNm(ms._2.place)
       } else {
         val dist = place.distNm(ms._2.place)
         if (dist<nearestDistance) {
           nearest = ms._2.place
           nearestDistance = place.distNm(ms._2.place)
         }
       }
    }
    nearest
  }

}
*/