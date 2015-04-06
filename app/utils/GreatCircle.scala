//package utils

import scala.math._

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

    def distNm(to: Place) = {
      var longdiff = to.longR - longR
      acos(sin(latR)*sin(to.latR)+
        cos(latR)*cos(to.latR)*cos(longdiff))*earthRadiusNM
    }

    val earthRadiusNM = 6371.0*0.539956804

    def PointOnWay(to: Place, distance: Double) : Place = {
      val bearing = initialBearing(to)
      val dR = distance / earthRadiusNM
      val latDstR = Math.asin( Math.sin(latR)*Math.cos(dR) +
        Math.cos(latR)*Math.sin(dR)*Math.cos(bearing) )
      var longDstR = longR + math.atan2(math.sin(bearing)*math.sin(dR)*Math.cos(latR),
        Math.cos(dR)-math.sin(latR)*math.sin(to.latR))
      Place("",latDstR.toDegrees,longDstR.toDegrees)
    }
}





// vim: set ts=4 sw=4 et:
