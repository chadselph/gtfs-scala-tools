## Gtfs Scala Toolkit

I basically wrote this to learn GTFS (static). I learn best by doing so

### CLI Tools

```console
$ gtfs --help

Usage: gtfs [options] [command] [command-options]

Available commands: agency, hash, routes, stops, stoptimes, trips
```

* agency - read agency.txt
* hash - hash each file in feed (for comparing files)
* routes - read routes.txt
* stops - read stops.txt
* stoptimes - read stoptimes.txt
* trips - read trips.txt

Install:

First, install [coursier](https://get-coursier.io/docs/cli-installation)

then use it to bootstrap gtfs command

```console
$ cs bootstrap -r jitpack me.chadrs.gtfs-scala-tools:gtfs-tools-cli_2.13:0.0.1 -M me.chadrs.gtfstools.cli.Launcher -o gtfs
```

### Codegen

Generates Scala case classes for gtfs files


### CSV
 CSV parser in parboiled2; used for both parsing gtfs files and parsing the gtfs spec
 by the codegen.
 
 
 ### Types
 This module was generated manually by codegen module; it's not yet automated
 and the generated code is currently in source control.
 
 
 ### TODO
 I'd like to use scalajs to make a chrome plugin
 for a very basic gtfs reader in chrome.
 Alternatively, I might try using javafx to make a GTFS GUI.

