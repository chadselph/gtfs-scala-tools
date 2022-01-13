package me.chadrs.gtfstools.cli.subcommands

import caseapp.CaseApp
import caseapp.core.RemainingArgs
import me.chadrs.gtfstools.cli.GtfsOptions.DrawShape
import me.chadrs.gtfstools.types.{ShapeId, ShapesFileRow, Shapes}
import cats.implicits._
import me.chadrs.gtfstools.parsing.GtfsInput

object DrawShapeCmd extends CaseApp[DrawShape] {
  override def run(options: DrawShape, remainingArgs: RemainingArgs): Unit = {
    for {
      input <- GtfsInput.fromString(remainingArgs.remaining.head)
      allShapeRows <- input.toGtfsZipFile.parseFile[ShapesFileRow]("shapes.txt")
      shape <- filterShape(allShapeRows, ShapeId(options.shapeId))
    } yield {
      System.out.println(encode(shape))
    }
  }

  def filterShape(
      raw: IndexedSeq[ShapesFileRow],
      shapeId: ShapeId
  ): Either[String, IndexedSeq[Shapes]] = {
    raw
      .collect {
        case row if row.shapeId.contains(shapeId) =>
          for {
            lat <- row.shapePtLat
            lon <- row.shapePtLon
            seq <- row.shapePtSequence
          } yield Shapes(shapeId, lat, lon, seq, None)
      }
      .toList
      .sequence
      .map(_.sortBy(_.shapePtSequence).toIndexedSeq)
  }

  /**
   * Copied from https://github.com/googlemaps/google-maps-services-java/blob/master/src/main/java/com/google/maps/internal/PolylineEncoding.java
   */
  def encode(path: Seq[Shapes]): String = {
    var lastLat = 0L
    var lastLng = 0L
    val result = new StringBuilder
    for (point <- path) {
      val lat: Long = Math.round(point.shapePtLat.toValue * 1e5)
      val lng: Long = Math.round(point.shapePtLon.toValue * 1e5)
      encode(lat - lastLat, result)
      encode(lng - lastLng, result)
      lastLat = lat
      lastLng = lng
    }
    result.toString
  }

  private def encode(in: Long, result: StringBuilder): Unit = {
    var v = if (in < 0) ~(in << 1) else in << 1
    while (v >= 0x20) {
      result.append(((0x20 | (v & 0x1f)) + 63).toChar)
      v = v >> 5
    }
    result.append((v + 63).toChar)
  }
}
