package me.chadrs.gtfstools.cli.calendar

import me.chadrs.gtfstools.cli.calendar.Extensions._
import me.chadrs.gtfstools.types.{ExceptionType, Calendar, CalendarDates, ServiceId}
import me.chadrs.gtfstools.validators.Validators
import me.chadrs.gtfstools.parsing.GtfsZipFile

import scala.compat.java8.StreamConverters._
import java.time.{DayOfWeek, LocalDate}
import scala.collection.immutable.SortedSet

case class Service(calendar: Option[Calendar], dates: Seq[CalendarDates]) {

  val addedDates: Set[LocalDate] =
    dates.filter(_.exceptionType == ExceptionType(1)).map(_.date).toSet
  val removedDates: Set[LocalDate] =
    dates.filter(_.exceptionType == ExceptionType(2)).map(_.date).toSet
  val daysOfWeek: Set[DayOfWeek] =
    calendar.daySet(DayOfWeek.SUNDAY, _.sunday) ++
      calendar.daySet(DayOfWeek.MONDAY, _.monday) ++
      calendar.daySet(DayOfWeek.TUESDAY, _.tuesday) ++
      calendar.daySet(DayOfWeek.WEDNESDAY, _.wednesday) ++
      calendar.daySet(DayOfWeek.THURSDAY, _.thursday) ++
      calendar.daySet(DayOfWeek.FRIDAY, _.friday) ++
      calendar.daySet(DayOfWeek.SATURDAY, _.saturday)

  def isActive(date: LocalDate): Boolean = {
    addedDates.contains(date) || (!removedDates.contains(date) && calendar.exists { cal =>
      daysOfWeek.contains(date.getDayOfWeek) && date.isBetweenInclusive(cal.startDate, cal.endDate)
    })
  }

  def activeDates: SortedSet[LocalDate] = {
    val fromCalendar = calendar.toSet.flatMap { c: Calendar =>
      c.startDate
        .datesUntil(c.endDate.plusDays(1))
        .filter(d => daysOfWeek.contains(d.getDayOfWeek))
        .toScala[Set]
    }
    implicit val dateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)
    SortedSet() ++ (fromCalendar -- removedDates ++ addedDates)
  }

}

object Service {
  def forGtfs(input: GtfsZipFile): Map[ServiceId, Service] = {
    val calendars = input.calendars.getOrElse(Nil).groupBy(_.serviceId).collect {
      case (Right(serviceId), calendars) =>
        serviceId -> Validators.calendar(calendars.head).toOption
    }
    val dates = input.calendarDates.getOrElse(Nil).groupBy(_.serviceId).collect {
      case (Right(serviceId), dates) =>
        serviceId -> dates.map(Validators.calendarDates).flatMap(_.toOption.toVector)
    }
    val allServiceIds = calendars.keys.toSet ++ dates.keys.toSet
    allServiceIds.map { serviceId =>
      serviceId -> Service(
        calendars.getOrElse(serviceId, None),
        dates.getOrElse(serviceId, Vector.empty)
      )
    }.toMap
  }
}

/** Some helpers for nicer postfix method names */
private object Extensions {

  implicit class OptCalendarWithDaySet(val optCal: Option[Calendar]) {
    def daySet(day: DayOfWeek, getDay: Calendar => Boolean): Set[DayOfWeek] = {
      Option.when(optCal.exists(getDay))(day).toSet
    }
  }

  implicit class LocalDateWithIsBetweenInc(val date: LocalDate) {
    def isBetweenInclusive(start: LocalDate, end: LocalDate): Boolean =
      date == start || date == end || (date.isBefore(end) && date.isAfter(start))
  }
}
