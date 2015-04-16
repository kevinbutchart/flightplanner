package utils

//case class MetarResult(station: String,
//                       time: String,
//                       windDir : Int,
//                       windSpeed: Int,
//                       vis : Int) {
//
//
//  addWeather(str : String)
//}

/**
 * Created by kevin on 10/04/15.
 */
object MetarParser {
  val TestMetar = "EGLL 110550Z 22012KT 9999 FEW020 SCT049 11/07 Q1012 NOSIG"

  var Clouds = List(
    "NCD",
    "SKC",
    "CLR",
    "NSC",
    "FEW",
    "SCT",
    "BKN",
    "OVC")
//
//
//  var WEATHER = {
//    // Intensity
//    "-": "light intensity",
//    "+": "heavy intensity",
//    VC: "in the vicinity",
//
//    // Descriptor
//    MI: "shallow",
//    PR: "partial",
//    BC: "patches",
//    DR: "low drifting",
//    BL: "blowing",
//    SH: "showers",
//    TS: "thunderstorm",
//    FZ: "freezing",
//
//    // Precipitation
//    RA: "rain",
//    DZ: "drizzle",
//    SN: "snow",
//    SG: "snow grains",
//    IC: "ice crystals",
//    PL: "ice pellets",
//    GR: "hail",
//    GS: "small hail",
//    UP: "unknown precipitation",
//
//    // Obscuration
//    FG: "fog",
//    VA: "volcanic ash",
//    BR: "mist",
//    HZ: "haze",
//    DU: "widespread dust",
//    FU: "smoke",
//    SA: "sand",
//    PY: "spray",
//
//    // Other
//    SQ: "squall",
//    PO: "dust or sand whirls",
//    DS: "duststorm",
//    SS: "sandstorm",
//    FC: "funnel cloud"
//  };



  def parseMetar(metarString : String) = {
    val metarArray = metarString.split(" ")

    val station = metarArray(0)
    val time = metarArray(1)
    val windDir = metarArray(2).substring(0,3)
    val windSpd = metarArray(2).substring(3,metarArray(2).length)
    val vis = metarArray(3)
    var index = 4
    var few = -1
    var sct = -1
    var bkn = -1
    var ovc = -1

    for (m <- metarArray) {
      if  (m.length>3 && (Clouds contains m.substring(0,3))) {
        var cld = m
        if (cld.startsWith("FEW")) {
          few = cld.substring(3,6).toInt
        }
        if (cld.startsWith("SCT")) {
          sct = cld.substring(3,6).toInt
        }
        if (cld.startsWith("BKN")) {
          bkn = cld.substring(3,6).toInt
        }
        if (cld.startsWith("OVC")) {
          ovc = cld.substring(3,6).toInt
        }
      }
    }
    (station, time, windDir, windSpd, vis, (few,sct,bkn,ovc))
  }
}
