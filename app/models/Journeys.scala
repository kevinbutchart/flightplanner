package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.Play.current
import play.api.data.Forms._
case class Journey(name: String, id: Option[Int] = None)

class Journeys(tag: Tag) extends Table[Journey](tag, "JOURNEYS") {
  // Auto Increment the id primary key column
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  // The name can't be null
  def name = column[String]("NAME", O.NotNull)
  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a User
  
  

  def * = (name, id.?) <> (Journey.tupled, Journey.unapply)
}

object Journeys {
	val db = play.api.db.slick.DB
	val journeys = TableQuery[Journeys]
	def all: List[Journey] = db.withSession { implicit session =>
		journeys.sortBy(_.id.asc.nullsFirst).list
	}
	def create(newjourney: Journey) = db.withTransaction{ implicit session =>
		journeys += newjourney
	}
	def find(id: Int): Journey = db.withSession{ implicit session =>
		journeys.filter(_.id === id).first
	}
	def update(updateJourney: Journey) = db.withTransaction{ implicit session =>
		journeys.filter(_.id === updateJourney.id).update(updateJourney)
	}
	def delete(id: Int) = db.withTransaction{ implicit session =>
		journeys.filter(_.id === id).delete
	}
}
