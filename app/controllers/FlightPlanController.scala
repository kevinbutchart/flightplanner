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
import models.Legs
import models.Leg
import models.Places
import models.Winds
import models.Wind
import utils.DoubleFormat._


object FlightPlanController extends Controller {
    var currentJourney = 1
    
	val LegForm = Form(
	    mapping(
	      "journeyId" -> number,
        "from" -> number,
         "to" -> number,
	     "id" -> optional(number)
	)(Leg.apply)(Leg.unapply))

  val WindForm = Form(
    mapping(
      "altitude" -> optional(number),
      "direction" -> number,
      "speed" -> number,
      "oat" -> number//,
      //"id" -> optional(number)
    )(Wind.apply)(Wind.unapply))

 	def index = Action {
		  Ok(views.html.flightplanindex(currentJourney))
	}
	def show(id:Int) = Action {
	    currentJourney = id
		Ok(views.html.flightplanindex(currentJourney))
	}
	def add(journeyId:Int) = Action {
	    Ok(views.html.flightplan.add(journeyId, LegForm))
	}
  def addWind = Action {
    Ok(views.html.flightplan.addWind(WindForm))
  }
  def saveWind = Action{implicit request =>
    val windform = WindForm.bindFromRequest.get
    Winds.create(windform)
    Redirect(routes.FlightPlanController.index)
  }

	def save = Action{implicit request =>
		val legform = LegForm.bindFromRequest.get
		Legs.create(legform)
		Redirect(routes.FlightPlanController.index)
	}
	def edit(id:Int) = Action {
		Ok(views.html.flightplan.edit(id, LegForm.fill(Legs.find(id))))
	}
	def update(updateid: Int) = Action {implicit request =>
		val leg = LegForm.bindFromRequest.get
//		val newLeg = Leg.copy(id = Some(updateid))
//		Legs.update(newLeg)
		Redirect(routes.FlightPlanController.index)
	}
	def delete(id: Int) = Action {implicit request =>
		Legs.delete(id)
		Redirect(routes.FlightPlanController.index)
	}
}
