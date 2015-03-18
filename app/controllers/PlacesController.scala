package controllers

import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._
import models.Place
import models.Places
import utils.DoubleFormat._


object PlacesController extends Controller {
	val PlaceForm = Form(
	    mapping(
	        "name" -> text,
	        "latitude" -> double,
	        "longitude" -> double,
	        "id" -> optional(number)
	)(Place.apply)(Place.unapply))
  	def index = Action {
		  Ok(views.html.index(Places.all))
	}
	def show(id:Int) = Action {
		Ok(views.html.places.show(Places.find(id)))
	}
	def add = Action {
	    Ok(views.html.places.add(PlaceForm))
	}
	def save = Action{implicit request =>
		val Place = PlaceForm.bindFromRequest.get
		Places.create(Place)
		Redirect(routes.Application.index)
	}
	def edit(id:Int) = Action {
		Ok(views.html.places.edit(id, PlaceForm.fill(Places.find(id))))
	}
	def update(updateid: Int) = Action {implicit request =>
		val Place = PlaceForm.bindFromRequest.get
		val newPlace = Place.copy(id = Some(updateid))
		Places.update(newPlace)
		Redirect(routes.Application.index)
	}
	def delete(id: Int) = Action {implicit request =>
		Places.delete(id)
		Redirect(routes.Application.index)
	}
}
