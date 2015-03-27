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
