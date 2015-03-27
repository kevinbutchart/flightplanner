package controllers

import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._
import models.Journey
import models.Journeys
import models.Places
import utils.DoubleFormat._


object JourneysController extends Controller {
	val JourneyForm = Form(
	    mapping(
	        "name" -> text,
	        "id" -> optional(number)
	)(Journey.apply)(Journey.unapply))
  	def index = Action {
		  Ok(views.html.journeysindex(Journeys.all, Places.all))
	}
	def show(id:Int) = Action {
		Ok(views.html.journeys.show(Journeys.find(id)))
	}
	def add = Action {
	    Ok(views.html.journeys.add(JourneyForm))
	}
	def save = Action{implicit request =>
		val Journey = JourneyForm.bindFromRequest.get
		Journeys.create(Journey)
		Redirect(routes.JourneysController.index)
	}
	def edit(id:Int) = Action {
		Ok(views.html.journeys.edit(id, JourneyForm.fill(Journeys.find(id))))
	}
	def update(updateid: Int) = Action {implicit request =>
		val Journey = JourneyForm.bindFromRequest.get
		val newJourney = Journey.copy(id = Some(updateid))
		Journeys.update(newJourney)
		Redirect(routes.JourneysController.index)
	}
	def delete(id: Int) = Action {implicit request =>
		Journeys.delete(id)
		Redirect(routes.JourneysController.index)
	}
}
