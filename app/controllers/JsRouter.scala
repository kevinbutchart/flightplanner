package controllers

import play.api._, mvc._

object JsRouter extends Controller {

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes"){
        JourneysController.index
      }
    ).as("text/javascript")
  }

}
