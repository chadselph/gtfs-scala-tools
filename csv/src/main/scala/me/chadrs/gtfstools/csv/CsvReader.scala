package me.chadrs.gtfstools.csv

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}

import me.chadrs.gtfstools.csv.CsvReader.Result

import scala.util.Try

trait CsvReader[F] {
  def readFile(file: CsvFile): Seq[Result[F]] = {
    val indices = file.headers.zipWithIndex.toMap
    file.rows.zipWithIndex.map { case (row, rowNum) =>
      // TODO get extra columns
      readRow(new IndexedSeqCsvCursor(indices, row.toIndexedSeq, rowNum + 2))
    }
  }

  def readRow(getValue: CsvCursor): CsvReader.Result[F]
}

object CsvReader {

  type Error = String
  type Result[T] = Either[Error, T]

  def parseAs[T](file: CsvFile)(implicit reader: CsvReader[T]): Seq[Result[T]] =
    reader.readFile(file)
}

/**
 * Similar to [[CsvReader]] but the validation is deferred so [[CsvRowViewer]].fromCsvCursor cannot
 * fail, but actually reading the column might. This makes more lenient apps possible since we can
 * just skip validation on columns we don't care about.
 */
trait CsvRowViewer[F] {

  def mapFile(file: CsvFile): IndexedSeq[F] = {
    val indices = file.headers.zipWithIndex.toMap
    file.rows.zipWithIndex.map { case (row, rowNum) =>
      fromCsvCursor(new IndexedSeqCsvCursor(indices, row.toIndexedSeq, rowNum + 2))
    }
  }.toIndexedSeq

  def fromCsvCursor(cursor: IndexedSeqCsvCursor): F
}

object CsvRowViewer {
  def mapFile[F: CsvRowViewer](file: CsvFile): IndexedSeq[F] =
    implicitly[CsvRowViewer[F]].mapFile(file)
}

trait CsvFromString[T] {
  def fromString(input: String): CsvReader.Result[Option[T]]

  def map[U](f: T => U): CsvFromString[U] = (input: String) => fromString(input).map(_.map(f))

  def flatmapF[U](f: T => Either[String, U]): CsvFromString[U] =
    (input: String) =>
      fromString(input).flatMap {
        case None    => Right(None) // if the field was empty, don't try to convert it.
        case Some(t) => f(t).map(Some(_))
      }
}

trait CsvCursor {
  def required[T: CsvFromString](key: String): CsvReader.Result[T]
  def optionally[T: CsvFromString](key: String): CsvReader.Result[Option[T]]
  def toMap: Map[String, String]
}

class IndexedSeqCsvCursor(indices: Map[String, Int], row: IndexedSeq[String], rowNumber: Int)
    extends CsvCursor {

  override def required[T: CsvFromString](key: String): CsvReader.Result[T] =
    getFromRow(key, row, rowNumber)
      .getOrElse(Left(s"no such column $key"))
      .flatMap(readValue(_))
      // required fields don't allow empty, turn None to left
      .flatMap(opt => opt.toRight("empty"))
      .left
      .map(error => s"Row $rowNumber column $key: $error")
  override def optionally[T: CsvFromString](key: String): CsvReader.Result[Option[T]] = {
    getFromRow(key, row, rowNumber)
      .map(_.flatMap(readValue(_)))
      // optional fields allow missing columns. turn None to Right
      .getOrElse(Right(None))
      .left
      .map(error => s"Row $rowNumber column $key: $error")
  }

  override def toMap: Map[String, String] = indices.view.mapValues(row.apply).toMap

  protected def readValue[T](s: String)(implicit reader: CsvFromString[T]): Result[Option[T]] =
    reader.fromString(s)

  private def getFromRow(
      key: String,
      row: Seq[String],
      rowNum: Int
  ): Option[Either[String, String]] =
    indices.get(key).map { index =>
      Either.cond(index < row.length, row(index), s"Line #$rowNum: index $index out of bounds")
    }

}

object CsvFromString {

  implicit val stringfromString: CsvFromString[String] = s => Right(Option(s).filter(_.nonEmpty))
  val fromTrimmedString: CsvFromString[String] = stringfromString.map(_.trim)

  implicit val intFromString: CsvFromString[Int] =
    fromTrimmedString.flatmapF(nonEmptyStr =>
      Either.cond(nonEmptyStr.forall(_.isDigit), nonEmptyStr.toInt, "not a number")
    )

  implicit val doubleFromString: CsvFromString[Double] =
    fromTrimmedString.flatmapF(nonEmptyStr =>
      nonEmptyStr.toDoubleOption.toRight("not a floating point number")
    )

  implicit val boolFromString: CsvFromString[Boolean] = fromTrimmedString.flatmapF {
    case "1" => Right(true)
    case "0" => Right(false)
    case _   => Left("Expected 1 or 0")
  }

  implicit val urlFromString: CsvFromString[java.net.URI] = fromTrimmedString.flatmapF { s =>
    Try(java.net.URI.create(s)).toEither.left.map(_ => "Not a URL")
  }

  private val gtfsDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
  implicit val localDateFromString: CsvFromString[LocalDate] = fromTrimmedString.flatmapF { s =>
    Try(LocalDate.parse(s, gtfsDateFormat)).toEither.left.map(_ => "Not a date in YYYYMMDD format")
  }
  private val gtfsTimeFormat = DateTimeFormatter.ofPattern("H:mm:ss")
  implicit val localTimeFromString: CsvFromString[LocalTime] = fromTrimmedString.flatmapF { s =>
    Try(LocalTime.parse(s, gtfsTimeFormat)).toEither.left
      .map(_ => s"$s is not a time in Hmmss format")
  }

  implicit val zoneIdFromString: CsvFromString[java.time.ZoneId] = fromTrimmedString.flatmapF { s =>
    Try(java.time.ZoneId.of(s)).toEither.left.map(_ => s"$s is not a valid timezone")
  }

}
