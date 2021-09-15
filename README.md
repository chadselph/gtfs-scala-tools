# Gtfs Scala Toolkit

[![Release](https://jitpack.io/v/me.chadrs/gtfs-scala-tools.svg)](https://jitpack.io/#me.chadrs/gtfs-scala-tools)

I basically wrote this to learn GTFS (static). I learn best by doing so

Everything is done in-memory, so it might be very slow for huge files if you don't give java enough heap space.

### CLI Tools

```console
$ gtfs --help

gtfs
Usage: gtfs [options] [command] [command-options]

Available commands: agency, browse, calendar, calendar-dates, draw-shape, expires, hash, routes, rt, service, shapes, stops, stoptimes, trips, trips-per-day, validate

Type  gtfs command --help  for help on an individual command
```

* agency - read agency.txt
* browse - console GUI to browse gtfs file
* calendar - read calendar.txt
* calendar-dates - read calendar_dates.txt
* draw-shape - output a shape as
  an [encoded polyline](https://developers.google.com/maps/documentation/utilities/polylinealgorithm)
* expires - output the final date of service according to calendar.txt and calendar_dates.txt
* hash - hash each file in feed (for comparing files)
* routes - read routes.txt
* rt - outputs human readable representation of gtfs realtime
* service - outputs the dates for each service according to calendar.txt and calendar_dates.txt
* shapes - read shapes.txt
* stops - read stops.txt
* stoptimes - read stoptimes.txt
* trips - read trips.txt
* trips-per-date - counts the number of trips per day for a given range
* validate - runs validation for all the fields and files

Install:

First, install [coursier](https://get-coursier.io/docs/cli-installation)

then use it to bootstrap gtfs command

```console
$ cs bootstrap -r jitpack me.chadrs.gtfs-scala-tools:gtfs-tools-cli_2.13:latest.release -M me.chadrs.gtfstools.cli.Launcher -o gtfs
```

### Codegen

Generates Scala case classes for gtfs files

### CSV

CSV parser in parboiled2; used for both parsing gtfs files and parsing the gtfs spec by the codegen.

### Types

This module was generated manually by codegen module; it's not yet automated and the generated code is currently in
source control.

### TODO

I'd like to use scalajs to make a chrome plugin for a very basic gtfs reader in chrome. Alternatively, I might try using
javafx to make a GTFS GUI.

