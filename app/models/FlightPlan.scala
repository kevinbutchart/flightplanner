package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.Play.current
import play.api.data.Forms._
import math._
import utils.Bearing

case class Leg(journeyId: Int, from: Int, to: Int, id: Option[Int] = None) {
  def f = Places.find(from)
  def t = Places.find(to)
  def lengthNM = {
      if (f!=null && t!=null) {
          var longdiff = t.longR - f.longR
          acos(sin(f.latR)*sin(t.latR)+
                cos(f.latR)*cos(t.latR)*cos(longdiff))*6371.0*0.539956804
      } else {
        0
      }
  }
  
  def initialBearing = {
     if (f!=null && t!=null) {
       f.initialBearing(t) 
     } else {
         0
     }
  }

  def getPoints(intervalNm: Double, startOffset: Double) = f.getPoints(t, intervalNm, startOffset)

  
  def TrkT = (initialBearing.toDegrees + 360) % 360
  def TrkRelWindT = ((TrkT - Winds.find(2000).direction) %360)-180
  def AngleIntoWind = (asin(Winds.find(2000).speed * sin(TrkRelWindT.toRadians)/Winds.find(2000).tas)).toDegrees
  def HdgT = Bearing.normalise(TrkT + AngleIntoWind)
  def Var = 2
  def HdgM = HdgT + Var
  def GS = {
    var w = Winds.find(2000)
    sqrt(w.speed*w.speed + w.tas*w.tas - 2 * w.tas * w.speed * cos((180-abs(AngleIntoWind)-abs(TrkRelWindT)).toRadians))
  }
  def LegTime = floor((lengthNM / GS)*60)
}

class Legs(tag: Tag) extends Table[Leg](tag, "JOURNEY_LEGS") {
  // Auto Increment the id primary key column
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def journeyId = column[Int]("JOURNEY_ID", O.NotNull)


  // The name can't be null
  def from = column[Int]("FROM", O.NotNull)
  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a User
  def to = column[Int]("TO", O.NotNull)

  def * = (journeyId, from, to, id.?) <> (Leg.tupled, Leg.unapply)
}

object Legs {
	val db = play.api.db.slick.DB
	val legs = TableQuery[Legs]
	def all: List[Leg] = db.withSession { implicit session =>
		legs.sortBy(_.id.asc.nullsFirst).list
	}
	def forJourney(choice: Int): List[Leg] = db.withSession { implicit session =>
		legs.filter(_.journeyId === choice).list
	}
  def pointsForJourney(choice: Int) : List[Place] = {
    var legs = forJourney(choice)
    var p = List[Place]()
    var e : Double = 0.0
    for (l <- legs) {
      var res = l.getPoints(1.0, e)
      p = p ::: res._1
      e = res._2
    }
    p
  }

  def stationsInArea(points : List[Place]) : List[MetarStation] = {
    var minLat = 90.0
    var maxLat = -90.0
    var minLong = 360.0
    var maxLong = -360.0
    for (p <- points) {
      if (p.latitude < minLat) { minLat = p.latitude}
      if (p.latitude > maxLat) { maxLat = p.latitude}
      if (p.longitude < minLong) { minLong = p.longitude}
      if (p.longitude > maxLong) { maxLong = p.longitude}
    }
    minLat -= .5
    maxLat += .5
    minLong -= .5
    maxLong += .5

    MetarStations.allWith(minLat,maxLat,minLong,maxLong)
  }


  def closestMetarStations(points : List[Place]) : List[MetarStation] = {
    var stations = stationsInArea(points : List[Place])
    points.map(p => {
      var minDist = 10000.0
      var nearest : MetarStation = null
      for (ms <- stations) {
        val d = ms.place.distNm(p)

        if (d < minDist) {
          minDist = d
          nearest = ms
        }
      }
      nearest
    })
  }

  def journeyCloudBases(choice: Int, samples: Int) : List[(String, String, Int,Int,Int,Int)] = {
    var points = pointsForJourney(choice)
    val ms = closestMetarStations(points)
    var parray = points.toArray

    if (ms.length > 0) {
      val ret = new Array[(String, String, Int,Int,Int,Int)](samples)
      for (ind <- 0 until samples) {
        var msIndex = ((ms.length.toDouble/samples.toDouble)*ind).toInt
        if (! (msIndex < ms.length)) {
          msIndex = ms.length -1
        }
        val station = ms(msIndex)
        if (station != null) {
          val parsedMetar = station.getParsedMetar

          var few = parsedMetar._6._1
          var sct = parsedMetar._6._2
          var bkn = parsedMetar._6._3
          var ovc = parsedMetar._6._4
          if (ovc == -1) ovc = 100
          if (bkn == -1) bkn = ovc
          if (sct == -1) sct = bkn
          if (few == -1) few = sct


          ovc *= 100
          bkn *= 100
          sct *= 100
          few *= 100

          ovc += station.elevation
          bkn += station.elevation
          sct += station.elevation
          few += station.elevation

          ret(ind) = (parsedMetar._1, parray(msIndex).name, few, sct, bkn, ovc)
        } else {
          ret(ind) = ("NONE","NONE", -1 , -1, -1, -1)
        }
      }
      println(ret)
      return ret.toList
    } else {
      return List[(String, String, Int,Int,Int,Int)]()
    }
  }



	def create(newLeg: Leg) = db.withTransaction{ implicit session =>
		legs += newLeg
	}
	def find(id: Int): Leg = db.withSession{ implicit session =>
		legs.filter(_.id === id).first
	}
	def update(updateLeg: Leg) = db.withTransaction{ implicit session =>
		legs.filter(_.id === updateLeg.id).update(updateLeg)
	}
	def delete(id: Int) = db.withTransaction{ implicit session =>
		legs.filter(_.id === id).delete
	}
}
