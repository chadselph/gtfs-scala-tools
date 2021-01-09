package me.chadrs.gtfstools.cli.subcommands

import caseapp.core.RemainingArgs
import caseapp.core.app.CaseApp
import cats.data.ValidatedNec
import cats.implicits.toTraverseOps
import me.chadrs.gtfstools.cli.GtfsInput
import me.chadrs.gtfstools.cli.GtfsOptions.Validate
import me.chadrs.gtfstools.validators.Validators
import cats.implicits._

object ValidateCmd extends CaseApp[Validate] {
  override def run(options: Validate, remainingArgs: RemainingArgs): Unit = {
    remainingArgs.remaining.map(GtfsInput.fromString).map(_.map(validateGtfs))
  }

  def validateGtfs(gtfsInput: GtfsInput) = {
    val gtfsZip = gtfsInput.toGtfsZipFile
    val errors =
      validateFile(gtfsZip.agencies, Validators.agency).iterator ++
        validateFile(gtfsZip.stopTimes, Validators.stopTimes).iterator ++
        validateFile(gtfsZip.trips, Validators.trips).iterator
    errors.foreach(println)
  }

  def validateFile[R, C](
      input: Either[String, IndexedSeq[R]],
      f: R => ValidatedNec[String, C]
  ): List[String] =
    input.toValidatedNec
      .andThen(rows => rows.toVector.map(f).sequence)
      .map(_ => ())
      .fold(_.toList, _ => Nil)

}
