package me.chadrs.gtfstools.cli.subcommands

import java.security.{DigestInputStream, MessageDigest}

import better.files._
import caseapp.CaseApp
import caseapp.core.RemainingArgs
import me.chadrs.gtfstools.cli.GtfsInput
import me.chadrs.gtfstools.cli.GtfsOptions.HashOptions

object HashCmd extends CaseApp[HashOptions] {

  override def run(options: HashOptions, remainingArgs: RemainingArgs): Unit = {
    options match {
      case args: HashOptions =>
        remainingArgs.remaining.map(GtfsInput.fromString).map { feedOrError =>
          feedOrError.left.map(println)
          feedOrError.map { feed =>
            println(feed.description)
            val zipInput = feed.zis
            zipInput
              .mapEntries { entry =>
                val digest =
                  new DigestInputStream(zipInput, MessageDigest.getInstance(args.algorithm))
                val bytes = digest.transferTo(NullOutputStream)
                val hash = BigInt(1, digest.getMessageDigest.digest())
                (entry.getName, entry.getTimeLocal, bytes, hash)
              }
              .to(LazyList)
              .foreach {
                case (path, lastModified, bytes, hash) =>
                  println(f"  $lastModified%20s $bytes%10d bytes $hash%032x $path")
              }
          }
        }
    }
  }
}
