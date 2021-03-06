val commonSettings =
  Seq(scalaVersion := "2.13.2", version := "0.0.4", organization := "com.goswiftly")

def module(name: String) = moduleName := s"gtfs-tools-$name"

val catsVersion = "2.3.1"

lazy val csv = (project in file("csv")).settings(
  commonSettings,
  module("csv"),
  libraryDependencies += "org.parboiled" %% "parboiled" % "2.2.0"
)

lazy val types = (project in file("types"))
  .settings(module("types"), scalaVersion := "2.13.2")
  .dependsOn(csv)

lazy val codegen = (project in file("codegen"))
  .settings(
    commonSettings,
    module("codegen"),
    libraryDependencies += "org.scalameta" %% "scalameta" % "4.3.8"
    // TODO: https://github.com/typelevel/paiges for docstrings
  )
  .dependsOn(csv, types)

lazy val validators = (project in file("validators"))
  .settings(
    commonSettings,
    module("validators"),
    libraryDependencies += "org.typelevel" %% "cats-core" % catsVersion
  )
  .dependsOn(types)

lazy val cli = project
  .in(file("cli"))
  .enablePlugins(GraalVMNativeImagePlugin, JlinkPlugin)
  .settings(
    resolvers += Resolver.sonatypeRepo("releases"),
    commonSettings,
    module("cli"),
    libraryDependencies ++= Seq(
      "com.github.pathikrit" %% "better-files" % "3.9.1",
      "com.jakewharton.fliptables" % "fliptables" % "1.1.0",
      "com.univocity" % "univocity-parsers" % "2.8.4",
      "com.github.alexarchambault" %% "case-app" % "2.0.0-M16",
      "com.googlecode.lanterna" % "lanterna" % "3.0.3",
      "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.1",
      "org.typelevel" %% "cats-core" % catsVersion
    ),
    graalVMNativeImageOptions ++= Seq(
      "--report-unsupported-elements-at-runtime",
      "-H:+ReportExceptionStackTraces",
      "--verbose",
      "--allow-incomplete-classpath",
      "--initialize-at-build-time",
      "--no-fallback"
    ),
    jlinkIgnoreMissingDependency := JlinkIgnore.byPackagePrefix(
      "shapeless" -> "scala.reflect",
      "shapeless" -> "scala.tools",
      "better.files" -> "scala.reflect",
      "org.parboiled2" -> "scala.reflect"
    ),
    initialCommands in console :=
      """import me.chadrs.gtfstools._;
        |import me.chadrs.gtfstools.cli._;
        |import me.chadrs.gtfstools.types._""".stripMargin,
    packMain := Map("gtfs" -> "me.chadrs.gtfstools.cli.Launcher"),
    PB.targets in Compile := Seq(scalapb.gen() -> (sourceManaged in Compile).value / "scalapb")
  )
  .dependsOn(types, validators)
  .enablePlugins(PackPlugin)

lazy val benchmarks = project
  .in(file("benchmarks"))
  .settings(
    commonSettings,
    module("benchmarks"),
    libraryDependencies += "com.univocity" % "univocity-parsers" % "2.8.4"
  )
  .dependsOn(types)
  .enablePlugins(JmhPlugin)
