
function updateFlightPlan(s) {
    console.log("test");
    console.log(s[s.selectedIndex].value);
    jsRoutes.controllers.PlacesController.index();
    //jsRoutes.controllers.FlightPlanController.show(s[s.selectedIndex].value);//s.selectedIndex);
}