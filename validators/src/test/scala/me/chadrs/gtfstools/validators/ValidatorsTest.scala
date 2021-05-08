package me.chadrs.gtfstools.validators

import me.chadrs.gtfstools.types.{Latitude, Longitude, StopId, Stops, StopsFileRow}
import me.chadrs.gtfstools.csv.IndexedSeqCsvCursor
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import cats.implicits._

class ValidatorsTest extends AnyFunSpec with Matchers {

  describe("stops validator") {
    it("works for a valid stop") {
      val row =
        StopsFileRow.csvReader.fromCsvCursor(
          new IndexedSeqCsvCursor(
            List("stop_id", "stop_name", "stop_lat", "stop_lon").zipWithIndex.toMap,
            IndexedSeq("1234", "Some Stop", "12", "132.0"),
            0
          )
        )
      Validators.stops(row) should be(
        Stops(
          StopId("1234"),
          None,
          Some("Some Stop"),
          None,
          Some(Latitude(12.0)),
          Some(Longitude(132.0)),
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None
        ).validNec
      )
    }
    it("validates stop_lat and stop_lon") {
      val row =
        StopsFileRow.csvReader.fromCsvCursor(
          new IndexedSeqCsvCursor(
            List("stop_id", "stop_name", "stop_lat", "stop_lon").zipWithIndex.toMap,
            IndexedSeq("1234", "Some Stop", "latty", "132.0"),
            0
          )
        )
      Validators.stops(row) should be(
        "Row 0 column stop_lat: not a floating point number".invalidNec[Stops]
      )
    }
  }

}
