package me.chadrs.gtfstools.types

import me.chadrs.gtfstools.csv.CsvFromString

import java.time.temporal.{
  ChronoField, ChronoUnit, Temporal, TemporalField, TemporalUnit, UnsupportedTemporalTypeException
}
import java.time.{DateTimeException, Duration, LocalTime}

/**
 * GTFS's time type.
 * Time in the HH:MM:SS format (H:MM:SS is also accepted).
 * The time is measured from "noon minus 12h" of the service day
 * (effectively midnight except for days on which daylight savings
 * time changes occur). For times occurring after midnight, enter
 * the time as a value greater than 24:00:00 in HH:MM:SS local time
 * for the day on which the trip schedule begins.
 *
 * Implements Temporal, but it might have some surprising results due to ambiguity
 * when converting to a LocalTime.
 */
class Time private (hour: Byte, minute: Byte, seconds: Byte)
    extends Comparable[Time]
    with Temporal {
  override def toString: String = f"$hour%02d:$minute%02d:$seconds%02d"

  def toLocalTime: LocalTime = LocalTime.of(hour % 24, minute, seconds)

  def secondsOfDay: Int = hour * 3600 + minute * 60 + seconds

  def plusSeconds(s: Long): Time = Time.fromSeconds(secondsOfDay + s.toInt)

  def pastMidnight: Boolean = hour > 23

  override def compareTo(o: Time): Int = {
    this.secondsOfDay - o.secondsOfDay
  }

  override def isSupported(unit: TemporalUnit): Boolean = toLocalTime.isSupported(unit)
  override def isSupported(field: TemporalField): Boolean = toLocalTime.isSupported(field)

  override def `with`(field: TemporalField, newValue: Long): Temporal = {
    field match {
      case ChronoField.NANO_OF_SECOND | ChronoField.NANO_OF_DAY | ChronoField.MILLI_OF_SECOND |
          ChronoField.MICRO_OF_SECOND =>
        this

      case ChronoField.SECOND_OF_MINUTE => new Time(hour, minute, (newValue % 60).toByte)
      case ChronoField.MINUTE_OF_HOUR   => new Time(hour, (newValue.toByte % 60).toByte, seconds)

      case ChronoField.CLOCK_HOUR_OF_AMPM | ChronoField.CLOCK_HOUR_OF_DAY |
          ChronoField.AMPM_OF_DAY | ChronoField.HOUR_OF_AMPM =>
        Time.fromLocalTime(toLocalTime.`with`(field, newValue))

      case ChronoField.MILLI_OF_DAY  => Time.fromSeconds((newValue / 1000).toInt)
      case ChronoField.HOUR_OF_DAY   => Time.fromSeconds(3600 * newValue.toInt)
      case ChronoField.MINUTE_OF_DAY => Time.fromSeconds(60 * newValue.toInt)
      case ChronoField.SECOND_OF_DAY => Time.fromSeconds(newValue.toInt)
      case ChronoField.MICRO_OF_DAY  => Time.fromSeconds((newValue / 1000_000).toInt)
    }
  }
  override def plus(amountToAdd: Long, unit: TemporalUnit): Temporal = {
    if (pastMidnight) toLocalTime.plus(amountToAdd, unit)
    else {
      unit match {
        case ChronoUnit.DAYS    => this
        case ChronoUnit.SECONDS => plusSeconds(amountToAdd)
        case unit: ChronoUnit   => plusSeconds(unit.getDuration.multipliedBy(amountToAdd).toSeconds)
        case _ =>
          throw new UnsupportedTemporalTypeException(s"$unit Not implemented for >24 hour times")
      }
    }
  }

  override def until(endExclusive: Temporal, unit: TemporalUnit): Long =
    endExclusive match {
      case end: Time if end.pastMidnight && this.pastMidnight =>
        unit.getDuration.dividedBy(Duration.ofSeconds(1))
      case _ => toLocalTime.until(endExclusive, unit)
    }

  override def getLong(field: TemporalField): Long =
    if (field == ChronoField.HOUR_OF_DAY) hour
    else toLocalTime.getLong(field)
}

object Time {

  def parse(s: String): Either[DateTimeException, Time] = {
    s.split(':') match {
      case Array(LessThan60(h), LessThan60(m), LessThan60(s)) =>
        Right(new Time(h, m, s))
      case _ =>
        Left(new DateTimeException(s"$s does not match the format HH:MM:SS"))
    }
  }

  def fromLocalTime(lt: LocalTime): Time =
    new Time(lt.getHour.toByte, lt.getMinute.toByte, lt.getSecond.toByte)

  private object LessThan60 {
    def unapply(s: String): Option[Byte] = {
      s.toByteOption.filter(b => b >= 0 && b < 60)
    }
  }

  def fromSeconds(seconds: Int): Time =
    new Time((seconds / 3600).toByte, ((seconds % 3600) / 60).toByte, (seconds % 60).toByte)

  implicit val ordering: Ordering[Time] = (x: Time, y: Time) => x.compareTo(y)

  implicit val csvFromString: CsvFromString[Time] =
    CsvFromString.fromTrimmedString.flatmapF { str =>
      Time.parse(str).left.map(_.getMessage)
    }
}
