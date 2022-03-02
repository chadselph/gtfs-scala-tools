package me.chadrs.gtfstools.gui

import com.google.transit.realtime.gtfs_realtime.FeedMessage
import me.chadrs.gtfstools.gui.components.{GtfsRtRawView, GtfsStaticView}
import me.chadrs.gtfstools.parsing.{GtfsInput, GtfsZipFile}
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.subway.Subway
import scalafx.application.{Platform, JFXApp3}
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.TabPane.TabClosingPolicy
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint._
import scalafx.stage.{StageStyle, FileChooser}
import scalafx.Includes._
import scalafx.scene.control.Alert.AlertType

import java.net.URI
import scala.util.Try

object GtfsRtViewer extends JFXApp3 {

  val gtfsRtUrl: StringProperty = StringProperty(
    "https://pullmanbusbeacon.com/gtfs-rt/vehiclepositions"
  )
  val feedMessage: ObjectProperty[FeedMessage] = ObjectProperty(FeedMessage.defaultInstance)

  val gtfsStaticUrl: StringProperty = StringProperty("https://pullmanbusbeacon.com/gtfs")

  val parsedGtfs: ObjectProperty[Option[GtfsZipFile]] = ObjectProperty(None)

  override def start(): Unit = {

    stage = new JFXApp3.PrimaryStage {
      initStyle(StageStyle.Unified)
      title = "GTFS Realtime Viewer"
      width = 800
      height = 1000
      scene = new Scene {
        fill = Color.rgb(38, 38, 38)
        root = new VBox {
          padding = Insets(50, 50, 50, 50)
          children = Seq(
            makeMenu(),
            new HBox {
              alignment = Pos.TopCenter
              children = Seq(
                new TextField {
                  text <==> gtfsRtUrl
                  style = "-fx-font: 14pt sans-serif"
                  prefWidth = 600
                },
                new Button("Fetch") {
                  graphic = new FontIcon(Subway.ROUND_ARROW_1)
                  style = "-fx-font: bold 14pt sans-serif"
                  onAction = () =>
                    Platform.runLater {
                      setPathAndFetch(URI.create(gtfsRtUrl.value))
                    }
                }
              )
            },
            new HBox {
              alignment = Pos.TopCenter
              children = Seq(
                new TextField {
                  text <==> gtfsStaticUrl
                  style = "-fx-font: 14pt sans-serif"
                  prefWidth = 600
                },
                new Button("Fetch") {
                  graphic = new FontIcon(Subway.ROUND_ARROW_1)
                  style = "-fx-font: bold 14pt sans-serif"
                  onAction = () =>
                    Platform.runLater {
                      GtfsInput
                        .fromString(gtfsStaticUrl.get())
                        .map(_.toGtfsZipFile)
                        .fold(
                          err => new Alert(AlertType.Error, err),
                          zip => parsedGtfs.set(Some(zip))
                        )
                    }
                }
              )
            },
            makeTabPane(feedMessage)
          )
        }
      }
    }
  }

  def reload(url: String): FeedMessage = {
    FeedMessage.parseFrom(URI.create(url).toURL.openStream())
  }

  def makeTabPane(feedMessage: ObjectProperty[FeedMessage]): TabPane = {
    val tp = new TabPane {
      styleClass.add(TabPane.StyleClassFloating)
      tabClosingPolicy = TabClosingPolicy.Unavailable
      tabs = makeTabs(feedMessage.value, parsedGtfs.value)
    }
    feedMessage.onChange {
      tp.tabs = makeTabs(feedMessage.value, parsedGtfs.value)
    }
    parsedGtfs.onChange {
      tp.tabs = makeTabs(feedMessage.value, parsedGtfs.value)
    }
    tp

  }

  def makeMenu(): MenuBar = {
    new MenuBar {
      useSystemMenuBar = true
      menus = List(
        new Menu("File") {
          items = List(
            new MenuItem("Open...") {
              onAction = _ => setPathAndFetch(new FileChooser().showOpenDialog(stage).toURI)
            },
            new MenuItem("Export") { disable = true }
          )
        },
        new Menu("Bookmarks") {
          items = Bookmarks.default.map(
            bookmark =>
              new MenuItem(bookmark.name) {
                onAction = _ => setPathAndFetch(bookmark.uri)
              }
          )
        }
      )
    }
  }

  def setPathAndFetch(uri: URI): Unit = {
    gtfsRtUrl.setValue(uri.toString)

    Try(this.reload(uri.toString))
      .fold(err => new Alert(AlertType.Error, err.getMessage).show(), feedMessage.set(_))
  }

  def makeTabs(feedMessage: FeedMessage, gtfs: Option[GtfsZipFile]): Seq[Tab] = {
    Seq(
      new Tab {
        graphic = new FontIcon(Subway.SMS_9)
        this.disable = !feedMessage.entity.exists(_.alert.isDefined)
        text = "Alerts"
        content = GtfsRtRawView(feedMessage, _.alert)
      },
      new Tab {
        graphic = new FontIcon(Subway.TIME_2)
        this.disable = !feedMessage.entity.exists(_.tripUpdate.isDefined)
        text = "Trip Updates"
        content = GtfsRtRawView(feedMessage, _.tripUpdate)
      },
      new Tab {
        graphic = new FontIcon(Subway.LOCATION)
        this.disable = !feedMessage.entity.exists(_.vehicle.isDefined)
        text = "Vehicles"
        content = GtfsRtRawView(feedMessage, _.vehicle)
      },
      new Tab {
        graphic = new FontIcon(Subway.ADMIN)
        this.disable = !gtfs.flatMap(_.agencies.toOption).exists(_.nonEmpty)
        text = "Agencies"
        content = gtfs.map(file => GtfsStaticView(file, _.agencies)).getOrElse(new ScrollPane())
      },
      new Tab {
        graphic = new FontIcon(Subway.CALENDAR)
        this.disable = !(gtfs
          .flatMap(_.calendars.toOption)
          .exists(_.nonEmpty) || gtfs.flatMap(_.calendarDates.toOption).exists(_.nonEmpty))
        text = "Calendars"
        content = gtfs.map(file => GtfsStaticView(file, _.calendars)).getOrElse(new ScrollPane())
      },
      new Tab {
        graphic = new FontIcon(Subway.CLOTH_1)
        this.disable = !gtfs.flatMap(_.routes.toOption).exists(_.nonEmpty)
        text = "Routes"
        content = gtfs.map(file => GtfsStaticView(file, _.routes)).getOrElse(new ScrollPane())
      }
    )
  }

}
