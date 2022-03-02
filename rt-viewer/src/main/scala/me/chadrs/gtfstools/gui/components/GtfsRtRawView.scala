package me.chadrs.gtfstools.gui.components

import scalapb.GeneratedMessage
import com.google.transit.realtime.gtfs_realtime.{FeedEntity, FeedMessage}
import scalafx.scene.control.{Label, ScrollPane}

object GtfsRtRawView {

  def apply[T <: GeneratedMessage](
      feedMessage: FeedMessage,
      entityType: FeedEntity => Option[T]
  ): ScrollPane = {
    new ScrollPane {
      content = new Label {
        style = "-fx-font: 10pt monospace"
        text = feedMessage.entity
          .filter(e => entityType(e).isDefined)
          .map(_.toProtoString)
          .mkString("\n")
      }
    }
  }

}
