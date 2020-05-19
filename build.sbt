val commonSettings =
  Seq(scalaVersion := "2.13.2", version := "0.1.0", organization := "com.goswiftly")

def module(name: String) = moduleName := s"gtfs-tools-$name"

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

lazy val cli = project
  .in(file("cli"))
  .enablePlugins(GraalVMNativeImagePlugin, JlinkPlugin)
  .settings(
    commonSettings,
    module("cli"),
    libraryDependencies ++= Seq(
      "com.github.pathikrit" %% "better-files" % "3.8.0",
      "com.jakewharton.fliptables" % "fliptables" % "1.1.0",
      "org.backuity.clist" %% "clist-core" % "3.5.1",
      "org.backuity.clist" %% "clist-macros" % "3.5.1" % "provided"
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
    )
  )
  .dependsOn(types)
