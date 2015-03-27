package models

import scala.slick.driver.PostgresDriver.simple._
import play.api.Play.current
import play.api.data.Forms._
import math._

case class Place(name: String, latitude: Double, longitude: Double, id: Option[Int] = None) {
    def prettyLatitude = {
        val latval = Math.abs(latitude)

        val hemi = if (latitude >= 0) "N" else "S"
        val degrees = latval.toInt
        val minutes = ((latval%1)*60).toInt
        val seconds = ((latval*60)%1)*60
        degrees + "˚" + minutes + "'" + "%.2f".format(seconds) +"''" + hemi
    }
    def prettyLongitude = {
        val longval = Math.abs(longitude)
        val hemi = if (longitude >= 0) "E" else "W"
      
        val degrees = longval.toInt
        val minutes = ((longval%1)*60).toInt
        val seconds = ((longval*60)%1)*60
        degrees + "˚" + minutes + "'" + "%.2f".format(seconds) +"''" + hemi
    }
    
    def latR = latitude.toRadians
    def longR = longitude.toRadians
    
    def initialBearing(to : Place) = {
        atan2(sin(to.longR-longR)*cos(to.latR), 
            cos(latR)*sin(to.latR)-sin(latR)*cos(to.latR)*cos(to.longR-longR)) 
    }
}
class Places(tag: Tag) extends Table[Place](tag, "PLACES") {
  // Auto Increment the id primary key column
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  // The name can't be null
  def name = column[String]("NAME", O.NotNull)
  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a User
  def latitude = column[Double]("LATITUDE",O.NotNull)
  def longitude = column[Double]("LONGITUDE",O.NotNull)
  
  

  def * = (name, latitude, longitude, id.?) <> (Place.tupled, Place.unapply)
}

object Places {
	val db = play.api.db.slick.DB
	val places = TableQuery[Places]
	def all: List[Place] = db.withSession { implicit session =>
		places.sortBy(_.id.asc.nullsFirst).list
	}
	def create(newplace: Place) = db.withTransaction{ implicit session =>
		places += newplace
	}
	def find(id: Int): Place = db.withSession{ implicit session =>
		places.filter(_.id === id).first
	}
	def find(name: String) : Place = db.withSession{ implicit session =>
		places.filter(_.name === name).first
	}
	def update(updatePlace: Place) = db.withTransaction{ implicit session =>
		places.filter(_.id === updatePlace.id).update(updatePlace)
	}
	def delete(id: Int) = db.withTransaction{ implicit session =>
		places.filter(_.id === id).delete
	}
}