package me.chadrs.gtfstools.graphql

import me.chadrs.gtfstools.graphql.Test.{GtfsContext, GtfsRepo}
import me.chadrs.gtfstools.types._
import sangria.schema._

object GeneratedSchemas {

  implicit lazy val agencySchema: ObjectType[GtfsContext, AgencyFileRow] = ObjectType(
    "agency",
    "",
    () =>
      fields[GtfsContext, AgencyFileRow](
        Field(
          "agency_id",
          OptionType(StringType),
          Some(
            "Identifies a transit brand which is often synonymous with a transit agency. Note that in some cases, such as when a single agency operates multiple separate services, agencies and brands are distinct. This document uses the term \"agency\" in place of \"brand\". A dataset may contain data from multiple agencies. This field is required when the dataset contains data for multiple transit agencies, otherwise it is optional."
          ),
          resolve = _.value.toMap.get("agency_id")
        ),
        Field(
          "agency_name",
          OptionType(StringType),
          Some("Full name of the transit agency."),
          resolve = _.value.toMap.get("agency_name")
        ),
        Field(
          "agency_url",
          OptionType(StringType),
          Some("URL of the transit agency."),
          resolve = _.value.toMap.get("agency_url")
        ),
        Field(
          "agency_timezone",
          OptionType(StringType),
          Some(
            "Timezone where the transit agency is located. If multiple agencies are specified in the dataset, each must have the same agency_timezone."
          ),
          resolve = _.value.toMap.get("agency_timezone")
        ),
        Field(
          "agency_lang",
          OptionType(StringType),
          Some(
            "Primary language used by this transit agency. This field helps GTFS consumers choose capitalization rules and other language-specific settings for the dataset."
          ),
          resolve = _.value.toMap.get("agency_lang")
        ),
        Field(
          "agency_phone",
          OptionType(StringType),
          Some(
            "A voice telephone number for the specified agency. This field is a string value that presents the telephone number as typical for the agency's service area. It can and should contain punctuation marks to group the digits of the number. Dialable text (for example, TriMet's \"503-238-RIDE\") is permitted, but the field must not contain any other descriptive text."
          ),
          resolve = _.value.toMap.get("agency_phone")
        ),
        Field(
          "agency_fare_url",
          OptionType(StringType),
          Some(
            "URL of a web page that allows a rider to purchase tickets or other fare instruments for that agency online."
          ),
          resolve = _.value.toMap.get("agency_fare_url")
        ),
        Field(
          "agency_email",
          OptionType(StringType),
          Some(
            "Email address actively monitored by the agency’s customer service department. This email address should be a direct contact point where transit riders can reach a customer service representative at the agency."
          ),
          resolve = _.value.toMap.get("agency_email")
        ),
        Field(
          "routes",
          ListType(routesSchema),
          Some("Rows from routes.txt where agency_id matches this object's agency_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[RoutesFileRow](
                "routes.txt",
                "agency_id",
                t.value.toMap.apply("agency_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "fare_attributes",
          ListType(fareAttributesSchema),
          Some("Rows from fare_attributes.txt where agency_id matches this object's agency_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[FareAttributesFileRow](
                "fare_attributes.txt",
                "agency_id",
                t.value.toMap.apply("agency_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "attributions",
          ListType(attributionsSchema),
          Some("Rows from attributions.txt where agency_id matches this object's agency_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[AttributionsFileRow](
                "attributions.txt",
                "agency_id",
                t.value.toMap.apply("agency_id")
              )
              .getOrElse(Nil)
        )
      )
  )
  implicit lazy val stopsSchema: ObjectType[GtfsContext, StopsFileRow] = ObjectType(
    "stops",
    "",
    () =>
      fields[GtfsContext, StopsFileRow](
        Field(
          "stop_id",
          OptionType(StringType),
          Some(
            """Identifies a stop, station, or station entrance.

The term "station entrance" refers to both station entrances and station exits. Stops, stations or station entrances are collectively referred to as locations. Multiple routes may use the same stop."""
          ),
          resolve = _.value.toMap.get("stop_id")
        ),
        Field(
          "stop_code",
          OptionType(StringType),
          Some(
            "Short text or a number that identifies the location for riders. These codes are often used in phone-based transit information systems or printed on signage to make it easier for riders to get information for a particular location. The stop_code can be the same as stop_id if it is public facing. This field should be left empty for locations without a code presented to riders."
          ),
          resolve = _.value.toMap.get("stop_code")
        ),
        Field(
          "stop_name",
          OptionType(StringType),
          Some(
            """Name of the location. Use a name that people will understand in the local and tourist vernacular.

When the location is a boarding area (location_type=4), the stop_name should contains the name of the boarding area as displayed by the agency. It could be just one letter (like on some European intercity railway stations), or text like “Wheelchair boarding area” (NYC’s Subway) or “Head of short trains” (Paris’ RER).

Conditionally Required:
• Required for locations which are stops (location_type=0), stations (location_type=1) or entrances/exits (location_type=2).
• Optional for locations which are generic nodes (location_type=3) or boarding areas (location_type=4)."""
          ),
          resolve = _.value.toMap.get("stop_name")
        ),
        Field(
          "stop_desc",
          OptionType(StringType),
          Some(
            "Description of the location that provides useful, quality information. Do not simply duplicate the name of the location."
          ),
          resolve = _.value.toMap.get("stop_desc")
        ),
        Field(
          "stop_lat",
          OptionType(StringType),
          Some(
            """Latitude of the location.

Conditionally Required:
• Required for locations which are stops (location_type=0), stations (location_type=1) or entrances/exits (location_type=2).
• Optional for locations which are generic nodes (location_type=3) or boarding areas (location_type=4)."""
          ),
          resolve = _.value.toMap.get("stop_lat")
        ),
        Field(
          "stop_lon",
          OptionType(StringType),
          Some(
            """Longitude of the location.

Conditionally Required:
• Required for locations which are stops (location_type=0), stations (location_type=1) or entrances/exits (location_type=2).
• Optional for locations which are generic nodes (location_type=3) or boarding areas (location_type=4)."""
          ),
          resolve = _.value.toMap.get("stop_lon")
        ),
        Field(
          "zone_id",
          OptionType(StringType),
          Some(
            "Identifies the fare zone for a stop. This field is required if providing fare information using fare_rules.txt, otherwise it is optional. If this record represents a station or station entrance, the zone_id is ignored."
          ),
          resolve = _.value.toMap.get("zone_id")
        ),
        Field(
          "stop_url",
          OptionType(StringType),
          Some(
            "URL of a web page about the location. This should be different from the agency.agency_url and the routes.route_url field values."
          ),
          resolve = _.value.toMap.get("stop_url")
        ),
        Field(
          "location_type",
          OptionType(StringType),
          Some(
            """Type of the location:
• 0 (or blank): Stop (or Platform). A location where passengers board or disembark from a transit vehicle. Is called a platform when defined within a parent_station.
• 1: Station. A physical structure or area that contains one or more platform.
• 2: Entrance/Exit. A location where passengers can enter or exit a station from the street. If an entrance/exit belongs to multiple stations, it can be linked by pathways to both, but the data provider must pick one of them as parent.
• 3: Generic Node. A location within a station, not matching any other location_type, which can be used to link together pathways define in pathways.txt.
• 4: Boarding Area. A specific location on a platform, where passengers can board and/or alight vehicles."""
          ),
          resolve = _.value.toMap.get("location_type")
        ),
        Field(
          "parent_station",
          OptionType(StringType),
          Some("""Defines hierarchy between the different locations defined in stops.txt. It contains the ID of the parent location, as followed:
• Stop/platform (location_type=0): the parent_station field contains the ID of a station.
• Station (location_type=1): this field must be empty.
• Entrance/exit (location_type=2) or generic node (location_type=3): the parent_station field contains the ID of a station (location_type=1)
• Boarding Area (location_type=4): the parent_station field contains ID of a platform.

Conditionally Required:
• Required for locations which are entrances (location_type=2), generic nodes (location_type=3) or boarding areas (location_type=4).
• Optional for stops/platforms (location_type=0).
• Forbidden for stations (location_type=1)."""),
          resolve = _.value.toMap.get("parent_station")
        ),
        Field(
          "stop_timezone",
          OptionType(StringType),
          Some(
            "Timezone of the location. If the location has a parent station, it inherits the parent station’s timezone instead of applying its own. Stations and parentless stops with empty stop_timezone inherit the timezone specified by agency.agency_timezone. If stop_timezone values are provided, the times in stop_times.txt should be entered as the time since midnight in the timezone specified by agency.agency_timezone. This ensures that the time values in a trip always increase over the course of a trip, regardless of which timezones the trip crosses."
          ),
          resolve = _.value.toMap.get("stop_timezone")
        ),
        Field(
          "wheelchair_boarding",
          OptionType(StringType),
          Some("""Indicates whether wheelchair boardings are possible from the location. Valid options are:

For parentless stops:
0 or empty - No accessibility information for the stop.
1 - Some vehicles at this stop can be boarded by a rider in a wheelchair.
2 - Wheelchair boarding is not possible at this stop.

For child stops:
0 or empty - Stop will inherit its wheelchair_boarding behavior from the parent station, if specified in the parent.
1 - There exists some accessible path from outside the station to the specific stop/platform.
2 - There exists no accessible path from outside the station to the specific stop/platform.

For station entrances/exits:
0 or empty - Station entrance will inherit its wheelchair_boarding behavior from the parent station, if specified for the parent.
1 - Station entrance is wheelchair accessible.
2 - No accessible path from station entrance to stops/platforms."""),
          resolve = _.value.toMap.get("wheelchair_boarding")
        ),
        Field(
          "level_id",
          OptionType(StringType),
          Some("Level of the location. The same level can be used by multiple unlinked stations."),
          resolve = _.value.toMap.get("level_id")
        ),
        Field(
          "platform_code",
          OptionType(StringType),
          Some(
            "Platform identifier for a platform stop (a stop belonging to a station). This should be just the platform identifier (eg. \"G\" or \"3\"). Words like “platform” or \"track\" (or the feed’s language-specific equivalent) should not be included. This allows feed consumers to more easily internationalize and localize the platform identifier into other languages."
          ),
          resolve = _.value.toMap.get("platform_code")
        ),
        Field(
          "parent_station_stop",
          OptionType(stopsSchema),
          Some("""Defines hierarchy between the different locations defined in stops.txt. It contains the ID of the parent location, as followed:
• Stop/platform (location_type=0): the parent_station field contains the ID of a station.
• Station (location_type=1): this field must be empty.
• Entrance/exit (location_type=2) or generic node (location_type=3): the parent_station field contains the ID of a station (location_type=1)
• Boarding Area (location_type=4): the parent_station field contains ID of a platform.

Conditionally Required:
• Required for locations which are entrances (location_type=2), generic nodes (location_type=3) or boarding areas (location_type=4).
• Optional for stops/platforms (location_type=0).
• Forbidden for stations (location_type=1)."""),
          resolve = t =>
            t.ctx
              .filterFileBy[StopsFileRow](
                "stops.txt",
                "stop_id",
                t.value.toMap.apply("parent_station")
              )
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "level",
          OptionType(levelsSchema),
          Some("Level of the location. The same level can be used by multiple unlinked stations."),
          resolve = t =>
            t.ctx
              .filterFileBy[LevelsFileRow](
                "levels.txt",
                "level_id",
                t.value.toMap.apply("level_id")
              )
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "stop_times",
          ListType(stopTimesSchema),
          Some("Rows from stop_times.txt where stop_id matches this object's stop_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[StopTimesFileRow](
                "stop_times.txt",
                "stop_id",
                t.value.toMap.apply("stop_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "origin_fare_rules",
          ListType(fareRulesSchema),
          Some("Rows from fare_rules.txt where origin_id matches this object's zone_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[FareRulesFileRow](
                "fare_rules.txt",
                "origin_id",
                t.value.toMap.apply("zone_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "destination_fare_rules",
          ListType(fareRulesSchema),
          Some("Rows from fare_rules.txt where destination_id matches this object's zone_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[FareRulesFileRow](
                "fare_rules.txt",
                "destination_id",
                t.value.toMap.apply("zone_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "contains_fare_rules",
          ListType(fareRulesSchema),
          Some("Rows from fare_rules.txt where contains_id matches this object's zone_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[FareRulesFileRow](
                "fare_rules.txt",
                "contains_id",
                t.value.toMap.apply("zone_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "from_stop_transfers",
          ListType(transfersSchema),
          Some("Rows from transfers.txt where from_stop_id matches this object's stop_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[TransfersFileRow](
                "transfers.txt",
                "from_stop_id",
                t.value.toMap.apply("stop_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "to_stop_transfers",
          ListType(transfersSchema),
          Some("Rows from transfers.txt where to_stop_id matches this object's stop_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[TransfersFileRow](
                "transfers.txt",
                "to_stop_id",
                t.value.toMap.apply("stop_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "from_stop_pathways",
          ListType(pathwaysSchema),
          Some("Rows from pathways.txt where from_stop_id matches this object's stop_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[PathwaysFileRow](
                "pathways.txt",
                "from_stop_id",
                t.value.toMap.apply("stop_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "to_stop_pathways",
          ListType(pathwaysSchema),
          Some("Rows from pathways.txt where to_stop_id matches this object's stop_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[PathwaysFileRow](
                "pathways.txt",
                "to_stop_id",
                t.value.toMap.apply("stop_id")
              )
              .getOrElse(Nil)
        )
      )
  )
  implicit lazy val routesSchema: ObjectType[GtfsContext, RoutesFileRow] = ObjectType(
    "routes",
    "",
    () =>
      fields[GtfsContext, RoutesFileRow](
        Field(
          "route_id",
          OptionType(StringType),
          Some("Identifies a route."),
          resolve = _.value.toMap.get("route_id")
        ),
        Field(
          "agency_id",
          OptionType(StringType),
          Some(
            "Agency for the specified route. This field is required when the dataset provides data for routes from more than one agency in agency.txt, otherwise it is optional."
          ),
          resolve = _.value.toMap.get("agency_id")
        ),
        Field(
          "route_short_name",
          OptionType(StringType),
          Some(
            "Short name of a route. This will often be a short, abstract identifier like \"32\", \"100X\", or \"Green\" that riders use to identify a route, but which doesn't give any indication of what places the route serves. Either route_short_name or route_long_name must be specified, or potentially both if appropriate."
          ),
          resolve = _.value.toMap.get("route_short_name")
        ),
        Field(
          "route_long_name",
          OptionType(StringType),
          Some(
            "Full name of a route. This name is generally more descriptive than the route_short_name and often includes the route's destination or stop. Either route_short_name or route_long_name must be specified, or potentially both if appropriate."
          ),
          resolve = _.value.toMap.get("route_long_name")
        ),
        Field(
          "route_desc",
          OptionType(StringType),
          Some(
            "Description of a route that provides useful, quality information. Do not simply duplicate the name of the route.Example: \"A\" trains operate between Inwood-207 St, Manhattan and Far Rockaway-Mott Avenue, Queens at all times. Also from about 6AM until about midnight, additional \"A\" trains operate between Inwood-207 St and Lefferts Boulevard (trains typically alternate between Lefferts Blvd and Far Rockaway)."
          ),
          resolve = _.value.toMap.get("route_desc")
        ),
        Field(
          "route_type",
          OptionType(StringType),
          Some("""Indicates the type of transportation used on a route. Valid options are:

0 - Tram, Streetcar, Light rail. Any light rail or street level system within a metropolitan area.
1 - Subway, Metro. Any underground rail system within a metropolitan area.
2 - Rail. Used for intercity or long-distance travel.
3 - Bus. Used for short- and long-distance bus routes.
4 - Ferry. Used for short- and long-distance boat service.
5 - Cable tram. Used for street-level rail cars where the cable runs beneath the vehicle, e.g., cable car in San Francisco.
6 - Aerial lift, suspended cable car (e.g., gondola lift, aerial tramway). Cable transport where cabins, cars, gondolas or open chairs are suspended by means of one or more cables.
7 - Funicular. Any rail system designed for steep inclines.
11 - Trolleybus. Electric buses that draw power from overhead wires using poles.
12 - Monorail. Railway in which the track consists of a single rail or a beam."""),
          resolve = _.value.toMap.get("route_type")
        ),
        Field(
          "route_url",
          OptionType(StringType),
          Some(
            "URL of a web page about the particular route. Should be different from the agency.agency_url value."
          ),
          resolve = _.value.toMap.get("route_url")
        ),
        Field(
          "route_color",
          OptionType(StringType),
          Some(
            "Route color designation that matches public facing material. Defaults to white (FFFFFF) when omitted or left empty. The color difference between route_color and route_text_color should provide sufficient contrast when viewed on a black and white screen."
          ),
          resolve = _.value.toMap.get("route_color")
        ),
        Field(
          "route_text_color",
          OptionType(StringType),
          Some(
            "Legible color to use for text drawn against a background of route_color. Defaults to black (000000) when omitted or left empty. The color difference between route_color and route_text_color should provide sufficient contrast when viewed on a black and white screen."
          ),
          resolve = _.value.toMap.get("route_text_color")
        ),
        Field(
          "route_sort_order",
          OptionType(StringType),
          Some(
            "Orders the routes in a way which is ideal for presentation to customers. Routes with smaller route_sort_order values should be displayed first."
          ),
          resolve = _.value.toMap.get("route_sort_order")
        ),
        Field(
          "agency",
          OptionType(agencySchema),
          Some(
            "Agency for the specified route. This field is required when the dataset provides data for routes from more than one agency in agency.txt, otherwise it is optional."
          ),
          resolve = t =>
            t.ctx
              .filterFileBy[AgencyFileRow](
                "agency.txt",
                "agency_id",
                t.value.toMap.apply("agency_id")
              )
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "trips",
          ListType(tripsSchema),
          Some("Rows from trips.txt where route_id matches this object's route_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[TripsFileRow]("trips.txt", "route_id", t.value.toMap.apply("route_id"))
              .getOrElse(Nil)
        ),
        Field(
          "fare_rules",
          ListType(fareRulesSchema),
          Some("Rows from fare_rules.txt where route_id matches this object's route_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[FareRulesFileRow](
                "fare_rules.txt",
                "route_id",
                t.value.toMap.apply("route_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "attributions",
          ListType(attributionsSchema),
          Some("Rows from attributions.txt where route_id matches this object's route_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[AttributionsFileRow](
                "attributions.txt",
                "route_id",
                t.value.toMap.apply("route_id")
              )
              .getOrElse(Nil)
        )
      )
  )
  implicit lazy val tripsSchema: ObjectType[GtfsContext, TripsFileRow] = ObjectType(
    "trips",
    "",
    () =>
      fields[GtfsContext, TripsFileRow](
        Field(
          "route_id",
          OptionType(StringType),
          Some("Identifies a route."),
          resolve = _.value.toMap.get("route_id")
        ),
        Field(
          "service_id",
          OptionType(StringType),
          Some("Identifies a set of dates when service is available for one or more routes."),
          resolve = _.value.toMap.get("service_id")
        ),
        Field(
          "trip_id",
          OptionType(StringType),
          Some("Identifies a trip."),
          resolve = _.value.toMap.get("trip_id")
        ),
        Field(
          "trip_headsign",
          OptionType(StringType),
          Some(
            "Text that appears on signage identifying the trip's destination to riders. Use this field to distinguish between different patterns of service on the same route. If the headsign changes during a trip, trip_headsign can be overridden by specifying values for the stop_times.stop_headsign."
          ),
          resolve = _.value.toMap.get("trip_headsign")
        ),
        Field(
          "trip_short_name",
          OptionType(StringType),
          Some(
            "Public facing text used to identify the trip to riders, for instance, to identify train numbers for commuter rail trips. If riders do not commonly rely on trip names, leave this field empty. A trip_short_name value, if provided, should uniquely identify a trip within a service day; it should not be used for destination names or limited/express designations."
          ),
          resolve = _.value.toMap.get("trip_short_name")
        ),
        Field(
          "direction_id",
          OptionType(StringType),
          Some("""Indicates the direction of travel for a trip. This field is not used in routing; it provides a way to separate trips by direction when publishing time tables. Valid options are:

0 - Travel in one direction (e.g. outbound travel).
1 - Travel in the opposite direction (e.g. inbound travel).Example: The trip_headsign and direction_id fields could be used together to assign a name to travel in each direction for a set of trips. A trips.txt file could contain these records for use in time tables:
trip_id,...,trip_headsign,direction_id
1234,...,Airport,0
1505,...,Downtown,1"""),
          resolve = _.value.toMap.get("direction_id")
        ),
        Field(
          "block_id",
          OptionType(StringType),
          Some(
            "Identifies the block to which the trip belongs. A block consists of a single trip or many sequential trips made using the same vehicle, defined by shared service days and block_id. A block_id can have trips with different service days, making distinct blocks. See the example below"
          ),
          resolve = _.value.toMap.get("block_id")
        ),
        Field(
          "shape_id",
          OptionType(StringType),
          Some("Identifies a geospatial shape describing the vehicle travel path for a trip."),
          resolve = _.value.toMap.get("shape_id")
        ),
        Field(
          "wheelchair_accessible",
          OptionType(StringType),
          Some("""Indicates wheelchair accessibility. Valid options are:

0 or empty - No accessibility information for the trip.
1 - Vehicle being used on this particular trip can accommodate at least one rider in a wheelchair.
2 - No riders in wheelchairs can be accommodated on this trip."""),
          resolve = _.value.toMap.get("wheelchair_accessible")
        ),
        Field(
          "bikes_allowed",
          OptionType(StringType),
          Some("""Indicates whether bikes are allowed. Valid options are:

0 or empty - No bike information for the trip.
1 - Vehicle being used on this particular trip can accommodate at least one bicycle.
2 - No bicycles are allowed on this trip."""),
          resolve = _.value.toMap.get("bikes_allowed")
        ),
        Field(
          "route",
          OptionType(routesSchema),
          Some("Identifies a route."),
          resolve = t =>
            t.ctx
              .filterFileBy[RoutesFileRow](
                "routes.txt",
                "route_id",
                t.value.toMap.apply("route_id")
              )
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "shape",
          ListType(shapesSchema),
          Some("Identifies a geospatial shape describing the vehicle travel path for a trip."),
          resolve = t =>
            t.ctx
              .filterFileBy[ShapesFileRow](
                "shapes.txt",
                "shape_id",
                t.value.toMap.apply("shape_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "stop_times",
          ListType(stopTimesSchema),
          Some("Rows from stop_times.txt where trip_id matches this object's trip_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[StopTimesFileRow](
                "stop_times.txt",
                "trip_id",
                t.value.toMap.apply("trip_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "frequencies",
          ListType(frequenciesSchema),
          Some("Rows from frequencies.txt where trip_id matches this object's trip_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[FrequenciesFileRow](
                "frequencies.txt",
                "trip_id",
                t.value.toMap.apply("trip_id")
              )
              .getOrElse(Nil)
        ),
        Field(
          "attributions",
          ListType(attributionsSchema),
          Some("Rows from attributions.txt where trip_id matches this object's trip_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[AttributionsFileRow](
                "attributions.txt",
                "trip_id",
                t.value.toMap.apply("trip_id")
              )
              .getOrElse(Nil)
        )
      )
  )
  implicit lazy val stopTimesSchema: ObjectType[GtfsContext, StopTimesFileRow] = ObjectType(
    "stopTimes",
    "",
    () =>
      fields[GtfsContext, StopTimesFileRow](
        Field(
          "trip_id",
          OptionType(StringType),
          Some("Identifies a trip."),
          resolve = _.value.toMap.get("trip_id")
        ),
        Field(
          "arrival_time",
          OptionType(StringType),
          Some(
            """Arrival time at a specific stop for a specific trip on a route. If there are not separate times for arrival and departure at a stop, enter the same value for arrival_time and departure_time. For times occurring after midnight on the service day, enter the time as a value greater than 24:00:00 in HH:MM:SS local time for the day on which the trip schedule begins.

Scheduled stops where the vehicle strictly adheres to the specified arrival and departure times are timepoints. If this stop is not a timepoint, it is recommended to provide an estimated or interpolated time. If this is not available, arrival_time can be left empty. Further, indicate that interpolated times are provided with timepoint=0. If interpolated times are indicated with timepoint=0, then time points must be indicated with timepoint=1. Provide arrival times for all stops that are time points. An arrival time must be specified for the first and the last stop in a trip."""
          ),
          resolve = _.value.toMap.get("arrival_time")
        ),
        Field(
          "departure_time",
          OptionType(StringType),
          Some(
            """Departure time from a specific stop for a specific trip on a route. For times occurring after midnight on the service day, enter the time as a value greater than 24:00:00 in HH:MM:SS local time for the day on which the trip schedule begins. If there are not separate times for arrival and departure at a stop, enter the same value for arrival_time and departure_time. See the arrival_time description for more details about using timepoints correctly.

The departure_time field should specify time values whenever possible, including non-binding estimated or interpolated times between timepoints."""
          ),
          resolve = _.value.toMap.get("departure_time")
        ),
        Field(
          "stop_id",
          OptionType(StringType),
          Some(
            "Identifies the serviced stop. All stops serviced during a trip must have a record in stop_times.txt. Referenced locations must be stops, not stations or station entrances. A stop may be serviced multiple times in the same trip, and multiple trips and routes may service the same stop."
          ),
          resolve = _.value.toMap.get("stop_id")
        ),
        Field(
          "stop_sequence",
          OptionType(StringType),
          Some(
            "Order of stops for a particular trip. The values must increase along the trip but do not need to be consecutive.Example: The first location on the trip could have a stop_sequence=1, the second location on the trip could have a stop_sequence=23, the third location could have a stop_sequence=40, and so on."
          ),
          resolve = _.value.toMap.get("stop_sequence")
        ),
        Field(
          "stop_headsign",
          OptionType(StringType),
          Some(
            """Text that appears on signage identifying the trip's destination to riders. This field overrides the default trips.trip_headsign when the headsign changes between stops. If the headsign is displayed for an entire trip, use trips.trip_headsign instead.

A stop_headsign value specified for one stop_time does not apply to subsequent stop_times in the same trip. If you want to override the trip_headsign for multiple stop_times in the same trip, the stop_headsign value must be repeated in each stop_time row."""
          ),
          resolve = _.value.toMap.get("stop_headsign")
        ),
        Field(
          "pickup_type",
          OptionType(StringType),
          Some("""Indicates pickup method. Valid options are:

0 or empty - Regularly scheduled pickup.
1 - No pickup available.
2 - Must phone agency to arrange pickup.
3 - Must coordinate with driver to arrange pickup."""),
          resolve = _.value.toMap.get("pickup_type")
        ),
        Field(
          "drop_off_type",
          OptionType(StringType),
          Some("""Indicates drop off method. Valid options are:

0 or empty - Regularly scheduled drop off.
1 - No drop off available.
2 - Must phone agency to arrange drop off.
3 - Must coordinate with driver to arrange drop off."""),
          resolve = _.value.toMap.get("drop_off_type")
        ),
        Field(
          "shape_dist_traveled",
          OptionType(StringType),
          Some(
            "Actual distance traveled along the associated shape, from the first stop to the stop specified in this record. This field specifies how much of the shape to draw between any two stops during a trip. Must be in the same units used in shapes.txt. Values used for shape_dist_traveled must increase along with stop_sequence; they cannot be used to show reverse travel along a route.Example: If a bus travels a distance of 5.25 kilometers from the start of the shape to the stop,shape_dist_traveled=5.25."
          ),
          resolve = _.value.toMap.get("shape_dist_traveled")
        ),
        Field(
          "timepoint",
          OptionType(StringType),
          Some("""Indicates if arrival and departure times for a stop are strictly adhered to by the vehicle or if they are instead approximate and/or interpolated times. This field allows a GTFS producer to provide interpolated stop-times, while indicating that the times are approximate. Valid options are:

0 - Times are considered approximate.
1 or empty - Times are considered exact."""),
          resolve = _.value.toMap.get("timepoint")
        ),
        Field(
          "trip",
          OptionType(tripsSchema),
          Some("Identifies a trip."),
          resolve = t =>
            t.ctx
              .filterFileBy[TripsFileRow]("trips.txt", "trip_id", t.value.toMap.apply("trip_id"))
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "stop",
          OptionType(stopsSchema),
          Some(
            "Identifies the serviced stop. All stops serviced during a trip must have a record in stop_times.txt. Referenced locations must be stops, not stations or station entrances. A stop may be serviced multiple times in the same trip, and multiple trips and routes may service the same stop."
          ),
          resolve = t =>
            t.ctx
              .filterFileBy[StopsFileRow]("stops.txt", "stop_id", t.value.toMap.apply("stop_id"))
              .getOrElse(Nil)
              .headOption
        )
      )
  )
  implicit lazy val calendarSchema: ObjectType[GtfsContext, CalendarFileRow] = ObjectType(
    "calendar",
    "",
    () =>
      fields[GtfsContext, CalendarFileRow](
        Field(
          "service_id",
          OptionType(StringType),
          Some(
            "Uniquely identifies a set of dates when service is available for one or more routes. Each service_id value can appear at most once in a calendar.txt file."
          ),
          resolve = _.value.toMap.get("service_id")
        ),
        Field(
          "monday",
          OptionType(StringType),
          Some("""Indicates whether the service operates on all Mondays in the date range specified by the start_date and end_date fields. Note that exceptions for particular dates may be listed in calendar_dates.txt. Valid options are:

1 - Service is available for all Mondays in the date range.
0 - Service is not available for Mondays in the date range."""),
          resolve = _.value.toMap.get("monday")
        ),
        Field(
          "tuesday",
          OptionType(StringType),
          Some("Functions in the same way as monday except applies to Tuesdays"),
          resolve = _.value.toMap.get("tuesday")
        ),
        Field(
          "wednesday",
          OptionType(StringType),
          Some("Functions in the same way as monday except applies to Wednesdays"),
          resolve = _.value.toMap.get("wednesday")
        ),
        Field(
          "thursday",
          OptionType(StringType),
          Some("Functions in the same way as monday except applies to Thursdays"),
          resolve = _.value.toMap.get("thursday")
        ),
        Field(
          "friday",
          OptionType(StringType),
          Some("Functions in the same way as monday except applies to Fridays"),
          resolve = _.value.toMap.get("friday")
        ),
        Field(
          "saturday",
          OptionType(StringType),
          Some("Functions in the same way as monday except applies to Saturdays."),
          resolve = _.value.toMap.get("saturday")
        ),
        Field(
          "sunday",
          OptionType(StringType),
          Some("Functions in the same way as monday except applies to Sundays."),
          resolve = _.value.toMap.get("sunday")
        ),
        Field(
          "start_date",
          OptionType(StringType),
          Some("Start service day for the service interval."),
          resolve = _.value.toMap.get("start_date")
        ),
        Field(
          "end_date",
          OptionType(StringType),
          Some(
            "End service day for the service interval. This service day is included in the interval."
          ),
          resolve = _.value.toMap.get("end_date")
        )
      )
  )
  implicit lazy val calendarDatesSchema: ObjectType[GtfsContext, CalendarDatesFileRow] = ObjectType(
    "calendarDates",
    """
The calendar_dates.txt table can explicitly activate or disable service by date. It can be used in two ways.

Recommended: Use calendar_dates.txt in conjunction with calendar.txt to define exceptions to the default service patterns defined in calendar.txt. If service is generally regular, with a few changes on explicit dates (for instance, to accommodate special event services, or a school schedule), this is a good approach. In this case calendar_dates.service_id is an ID referencing calendar.service_id.
Alternate: Omit calendar.txt, and specify each date of service in calendar_dates.txt. This allows for considerable service variation and accommodates service without normal weekly schedules. In this case service_id is an ID.""",
    () =>
      fields[GtfsContext, CalendarDatesFileRow](
        Field(
          "service_id",
          OptionType(StringType),
          Some(
            "Identifies a set of dates when a service exception occurs for one or more routes. Each (service_id, date) pair can only appear once in calendar_dates.txt if using calendar.txt and calendar_dates.txt in conjunction. If a service_id value appears in both calendar.txt and calendar_dates.txt, the information in calendar_dates.txt modifies the service information specified in calendar.txt."
          ),
          resolve = _.value.toMap.get("service_id")
        ),
        Field(
          "date",
          OptionType(StringType),
          Some("Date when service exception occurs."),
          resolve = _.value.toMap.get("date")
        ),
        Field(
          "exception_type",
          OptionType(StringType),
          Some(
            """Indicates whether service is available on the date specified in the date field. Valid options are:

1 - Service has been added for the specified date.
2 - Service has been removed for the specified date.Example: Suppose a route has one set of trips available on holidays and another set of trips available on all other days. One service_id could correspond to the regular service schedule and another service_id could correspond to the holiday schedule. For a particular holiday, the calendar_dates.txt file could be used to add the holiday to the holiday service_id and to remove the holiday from the regular service_id schedule."""
          ),
          resolve = _.value.toMap.get("exception_type")
        )
      )
  )
  implicit lazy val fareAttributesSchema: ObjectType[GtfsContext, FareAttributesFileRow] =
    ObjectType(
      "fareAttributes",
      "",
      () =>
        fields[GtfsContext, FareAttributesFileRow](
          Field(
            "fare_id",
            OptionType(StringType),
            Some("Identifies a fare class."),
            resolve = _.value.toMap.get("fare_id")
          ),
          Field(
            "price",
            OptionType(StringType),
            Some("Fare price, in the unit specified by currency_type."),
            resolve = _.value.toMap.get("price")
          ),
          Field(
            "currency_type",
            OptionType(StringType),
            Some("Currency used to pay the fare."),
            resolve = _.value.toMap.get("currency_type")
          ),
          Field(
            "payment_method",
            OptionType(StringType),
            Some("""Indicates when the fare must be paid. Valid options are:

0 - Fare is paid on board.
1 - Fare must be paid before boarding."""),
            resolve = _.value.toMap.get("payment_method")
          ),
          Field(
            "transfers",
            OptionType(StringType),
            Some("""Indicates the number of transfers permitted on this fare. The fact that this field can be left empty is an exception to the requirement that a Required field must not be empty. Valid options are:

0 - No transfers permitted on this fare.
1 - Riders may transfer once.
2 - Riders may transfer twice.
empty - Unlimited transfers are permitted."""),
            resolve = _.value.toMap.get("transfers")
          ),
          Field(
            "agency_id",
            OptionType(StringType),
            Some(
              "Identifies the relevant agency for a fare. This field is required for datasets with multiple agencies defined in agency.txt, otherwise it is optional."
            ),
            resolve = _.value.toMap.get("agency_id")
          ),
          Field(
            "transfer_duration",
            OptionType(StringType),
            Some(
              "Length of time in seconds before a transfer expires. When transfers=0 this field can be used to indicate how long a ticket is valid for or it can can be left empty."
            ),
            resolve = _.value.toMap.get("transfer_duration")
          ),
          Field(
            "agency",
            OptionType(agencySchema),
            Some(
              "Identifies the relevant agency for a fare. This field is required for datasets with multiple agencies defined in agency.txt, otherwise it is optional."
            ),
            resolve = t =>
              t.ctx
                .filterFileBy[AgencyFileRow](
                  "agency.txt",
                  "agency_id",
                  t.value.toMap.apply("agency_id")
                )
                .getOrElse(Nil)
                .headOption
          ),
          Field(
            "fare_rules",
            ListType(fareRulesSchema),
            Some("Rows from fare_rules.txt where fare_id matches this object's fare_id."),
            resolve = t =>
              t.ctx
                .filterFileBy[FareRulesFileRow](
                  "fare_rules.txt",
                  "fare_id",
                  t.value.toMap.apply("fare_id")
                )
                .getOrElse(Nil)
          )
        )
    )
  implicit lazy val fareRulesSchema: ObjectType[GtfsContext, FareRulesFileRow] = ObjectType(
    "fareRules",
    """
The fare_rules.txt table specifies how fares in fare_attributes.txt apply to an itinerary. Most fare structures use some combination of the following rules:

Fare depends on origin or destination stations.
Fare depends on which zones the itinerary passes through.
Fare depends on which route the itinerary uses.

For examples that demonstrate how to specify a fare structure with fare_rules.txt and fare_attributes.txt, see https://code.google.com/p/googletransitdatafeed/wiki/FareExamples in the GoogleTransitDataFeed open source project wiki.
""",
    () =>
      fields[GtfsContext, FareRulesFileRow](
        Field(
          "fare_id",
          OptionType(StringType),
          Some("Identifies a fare class."),
          resolve = _.value.toMap.get("fare_id")
        ),
        Field(
          "route_id",
          OptionType(StringType),
          Some("""Identifies a route associated with the fare class. If several routes with the same fare attributes exist, create a record in fare_rules.txt for each route.Example: If fare class "b" is valid on route "TSW" and "TSE", the fare_rules.txt file would contain these records for the fare class:
fare_id,route_id
b,TSW
b,TSE"""),
          resolve = _.value.toMap.get("route_id")
        ),
        Field(
          "origin_id",
          OptionType(StringType),
          Some("""Identifies an origin zone. If a fare class has multiple origin zones, create a record in fare_rules.txt for each origin_id.Example: If fare class "b" is valid for all travel originating from either zone "2" or zone "8", the fare_rules.txt file would contain these records for the fare class:
fare_id,...,origin_id
b,...,2
b,...,8"""),
          resolve = _.value.toMap.get("origin_id")
        ),
        Field(
          "destination_id",
          OptionType(StringType),
          Some("""Identifies a destination zone. If a fare class has multiple destination zones, create a record in fare_rules.txt for each destination_id.Example: The origin_id and destination_id fields could be used together to specify that fare class "b" is valid for travel between zones 3 and 4, and for travel between zones 3 and 5, the fare_rules.txt file would contain these records for the fare class:
fare_id,...,origin_id,destination_id
b,...,3,4
b,...,3,5"""),
          resolve = _.value.toMap.get("destination_id")
        ),
        Field(
          "contains_id",
          OptionType(StringType),
          Some(
            """Identifies the zones that a rider will enter while using a given fare class. Used in some systems to calculate correct fare class.Example: If fare class "c" is associated with all travel on the GRT route that passes through zones 5, 6, and 7 the fare_rules.txt would contain these records:
fare_id,route_id,...,contains_id
c,GRT,...,5
c,GRT,...,6
c,GRT,...,7
Because all contains_id zones must be matched for the fare to apply, an itinerary that passes through zones 5 and 6 but not zone 7 would not have fare class "c". For more detail, see https://code.google.com/p/googletransitdatafeed/wiki/FareExamples in the GoogleTransitDataFeed project wiki."""
          ),
          resolve = _.value.toMap.get("contains_id")
        ),
        Field(
          "fare",
          OptionType(fareAttributesSchema),
          Some("Identifies a fare class."),
          resolve = t =>
            t.ctx
              .filterFileBy[FareAttributesFileRow](
                "fare_attributes.txt",
                "fare_id",
                t.value.toMap.apply("fare_id")
              )
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "route",
          OptionType(routesSchema),
          Some("""Identifies a route associated with the fare class. If several routes with the same fare attributes exist, create a record in fare_rules.txt for each route.Example: If fare class "b" is valid on route "TSW" and "TSE", the fare_rules.txt file would contain these records for the fare class:
fare_id,route_id
b,TSW
b,TSE"""),
          resolve = t =>
            t.ctx
              .filterFileBy[RoutesFileRow](
                "routes.txt",
                "route_id",
                t.value.toMap.apply("route_id")
              )
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "origin",
          OptionType(stopsSchema),
          Some("""Identifies an origin zone. If a fare class has multiple origin zones, create a record in fare_rules.txt for each origin_id.Example: If fare class "b" is valid for all travel originating from either zone "2" or zone "8", the fare_rules.txt file would contain these records for the fare class:
fare_id,...,origin_id
b,...,2
b,...,8"""),
          resolve = t =>
            t.ctx
              .filterFileBy[StopsFileRow]("stops.txt", "zone_id", t.value.toMap.apply("origin_id"))
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "destination",
          OptionType(stopsSchema),
          Some("""Identifies a destination zone. If a fare class has multiple destination zones, create a record in fare_rules.txt for each destination_id.Example: The origin_id and destination_id fields could be used together to specify that fare class "b" is valid for travel between zones 3 and 4, and for travel between zones 3 and 5, the fare_rules.txt file would contain these records for the fare class:
fare_id,...,origin_id,destination_id
b,...,3,4
b,...,3,5"""),
          resolve = t =>
            t.ctx
              .filterFileBy[StopsFileRow](
                "stops.txt",
                "zone_id",
                t.value.toMap.apply("destination_id")
              )
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "contains",
          OptionType(stopsSchema),
          Some(
            """Identifies the zones that a rider will enter while using a given fare class. Used in some systems to calculate correct fare class.Example: If fare class "c" is associated with all travel on the GRT route that passes through zones 5, 6, and 7 the fare_rules.txt would contain these records:
fare_id,route_id,...,contains_id
c,GRT,...,5
c,GRT,...,6
c,GRT,...,7
Because all contains_id zones must be matched for the fare to apply, an itinerary that passes through zones 5 and 6 but not zone 7 would not have fare class "c". For more detail, see https://code.google.com/p/googletransitdatafeed/wiki/FareExamples in the GoogleTransitDataFeed project wiki."""
          ),
          resolve = t =>
            t.ctx
              .filterFileBy[StopsFileRow](
                "stops.txt",
                "zone_id",
                t.value.toMap.apply("contains_id")
              )
              .getOrElse(Nil)
              .headOption
        )
      )
  )
  implicit lazy val shapesSchema: ObjectType[GtfsContext, ShapesFileRow] = ObjectType(
    "shapes",
    """
Shapes describe the path that a vehicle travels along a route alignment, and are defined in the file shapes.txt. Shapes are associated with Trips, and consist of a sequence of points through which the vehicle passes in order. Shapes do not need to intercept the location of Stops exactly, but all Stops on a trip should lie within a small distance of the shape for that trip, i.e. close to straight line segments connecting the shape points.
""",
    () =>
      fields[GtfsContext, ShapesFileRow](
        Field(
          "shape_id",
          OptionType(StringType),
          Some("Identifies a shape."),
          resolve = _.value.toMap.get("shape_id")
        ),
        Field(
          "shape_pt_lat",
          OptionType(StringType),
          Some(
            "Latitude of a shape point. Each record in shapes.txt represents a shape point used to define the shape."
          ),
          resolve = _.value.toMap.get("shape_pt_lat")
        ),
        Field(
          "shape_pt_lon",
          OptionType(StringType),
          Some("Longitude of a shape point."),
          resolve = _.value.toMap.get("shape_pt_lon")
        ),
        Field(
          "shape_pt_sequence",
          OptionType(StringType),
          Some("""Sequence in which the shape points connect to form the shape. Values must increase along the trip but do not need to be consecutive.Example: If the shape "A_shp" has three points in its definition, the shapes.txt file might contain these records to define the shape:
shape_id,shape_pt_lat,shape_pt_lon,shape_pt_sequence
A_shp,37.61956,-122.48161,0
A_shp,37.64430,-122.41070,6
A_shp,37.65863,-122.30839,11"""),
          resolve = _.value.toMap.get("shape_pt_sequence")
        ),
        Field(
          "shape_dist_traveled",
          OptionType(StringType),
          Some("""Actual distance traveled along the shape from the first shape point to the point specified in this record. Used by trip planners to show the correct portion of the shape on a map. Values must increase along with shape_pt_sequence; they cannot be used to show reverse travel along a route. Distance units must be consistent with those used in stop_times.txt.Example: If a bus travels along the three points defined above for A_shp, the additional shape_dist_traveled values (shown here in kilometers) would look like this:
shape_id,shape_pt_lat,shape_pt_lon,shape_pt_sequence,shape_dist_traveled
A_shp,37.61956,-122.48161,0,0
A_shp,37.64430,-122.41070,6,6.8310
A_shp,37.65863,-122.30839,11,15.8765"""),
          resolve = _.value.toMap.get("shape_dist_traveled")
        ),
        Field(
          "trips",
          ListType(tripsSchema),
          Some("Rows from trips.txt where shape_id matches this object's shape_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[TripsFileRow]("trips.txt", "shape_id", t.value.toMap.apply("shape_id"))
              .getOrElse(Nil)
        )
      )
  )
  implicit lazy val frequenciesSchema: ObjectType[GtfsContext, FrequenciesFileRow] = ObjectType(
    "frequencies",
    """
Frequencies.txt represents trips that operate on regular headways (time between trips). This file can be used to represent two different types of service.

Frequency-based service (exact_times=0) in which service does not follow a fixed schedule throughout the day. Instead, operators attempt to strictly maintain predetermined headways for trips.
A compressed representation of schedule-based service (exact_times=1) that has the exact same headway for trips over specified time period(s). In schedule-based service operators try to strictly adhere to a schedule.""",
    () =>
      fields[GtfsContext, FrequenciesFileRow](
        Field(
          "trip_id",
          OptionType(StringType),
          Some("Identifies a trip to which the specified headway of service applies."),
          resolve = _.value.toMap.get("trip_id")
        ),
        Field(
          "start_time",
          OptionType(StringType),
          Some(
            "Time at which the first vehicle departs from the first stop of the trip with the specified headway."
          ),
          resolve = _.value.toMap.get("start_time")
        ),
        Field(
          "end_time",
          OptionType(StringType),
          Some(
            "Time at which service changes to a different headway (or ceases) at the first stop in the trip."
          ),
          resolve = _.value.toMap.get("end_time")
        ),
        Field(
          "headway_secs",
          OptionType(StringType),
          Some(
            "Time, in seconds, between departures from the same stop (headway) for the trip, during the time interval specified by start_time and end_time. Multiple headways for the same trip are allowed, but may not overlap. New headways may start at the exact time the previous headway ends."
          ),
          resolve = _.value.toMap.get("headway_secs")
        ),
        Field(
          "exact_times",
          OptionType(StringType),
          Some(
            """Indicates the type of service for a trip. See the file description for more information. Valid options are:

0 or empty - Frequency-based trips.
1 - Schedule-based trips with the exact same headway throughout the day. In this case the end_time value must be greater than the last desired trip start_time but less than the last desired trip start_time + headway_secs."""
          ),
          resolve = _.value.toMap.get("exact_times")
        ),
        Field(
          "trip",
          OptionType(tripsSchema),
          Some("Identifies a trip to which the specified headway of service applies."),
          resolve = t =>
            t.ctx
              .filterFileBy[TripsFileRow]("trips.txt", "trip_id", t.value.toMap.apply("trip_id"))
              .getOrElse(Nil)
              .headOption
        )
      )
  )
  implicit lazy val transfersSchema: ObjectType[GtfsContext, TransfersFileRow] = ObjectType(
    "transfers",
    """
When calculating an itinerary, GTFS-consuming applications interpolate transfers based on allowable time and stop proximity. Transfers.txt specifies additional rules and overrides for selected transfers.
""",
    () =>
      fields[GtfsContext, TransfersFileRow](
        Field(
          "from_stop_id",
          OptionType(StringType),
          Some(
            "Identifies a stop or station where a connection between routes begins. If this field refers to a station, the transfer rule applies to all its child stops."
          ),
          resolve = _.value.toMap.get("from_stop_id")
        ),
        Field(
          "to_stop_id",
          OptionType(StringType),
          Some(
            "Identifies a stop or station where a connection between routes ends. If this field refers to a station, the transfer rule applies to all child stops."
          ),
          resolve = _.value.toMap.get("to_stop_id")
        ),
        Field(
          "transfer_type",
          OptionType(StringType),
          Some("""Indicates the type of connection for the specified (from_stop_id, to_stop_id) pair. Valid options are:

0 or empty - Recommended transfer point between routes.
1 - Timed transfer point between two routes. The departing vehicle is expected to wait for the arriving one and leave sufficient time for a rider to transfer between routes.
2 - Transfer requires a minimum amount of time between arrival and departure to ensure a connection. The time required to transfer is specified by min_transfer_time.
3 - Transfers are not possible between routes at the location."""),
          resolve = _.value.toMap.get("transfer_type")
        ),
        Field(
          "min_transfer_time",
          OptionType(StringType),
          Some(
            "Amount of time, in seconds, that must be available to permit a transfer between routes at the specified stops. The min_transfer_time should be sufficient to permit a typical rider to move between the two stops, including buffer time to allow for schedule variance on each route."
          ),
          resolve = _.value.toMap.get("min_transfer_time")
        ),
        Field(
          "from_stop",
          OptionType(stopsSchema),
          Some(
            "Identifies a stop or station where a connection between routes begins. If this field refers to a station, the transfer rule applies to all its child stops."
          ),
          resolve = t =>
            t.ctx
              .filterFileBy[StopsFileRow](
                "stops.txt",
                "stop_id",
                t.value.toMap.apply("from_stop_id")
              )
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "to_stop",
          OptionType(stopsSchema),
          Some(
            "Identifies a stop or station where a connection between routes ends. If this field refers to a station, the transfer rule applies to all child stops."
          ),
          resolve = t =>
            t.ctx
              .filterFileBy[StopsFileRow]("stops.txt", "stop_id", t.value.toMap.apply("to_stop_id"))
              .getOrElse(Nil)
              .headOption
        )
      )
  )
  implicit lazy val pathwaysSchema: ObjectType[GtfsContext, PathwaysFileRow] = ObjectType(
    "pathways",
    """
The GTFS-Pathways extension uses a graph representation to describe subway or train, with nodes (the locations) and edges (the pathways).

To go from the entrance (which is a node represented as a location with location_type=2) to a platform (which is a node represented as a location with location_type=0), the rider will go through walkway, fare gates, stairs, etc (which are edges represented as pathways). The proposal also adds another type of location, a generic one called "generic node", to represent for example a walkway crossing from which different walkways can be taken.

Warning: Pathways must be exhaustive in a station. As consequence, as soon as one platform (as stop), entrance or node belonging to a station has a pathway linked to it, the station is assumed to have exhaustive description of its pathways. Therefore, the following common sense rules apply:

No dangling location: If any location within a station has a pathway, then all locations should have pathways (except for those platforms that have boarding areas).
No locked platforms: Each platform must be connected to at least one entrance via some chain of pathways. There are very rare stations in the real life where you cannot go outside.
No pathways for a platform with boarding areas: A platform that has boarding areas is treated as a parent object, not a point. It may not have pathways assigned. All pathways should be for boarding areas.""",
    () =>
      fields[GtfsContext, PathwaysFileRow](
        Field(
          "pathway_id",
          OptionType(StringType),
          Some(
            """The pathway_id field contains an ID that uniquely identifies the pathway. The pathway_id is used by systems as an internal identifier of this record (e.g., primary key in database), and therefore the pathway_id must be dataset unique.
Different pathways can go from the same from_stop_id to the same to_stop_id. For example, this happens when two escalators are side by side in opposite direction, or when a stair is nearby and elevator and both go from the same place to the same place."""
          ),
          resolve = _.value.toMap.get("pathway_id")
        ),
        Field(
          "from_stop_id",
          OptionType(StringType),
          Some(
            "Location at which the pathway begins. It contains a stop_id that identifies a platform, entrance/exit, generic node or boarding area from the stops.txt file."
          ),
          resolve = _.value.toMap.get("from_stop_id")
        ),
        Field(
          "to_stop_id",
          OptionType(StringType),
          Some(
            "Location at which the pathway ends. It contains a stop_id that identifies a platform, entrance/exit, generic node or boarding area from the stops.txt file."
          ),
          resolve = _.value.toMap.get("to_stop_id")
        ),
        Field(
          "pathway_mode",
          OptionType(StringType),
          Some(
            """Type of pathway between the specified (from_stop_id, to_stop_id) pair. Valid values for this field are:
• 1: walkway
• 2: stairs
• 3: moving sidewalk/travelator
• 4: escalator
• 5: elevator
• 6: fare gate (or payment gate): A pathway that crosses into an area of the station where a proof of payment is required (usually via a physical payment gate).
Fare gates may either separate paid areas of the station from unpaid ones, or separate different payment areas within the same station from each other. This information can be used to avoid routing passengers through stations using shortcuts that would require passengers to make unnecessary payments, like directing a passenger to walk through a subway platform to reach a busway.
• 7: exit gate: Indicates a pathway exiting an area where proof-of-payment is required into an area where proof-of-payment is no longer required."""
          ),
          resolve = _.value.toMap.get("pathway_mode")
        ),
        Field(
          "is_bidirectional",
          OptionType(StringType),
          Some(
            """Indicates in which direction the pathway can be used:
• 0: Unidirectional pathway, it can only be used from from_stop_id to to_stop_id.
• 1: Bidirectional pathway, it can be used in the two directions.

Fare gates (pathway_mode=6) and exit gates (pathway_mode=7) cannot be bidirectional."""
          ),
          resolve = _.value.toMap.get("is_bidirectional")
        ),
        Field(
          "length",
          OptionType(StringType),
          Some(
            """Horizontal length in meters of the pathway from the origin location (defined in from_stop_id) to the destination location (defined in to_stop_id).

This field is recommended for walkways (pathway_mode=1), fare gates (pathway_mode=6) and exit gates (pathway_mode=7)."""
          ),
          resolve = _.value.toMap.get("length")
        ),
        Field(
          "traversal_time",
          OptionType(StringType),
          Some(
            """Average time in seconds needed to walk through the pathway from the origin location (defined in from_stop_id) to the destination location (defined in to_stop_id).

This field is recommended for moving sidewalks (pathway_mode=3), escalators (pathway_mode=4) and elevator (pathway_mode=5)."""
          ),
          resolve = _.value.toMap.get("traversal_time")
        ),
        Field(
          "stair_count",
          OptionType(StringType),
          Some("""Number of stairs of the pathway.

Best Practices: one could use the approximation of 1 floor = 15 stairs to generate approximative values.

A positive stair_count implies that the rider walk up from from_stop_id to to_stop_id. And a negative stair_count implies that the rider walk down from from_stop_id to to_stop_id.

This field is recommended for stairs (pathway_mode=2)."""),
          resolve = _.value.toMap.get("stair_count")
        ),
        Field(
          "max_slope",
          OptionType(StringType),
          Some(
            """Maximum slope ratio of the pathway. Valid values for this field are:
• 0 or (empty): no slope.
• A float: slope ratio of the pathway, positive for upwards, negative for downwards.

This field should be used only with walkways (pathway_type=1) and moving sidewalks (pathway_type=3).

Example: In the US, 0.083 (also written 8.3%) is the maximum slope ratio for hand-propelled wheelchair, which mean an increase of 0.083m (so 8.3cm) for each 1m."""
          ),
          resolve = _.value.toMap.get("max_slope")
        ),
        Field(
          "min_width",
          OptionType(StringType),
          Some("""Minimum width of the pathway in meters.

This field is highly recommended if the minimum width is less than 1 meter."""),
          resolve = _.value.toMap.get("min_width")
        ),
        Field(
          "signposted_as",
          OptionType(StringType),
          Some(
            "String of text from physical signage visible to transit riders. The string can be used to provide text directions to users, such as 'follow signs to '. The language text should appear in this field exactly how it is printed on the signs - it should not be translated."
          ),
          resolve = _.value.toMap.get("signposted_as")
        ),
        Field(
          "reversed_signposted_as",
          OptionType(StringType),
          Some(
            "Same than the signposted_as field, but when the pathways is used backward, i.e. from the to_stop_id to the from_stop_id."
          ),
          resolve = _.value.toMap.get("reversed_signposted_as")
        ),
        Field(
          "from_stop",
          OptionType(stopsSchema),
          Some(
            "Location at which the pathway begins. It contains a stop_id that identifies a platform, entrance/exit, generic node or boarding area from the stops.txt file."
          ),
          resolve = t =>
            t.ctx
              .filterFileBy[StopsFileRow](
                "stops.txt",
                "stop_id",
                t.value.toMap.apply("from_stop_id")
              )
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "to_stop",
          OptionType(stopsSchema),
          Some(
            "Location at which the pathway ends. It contains a stop_id that identifies a platform, entrance/exit, generic node or boarding area from the stops.txt file."
          ),
          resolve = t =>
            t.ctx
              .filterFileBy[StopsFileRow]("stops.txt", "stop_id", t.value.toMap.apply("to_stop_id"))
              .getOrElse(Nil)
              .headOption
        )
      )
  )
  implicit lazy val levelsSchema: ObjectType[GtfsContext, LevelsFileRow] = ObjectType(
    "levels",
    """
Describe the different levels of a station. Is mostly useful when used in conjunction with pathways.txt, and is required for elevator (pathway_mode=5) to ask the user to take the elevator to the “Mezzanine” or the “Platform” level.
""",
    () =>
      fields[GtfsContext, LevelsFileRow](
        Field(
          "level_id",
          OptionType(StringType),
          Some("Id of the level that can be referenced from stops.txt."),
          resolve = _.value.toMap.get("level_id")
        ),
        Field(
          "level_index",
          OptionType(StringType),
          Some(
            """Numeric index of the level that indicates relative position of this level in relation to other levels (levels with higher indices are assumed to be located above levels with lower indices).

Ground level should have index 0, with levels above ground indicated by positive indices and levels below ground by negative indices."""
          ),
          resolve = _.value.toMap.get("level_index")
        ),
        Field(
          "level_name",
          OptionType(StringType),
          Some(
            "Optional name of the level (that matches level lettering/numbering used inside the building or the station). Is useful for elevator routing (e.g. “take the elevator to level “Mezzanine” or “Platforms” or “-1”)."
          ),
          resolve = _.value.toMap.get("level_name")
        ),
        Field(
          "stops",
          ListType(stopsSchema),
          Some("Rows from stops.txt where level_id matches this object's level_id."),
          resolve = t =>
            t.ctx
              .filterFileBy[StopsFileRow]("stops.txt", "level_id", t.value.toMap.apply("level_id"))
              .getOrElse(Nil)
        )
      )
  )
  implicit lazy val feedInfoSchema: ObjectType[GtfsContext, FeedInfoFileRow] = ObjectType(
    "feedInfo",
    """
This file contains information about the dataset itself, rather than the services the dataset describes. In some cases, the publisher of the dataset differs from the agencies. If translations.txt is provided, this file is required.
""",
    () =>
      fields[GtfsContext, FeedInfoFileRow](
        Field(
          "feed_publisher_name",
          OptionType(StringType),
          Some(
            "Full name of the organization that publishes the dataset. This might be the same as one of the agency.agency_name values."
          ),
          resolve = _.value.toMap.get("feed_publisher_name")
        ),
        Field(
          "feed_publisher_url",
          OptionType(StringType),
          Some(
            "URL of the dataset publishing organization's website. This may be the same as one of the agency.agency_url values."
          ),
          resolve = _.value.toMap.get("feed_publisher_url")
        ),
        Field(
          "feed_lang",
          OptionType(StringType),
          Some("""Default language for the text in this dataset. This setting helps GTFS consumers choose capitalization rules and other language-specific settings for the dataset.

To define another language, use the language field in translations.txt.

Multilingual datasets might be the default language with the original text in multiple languages. In such cases, use the ISO 639-2 language code mul in the feed_lang field. Provide a translation for each of the languages used in the dataset in translations.txt. If all of the original text in the dataset is in the same language, don't use mul.

For example, a dataset in Switzerland might set the original stops.stop_name field populated with stop names in different languages. Each stop name is written in accordance with the dominant language in that stop’s geographic location. Stop names include Genève for the French-speaking city of Geneva, Zürich for the German-speaking city of Zurich, and Biel/Bienne for the bilingual city of Biel/Bienne. Set feed_lang=mul and provide the following translations in translations.txt:German: "Genf," "Zürich," and "Biel"
French: "Genève," "Zurich," and "Bienne"
Italian: "Ginevra," "Zurigo," and "Bienna"
English: "Geneva," "Zurich," and "Biel/Bienne"."""),
          resolve = _.value.toMap.get("feed_lang")
        ),
        Field(
          "default_lang",
          OptionType(StringType),
          Some(
            "Defines the language used when the data consumer doesn’t know the language of the rider. It's often defined as en, English."
          ),
          resolve = _.value.toMap.get("default_lang")
        ),
        Field(
          "feed_start_date",
          OptionType(StringType),
          Some(
            "The dataset provides complete and reliable schedule information for service in the period from the beginning of the feed_start_date day to the end of the feed_end_date day. Both days can be left empty if unavailable. The feed_end_date date must not precede the feed_start_date date if both are given. Dataset providers are encouraged to give schedule data outside this period to advise of likely future service, but dataset consumers should treat it mindful of its non-authoritative status. If feed_start_date or feed_end_date extend beyond the active calendar dates defined in calendar.txt and calendar_dates.txt, the dataset is making an explicit assertion that there is no service for dates within the feed_start_date to feed_end_date range but not included in the active calendar dates."
          ),
          resolve = _.value.toMap.get("feed_start_date")
        ),
        Field(
          "feed_end_date",
          OptionType(StringType),
          Some("Refer to the feed_start_date row in this table."),
          resolve = _.value.toMap.get("feed_end_date")
        ),
        Field(
          "feed_version",
          OptionType(StringType),
          Some(
            "String that indicates the current version of their GTFS dataset. GTFS-consuming applications can display this value to help dataset publishers determine whether the latest dataset has been incorporated."
          ),
          resolve = _.value.toMap.get("feed_version")
        ),
        Field(
          "feed_contact_email",
          OptionType(StringType),
          Some(
            "Email address for communication regarding the GTFS dataset and data publishing practices. feed_contact_email is a technical contact for GTFS-consuming applications. Provide customer service contact information through agency.txt."
          ),
          resolve = _.value.toMap.get("feed_contact_email")
        ),
        Field(
          "feed_contact_url",
          OptionType(StringType),
          Some(
            "URL for contact information, a web-form, support desk, or other tools for communication regarding the GTFS dataset and data publishing practices. feed_contact_url is a technical contact for GTFS-consuming applications. Provide customer service contact information through agency.txt."
          ),
          resolve = _.value.toMap.get("feed_contact_url")
        )
      )
  )
  implicit lazy val translationsSchema: ObjectType[GtfsContext, TranslationsFileRow] = ObjectType(
    "translations",
    "",
    () =>
      fields[GtfsContext, TranslationsFileRow](
        Field(
          "table_name",
          OptionType(StringType),
          Some("""Defines the dataset table that contains the field to be translated. The following values are allowed:
agency
stops
routes
trips
stop_times
feed_info
Note: Don't include the .txt file extension after the table's name."""),
          resolve = _.value.toMap.get("table_name")
        ),
        Field(
          "field_name",
          OptionType(StringType),
          Some("""Provides the name of the field to be translated. Fields with the type "Text" can be translated, while fields with the types "URL," "Email," and "Phone number" can be included here to provide those resources in the correct language.
Note: Fields with other types are ignored and will not be translated."""),
          resolve = _.value.toMap.get("field_name")
        ),
        Field(
          "language",
          OptionType(StringType),
          Some(
            """Provides the language of translation.
If this language is the same as the one from feed_lang in feed_info.txt, the original value of the field is the default value used in languages without specific translations.
For example, in Switzerland, a city in a bilingual canton is officially called "Biel/Bienne," but it would simply be called "Bienne" in French and "Biel" in German."""
          ),
          resolve = _.value.toMap.get("language")
        ),
        Field(
          "translation",
          OptionType(StringType),
          Some("Provides the translated value for the specified field_name."),
          resolve = _.value.toMap.get("translation")
        ),
        Field(
          "record_id",
          OptionType(StringType),
          Some("""Defines the record that corresponds to the field to be translated. The value in record_id needs to be a main ID from a dataset table, as defined in the following table:
table_namerecord_idagency agency_id stops stop_id routes route_id trips trip_id stop_times trip_id pathways pathway_id levels level_id 

The following conditions determine how this field can be used:
Forbidden if table_name equals feed_info.
Forbidden if field_value is defined.
Required if field_value is empty."""),
          resolve = _.value.toMap.get("record_id")
        ),
        Field(
          "record_sub_id",
          OptionType(StringType),
          Some("""Helps to translate the record that contains the field when the table referenced in record_id doesn’t have a unique ID. The value in record_sub_id is the secondary ID of a dataset table, as defined in the following table:
table_namerecord_sub_idagency NONE stops NONE routes NONE trips NONE stop_times stop_sequence pathways NONE levels NONE 

The following conditions determine how this field can be used:
Forbidden if table_name equals feed_info.
Forbidden if field_value is defined.
Required if table_name equals stop_times and record_id is defined."""),
          resolve = _.value.toMap.get("record_sub_id")
        ),
        Field(
          "field_value",
          OptionType(StringType),
          Some("""Instead of using record_id and record_sub_id to define which record needs to be translated, field_value can be used to define the value for translation. When used, the translation is applied when the field identified by table_name and field_name contains the exact same value defined in field_value.
The field must exactly match the value defined in field_value. If only a subset of the value matches field_value, the translation isn't applied.
If two translation rules match the same record, one with field_value and the other one with record_id, then the rule with record_id is the one that needs to be used.
The following conditions determine how this field can be used:
Forbidden if table_name equals feed_info.
Forbidden if record_id is defined.
Required if record_id is empty."""),
          resolve = _.value.toMap.get("field_value")
        )
      )
  )
  implicit lazy val attributionsSchema: ObjectType[GtfsContext, AttributionsFileRow] = ObjectType(
    "attributions",
    "",
    () =>
      fields[GtfsContext, AttributionsFileRow](
        Field(
          "attribution_id",
          OptionType(StringType),
          Some(
            "Identifies an attribution for the dataset, or a subset of it. This field is useful for translations."
          ),
          resolve = _.value.toMap.get("attribution_id")
        ),
        Field(
          "agency_id",
          OptionType(StringType),
          Some(
            "The agency to which the attribution applies. If one agency_id, route_id, or trip_id attribution is defined, the other fields must be empty. If none are specified, the attribution applies to the whole dataset."
          ),
          resolve = _.value.toMap.get("agency_id")
        ),
        Field(
          "route_id",
          OptionType(StringType),
          Some(
            "This field functions in the same way as agency_id, except the attribution applies to a route. Multiple attributions can apply to the same route."
          ),
          resolve = _.value.toMap.get("route_id")
        ),
        Field(
          "trip_id",
          OptionType(StringType),
          Some(
            "This field functions in the same way as agency_id, except the attribution applies to a trip. Multiple attributions can apply to the same trip."
          ),
          resolve = _.value.toMap.get("trip_id")
        ),
        Field(
          "organization_name",
          OptionType(StringType),
          Some("The name of the organization that the dataset is attributed to."),
          resolve = _.value.toMap.get("organization_name")
        ),
        Field(
          "is_producer",
          OptionType(StringType),
          Some("""The role of the organization is producer. Allowed values include the following:
• 0 or empty: Organization doesn’t have this role.
• 1: Organization does have this role.
At least one of the fields, either is_producer, is_operator, or is_authority, must be set at 1."""),
          resolve = _.value.toMap.get("is_producer")
        ),
        Field(
          "is_operator",
          OptionType(StringType),
          Some(
            "Functions in the same way as is_producer, except the role of the organization is operator."
          ),
          resolve = _.value.toMap.get("is_operator")
        ),
        Field(
          "is_authority",
          OptionType(StringType),
          Some(
            "Functions in the same way as is_producer, except the role of the organization is authority."
          ),
          resolve = _.value.toMap.get("is_authority")
        ),
        Field(
          "attribution_url",
          OptionType(StringType),
          Some("The URL of the organization."),
          resolve = _.value.toMap.get("attribution_url")
        ),
        Field(
          "attribution_email",
          OptionType(StringType),
          Some("The email of the organization."),
          resolve = _.value.toMap.get("attribution_email")
        ),
        Field(
          "attribution_phone",
          OptionType(StringType),
          Some("The phone number of the organization."),
          resolve = _.value.toMap.get("attribution_phone")
        ),
        Field(
          "agency",
          OptionType(agencySchema),
          Some(
            "The agency to which the attribution applies. If one agency_id, route_id, or trip_id attribution is defined, the other fields must be empty. If none are specified, the attribution applies to the whole dataset."
          ),
          resolve = t =>
            t.ctx
              .filterFileBy[AgencyFileRow](
                "agency.txt",
                "agency_id",
                t.value.toMap.apply("agency_id")
              )
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "route",
          OptionType(routesSchema),
          Some(
            "This field functions in the same way as agency_id, except the attribution applies to a route. Multiple attributions can apply to the same route."
          ),
          resolve = t =>
            t.ctx
              .filterFileBy[RoutesFileRow](
                "routes.txt",
                "route_id",
                t.value.toMap.apply("route_id")
              )
              .getOrElse(Nil)
              .headOption
        ),
        Field(
          "trip",
          OptionType(tripsSchema),
          Some(
            "This field functions in the same way as agency_id, except the attribution applies to a trip. Multiple attributions can apply to the same trip."
          ),
          resolve = t =>
            t.ctx
              .filterFileBy[TripsFileRow]("trips.txt", "trip_id", t.value.toMap.apply("trip_id"))
              .getOrElse(Nil)
              .headOption
        )
      )
  )

}
