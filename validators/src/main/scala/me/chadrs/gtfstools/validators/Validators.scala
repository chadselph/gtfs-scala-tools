package me.chadrs.gtfstools.validators

import cats.data.ValidatedNec
import me.chadrs.gtfstools.types._
import cats.implicits._
import cats.data.Validated._

object Validators {

  type Error = String
  type ValidationResult[A] = ValidatedNec[Error, A]

  def agency(csv: AgencyFileRow): ValidationResult[Agency] = {
    (
      csv.agencyId.toValidatedNec,
      csv.agencyName.toValidatedNec,
      csv.agencyUrl.toValidatedNec,
      csv.agencyTimezone.toValidatedNec,
      csv.agencyLang.toValidatedNec,
      csv.agencyPhone.toValidatedNec,
      csv.agencyFareUrl.toValidatedNec,
      csv.agencyEmail.toValidatedNec
    ).mapN({
      case (
            agencyId,
            agencyName,
            agencyUrl,
            agencyTimezone,
            agencyLang,
            agencyPhone,
            agencyFareUrl,
            agencyEmail
          ) =>
        Agency(
          agencyId,
          agencyName,
          agencyUrl,
          agencyTimezone,
          agencyLang,
          agencyPhone,
          agencyFareUrl,
          agencyEmail
        )
    })
  }
  def stops(csv: StopsFileRow): ValidationResult[Stops] = {
    (
      csv.stopId.toValidatedNec,
      csv.stopCode.toValidatedNec,
      csv.stopName.toValidatedNec,
      csv.stopDesc.toValidatedNec,
      csv.stopLat.toValidatedNec,
      csv.stopLon.toValidatedNec,
      csv.zoneId.toValidatedNec,
      csv.stopUrl.toValidatedNec,
      csv.locationType.toValidatedNec,
      csv.parentStation.toValidatedNec,
      csv.stopTimezone.toValidatedNec,
      csv.wheelchairBoarding.toValidatedNec,
      csv.levelId.toValidatedNec,
      csv.platformCode.toValidatedNec
    ).mapN({
      case (
            stopId,
            stopCode,
            stopName,
            stopDesc,
            stopLat,
            stopLon,
            zoneId,
            stopUrl,
            locationType,
            parentStation,
            stopTimezone,
            wheelchairBoarding,
            levelId,
            platformCode
          ) =>
        Stops(
          stopId,
          stopCode,
          stopName,
          stopDesc,
          stopLat,
          stopLon,
          zoneId,
          stopUrl,
          locationType,
          parentStation,
          stopTimezone,
          wheelchairBoarding,
          levelId,
          platformCode
        )
    })
  }
  def routes(csv: RoutesFileRow): ValidationResult[Routes] = {
    (
      csv.routeId.toValidatedNec,
      csv.agencyId.toValidatedNec,
      csv.routeShortName.toValidatedNec,
      csv.routeLongName.toValidatedNec,
      csv.routeDesc.toValidatedNec,
      csv.routeType.toValidatedNec,
      csv.routeUrl.toValidatedNec,
      csv.routeColor.toValidatedNec,
      csv.routeTextColor.toValidatedNec,
      csv.routeSortOrder.toValidatedNec
    ).mapN({
      case (
            routeId,
            agencyId,
            routeShortName,
            routeLongName,
            routeDesc,
            routeType,
            routeUrl,
            routeColor,
            routeTextColor,
            routeSortOrder
          ) =>
        Routes(
          routeId,
          agencyId,
          routeShortName,
          routeLongName,
          routeDesc,
          routeType,
          routeUrl,
          routeColor,
          routeTextColor,
          routeSortOrder
        )
    })
  }
  def trips(csv: TripsFileRow): ValidationResult[Trips] = {
    (
      csv.routeId.toValidatedNec,
      csv.serviceId.toValidatedNec,
      csv.tripId.toValidatedNec,
      csv.tripHeadsign.toValidatedNec,
      csv.tripShortName.toValidatedNec,
      csv.directionId.toValidatedNec,
      csv.blockId.toValidatedNec,
      csv.shapeId.toValidatedNec,
      csv.wheelchairAccessible.toValidatedNec,
      csv.bikesAllowed.toValidatedNec
    ).mapN({
      case (
            routeId,
            serviceId,
            tripId,
            tripHeadsign,
            tripShortName,
            directionId,
            blockId,
            shapeId,
            wheelchairAccessible,
            bikesAllowed
          ) =>
        Trips(
          routeId,
          serviceId,
          tripId,
          tripHeadsign,
          tripShortName,
          directionId,
          blockId,
          shapeId,
          wheelchairAccessible,
          bikesAllowed
        )
    })
  }
  def stopTimes(csv: StopTimesFileRow): ValidationResult[StopTimes] = {
    (
      csv.tripId.toValidatedNec,
      csv.arrivalTime.toValidatedNec,
      csv.departureTime.toValidatedNec,
      csv.stopId.toValidatedNec,
      csv.stopSequence.toValidatedNec,
      csv.stopHeadsign.toValidatedNec,
      csv.pickupType.toValidatedNec,
      csv.dropOffType.toValidatedNec,
      csv.shapeDistTraveled.toValidatedNec,
      csv.timepoint.toValidatedNec
    ).mapN({
      case (
            tripId,
            arrivalTime,
            departureTime,
            stopId,
            stopSequence,
            stopHeadsign,
            pickupType,
            dropOffType,
            shapeDistTraveled,
            timepoint
          ) =>
        StopTimes(
          tripId,
          arrivalTime,
          departureTime,
          stopId,
          stopSequence,
          stopHeadsign,
          pickupType,
          dropOffType,
          shapeDistTraveled,
          timepoint
        )
    })
  }
  def calendar(csv: CalendarFileRow): ValidationResult[Calendar] = {
    (
      csv.serviceId.toValidatedNec,
      csv.monday.toValidatedNec,
      csv.tuesday.toValidatedNec,
      csv.wednesday.toValidatedNec,
      csv.thursday.toValidatedNec,
      csv.friday.toValidatedNec,
      csv.saturday.toValidatedNec,
      csv.sunday.toValidatedNec,
      csv.startDate.toValidatedNec,
      csv.endDate.toValidatedNec
    ).mapN({
      case (
            serviceId,
            monday,
            tuesday,
            wednesday,
            thursday,
            friday,
            saturday,
            sunday,
            startDate,
            endDate
          ) =>
        Calendar(
          serviceId,
          monday,
          tuesday,
          wednesday,
          thursday,
          friday,
          saturday,
          sunday,
          startDate,
          endDate
        )
    })
  }
  def calendarDates(csv: CalendarDatesFileRow): ValidationResult[CalendarDates] = {
    (csv.serviceId.toValidatedNec, csv.date.toValidatedNec, csv.exceptionType.toValidatedNec).mapN({
      case (serviceId, date, exceptionType) =>
        CalendarDates(serviceId, date, exceptionType)
    })
  }
  def fareAttributes(csv: FareAttributesFileRow): ValidationResult[FareAttributes] = {
    (
      csv.fareId.toValidatedNec,
      csv.price.toValidatedNec,
      csv.currencyType.toValidatedNec,
      csv.paymentMethod.toValidatedNec,
      csv.transfers.toValidatedNec,
      csv.agencyId.toValidatedNec,
      csv.transferDuration.toValidatedNec
    ).mapN({
      case (fareId, price, currencyType, paymentMethod, transfers, agencyId, transferDuration) =>
        FareAttributes(
          fareId,
          price,
          currencyType,
          paymentMethod,
          transfers,
          agencyId,
          transferDuration
        )
    })
  }
  def fareRules(csv: FareRulesFileRow): ValidationResult[FareRules] = {
    (
      csv.fareId.toValidatedNec,
      csv.routeId.toValidatedNec,
      csv.originId.toValidatedNec,
      csv.destinationId.toValidatedNec,
      csv.containsId.toValidatedNec
    ).mapN({
      case (fareId, routeId, originId, destinationId, containsId) =>
        FareRules(fareId, routeId, originId, destinationId, containsId)
    })
  }
  def shapes(csv: ShapesFileRow): ValidationResult[Shapes] = {
    (
      csv.shapeId.toValidatedNec,
      csv.shapePtLat.toValidatedNec,
      csv.shapePtLon.toValidatedNec,
      csv.shapePtSequence.toValidatedNec,
      csv.shapeDistTraveled.toValidatedNec
    ).mapN({
      case (shapeId, shapePtLat, shapePtLon, shapePtSequence, shapeDistTraveled) =>
        Shapes(shapeId, shapePtLat, shapePtLon, shapePtSequence, shapeDistTraveled)
    })
  }
  def frequencies(csv: FrequenciesFileRow): ValidationResult[Frequencies] = {
    (
      csv.tripId.toValidatedNec,
      csv.startTime.toValidatedNec,
      csv.endTime.toValidatedNec,
      csv.headwaySecs.toValidatedNec,
      csv.exactTimes.toValidatedNec
    ).mapN({
      case (tripId, startTime, endTime, headwaySecs, exactTimes) =>
        Frequencies(tripId, startTime, endTime, headwaySecs, exactTimes)
    })
  }
  def transfers(csv: TransfersFileRow): ValidationResult[Transfers] = {
    (
      csv.fromStopId.toValidatedNec,
      csv.toStopId.toValidatedNec,
      csv.transferType.toValidatedNec,
      csv.minTransferTime.toValidatedNec
    ).mapN({
      case (fromStopId, toStopId, transferType, minTransferTime) =>
        Transfers(fromStopId, toStopId, transferType, minTransferTime)
    })
  }
  def pathways(csv: PathwaysFileRow): ValidationResult[Pathways] = {
    (
      csv.pathwayId.toValidatedNec,
      csv.fromStopId.toValidatedNec,
      csv.toStopId.toValidatedNec,
      csv.pathwayMode.toValidatedNec,
      csv.isBidirectional.toValidatedNec,
      csv.length.toValidatedNec,
      csv.traversalTime.toValidatedNec,
      csv.stairCount.toValidatedNec,
      csv.maxSlope.toValidatedNec,
      csv.minWidth.toValidatedNec,
      csv.signpostedAs.toValidatedNec,
      csv.reversedSignpostedAs.toValidatedNec
    ).mapN({
      case (
            pathwayId,
            fromStopId,
            toStopId,
            pathwayMode,
            isBidirectional,
            length,
            traversalTime,
            stairCount,
            maxSlope,
            minWidth,
            signpostedAs,
            reversedSignpostedAs
          ) =>
        Pathways(
          pathwayId,
          fromStopId,
          toStopId,
          pathwayMode,
          isBidirectional,
          length,
          traversalTime,
          stairCount,
          maxSlope,
          minWidth,
          signpostedAs,
          reversedSignpostedAs
        )
    })
  }
  def levels(csv: LevelsFileRow): ValidationResult[Levels] = {
    (csv.levelId.toValidatedNec, csv.levelIndex.toValidatedNec, csv.levelName.toValidatedNec).mapN({
      case (levelId, levelIndex, levelName) =>
        Levels(levelId, levelIndex, levelName)
    })
  }
  def feedInfo(csv: FeedInfoFileRow): ValidationResult[FeedInfo] = {
    (
      csv.feedPublisherName.toValidatedNec,
      csv.feedPublisherUrl.toValidatedNec,
      csv.feedLang.toValidatedNec,
      csv.defaultLang.toValidatedNec,
      csv.feedStartDate.toValidatedNec,
      csv.feedEndDate.toValidatedNec,
      csv.feedVersion.toValidatedNec,
      csv.feedContactEmail.toValidatedNec,
      csv.feedContactUrl.toValidatedNec
    ).mapN({
      case (
            feedPublisherName,
            feedPublisherUrl,
            feedLang,
            defaultLang,
            feedStartDate,
            feedEndDate,
            feedVersion,
            feedContactEmail,
            feedContactUrl
          ) =>
        FeedInfo(
          feedPublisherName,
          feedPublisherUrl,
          feedLang,
          defaultLang,
          feedStartDate,
          feedEndDate,
          feedVersion,
          feedContactEmail,
          feedContactUrl
        )
    })
  }
  def translations(csv: TranslationsFileRow): ValidationResult[Translations] = {
    (
      csv.tableName.toValidatedNec,
      csv.fieldName.toValidatedNec,
      csv.language.toValidatedNec,
      csv.translation.toValidatedNec,
      csv.recordId.toValidatedNec,
      csv.recordSubId.toValidatedNec,
      csv.fieldValue.toValidatedNec
    ).mapN({
      case (tableName, fieldName, language, translation, recordId, recordSubId, fieldValue) =>
        Translations(tableName, fieldName, language, translation, recordId, recordSubId, fieldValue)
    })
  }
  def attributions(csv: AttributionsFileRow): ValidationResult[Attributions] = {
    (
      csv.attributionId.toValidatedNec,
      csv.agencyId.toValidatedNec,
      csv.routeId.toValidatedNec,
      csv.tripId.toValidatedNec,
      csv.organizationName.toValidatedNec,
      csv.isProducer.toValidatedNec,
      csv.isOperator.toValidatedNec,
      csv.isAuthority.toValidatedNec,
      csv.attributionUrl.toValidatedNec,
      csv.attributionEmail.toValidatedNec,
      csv.attributionPhone.toValidatedNec
    ).mapN({
      case (
            attributionId,
            agencyId,
            routeId,
            tripId,
            organizationName,
            isProducer,
            isOperator,
            isAuthority,
            attributionUrl,
            attributionEmail,
            attributionPhone
          ) =>
        Attributions(
          attributionId,
          agencyId,
          routeId,
          tripId,
          organizationName,
          isProducer,
          isOperator,
          isAuthority,
          attributionUrl,
          attributionEmail,
          attributionPhone
        )
    })
  }

}
