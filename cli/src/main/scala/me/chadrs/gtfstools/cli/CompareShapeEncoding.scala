package me.chadrs.gtfstools.cli

import com.google.transit.realtime.new_shape.{Shape, ShapePoint}
import me.chadrs.gtfstools.cli.subcommands.DrawShapeCmd
import me.chadrs.gtfstools.types.ShapeId
import me.chadrs.gtfstools.validators.Validators

object CompareShapeEncoding extends App {

  val shape = GtfsInput
    .fromString("https://gtfs-intake-prod.swiftly-internal.com/uploads/vta/latest.zip")
    .getOrElse(throw new Exception("invalid url"))
    .toGtfsZipFile
    .shapes
    .getOrElse(throw new Exception("no shapes found"))
    .filter(row => row.shapeId.contains(ShapeId("102534")))
    .map(Validators.shapes)
    .map(_.getOrElse(throw new Exception("Invalid shape row")))

  val polyencoded = DrawShapeCmd.encode(shape).getBytes("utf-8")

  val protobufEncoded = Shape
    .apply(
      "",
      shape.map(
        gtfs =>
          ShapePoint(
            gtfs.shapePtLat.toValue.toFloat,
            gtfs.shapePtLon.toValue.toFloat,
            gtfs.shapeDistTraveled.map(_.toFloat),
            gtfs.shapePtSequence
          )
      )
    )
    .toByteArray

  val cvsEncoded = shape
    .map(
      row =>
        s"${row.shapeId},${row.shapePtLon},${row.shapePtLat},${row.shapeDistTraveled},${row.shapePtSequence}"
    )
    .mkString("\n")
    .getBytes

  println(s"""
      |Polyencoded bytes: ${polyencoded.length} 
      |Protobuf: ${protobufEncoded.length}
      |CSV: ${cvsEncoded.length}
      |""".stripMargin)

}
