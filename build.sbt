import sbtprotoc.ProtocPlugin.autoImport.PB

val commonSettings =
  Seq(scalaVersion := "2.13.7", version := "0.0.5", organization := "me.chadrs")

def module(name: String) = moduleName := s"gtfs-tools-$name"

val catsVersion = "2.3.1"

lazy val rtBindings = project
  .in(file("rt-bindings"))
  .settings(
    commonSettings,
    module("rt-bindings"),
    Compile / PB.targets := Seq(scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"),
    libraryDependencies ++= Seq(
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
    )
  )

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
    libraryDependencies += "org.typelevel" %% "cats-core" % catsVersion,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.8" % Test
  )
  .dependsOn(types)

lazy val parsing = (project in file("parsing"))
  .settings(
    commonSettings,
    module("parsing"),
    libraryDependencies ++= Seq(
      "com.github.pathikrit" %% "better-files" % "3.9.1",
      "com.univocity" % "univocity-parsers" % "2.8.4"
    )
  )
  .dependsOn(types, rtBindings)

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
    console / initialCommands :=
      """import me.chadrs.gtfstools._;
        |import me.chadrs.gtfstools.cli._;
        |import me.chadrs.gtfstools.types._""".stripMargin,
    packMain := Map("gtfs" -> "me.chadrs.gtfstools.cli.Launcher")
  )
  .dependsOn(types, validators, rtBindings, parsing)
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

lazy val javaFXModules = {
  // Determine OS version of JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux")   => "linux"
    case n if n.startsWith("Mac")     => "mac"
    case n if n.startsWith("Windows") => "win"
    case _                            => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web").map(
    m => "org.openjfx" % s"javafx-$m" % "17.0.1" classifier osName
  )
}

lazy val rtViewer = project
  .in(file("rt-viewer"))
  .settings(
    commonSettings,
    module("rt-viewer"),
    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % "17.0.1-R26",
      "org.kordamp.ikonli" % "ikonli-subway-pack" % "12.3.0",
      "org.kordamp.ikonli" % "ikonli-javafx" % "12.3.0"
    ),
    libraryDependencies ++= javaFXModules,
    fork := true
  )
  .dependsOn(types, rtBindings, parsing)
