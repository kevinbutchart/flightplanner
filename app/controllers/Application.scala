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
import models.Journeys

object Application extends Controller {
  def index = Action {
    Ok(views.html.index(Places.all))
  }

  def journeysindex = Action {
    Ok(views.html.journeysindex(Journeys.all, Places.all))
  }

//  def javascriptRoutes() = {
//    response().setContentType("text/javascript")
//    return ok(
//      Routes.javascriptRouter("jsRoutes",
//        controllers.routes.javascript.FlightPlanController.show()
//      )
//    )
//  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes"){
        controllers.routes.javascript.FlightPlanController.show
        controllers.routes.javascript.PlacesController.index
      }
    ).as("text/javascript")
  }
}
