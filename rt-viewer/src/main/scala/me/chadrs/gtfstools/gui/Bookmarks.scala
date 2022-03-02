package me.chadrs.gtfstools.gui

import java.net.URI

case class Bookmark(name: String, uri: URI)

object Bookmark {
  def apply(name: String, uri: String) = new Bookmark(name, URI.create(uri))
}
object Bookmarks {
  val default = List(
    Bookmark(
      "Pullman Transit Vehicle Positions",
      "https://pullmanbusbeacon.com/gtfs-rt/vehiclepositions"
    ),
    Bookmark("Pullman Transit Trip Updates", "https://pullmanbusbeacon.com/gtfs-rt/tripupdates"),
    Bookmark("WMata", "https://api.wmata.com/gtfs/bus-gtfsrt-vehiclepositions.pb"),
    Bookmark("BART Trip Updates", "http://api.bart.gov/gtfsrt/tripupdate.aspx"),
    Bookmark("BART Alerts", "http://api.bart.gov/gtfsrt/alerts.aspx"),
    Bookmark("MBTA Vehicle Positions", "https://cdn.mbta.com/realtime/VehiclePositions.pb"),
    Bookmark("MBTA Trip Updates", "https://cdn.mbta.com/realtime/TripUpdates.pb"),
    Bookmark("MBTA Alerts", "https://cdn.mbta.com/realtime/Alerts.pb"),
    Bookmark("Capital Metro", "https://data.texas.gov/download/rmk2-acnw/application/octet-stream")
  )
}
