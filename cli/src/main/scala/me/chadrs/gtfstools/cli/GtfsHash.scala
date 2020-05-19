package me.chadrs.gtfstools.cli

import java.security.{DigestInputStream, MessageDigest}

import better.files._
import me.chadrs.gtfstools.cli.Commands.{Dump, Hash}
import org.backuity.clist.Cli

object GtfsHash {

  def main(args: Array[String]): Unit = {
    Cli.parse(args).withCommands(new Hash, new Dump).foreach { case args: Hash =>
      val zipInput = args.feed.zis
      zipInput.mapEntries { entry =>
        val digest = new DigestInputStream(zipInput, MessageDigest.getInstance(args.algorithm))
        val bytes = digest.transferTo(NullOutputStream)
        val hash = BigInt(1, digest.getMessageDigest.digest())
        println(f"${entry.getName}%20s ${entry.getTimeLocal}%20s $bytes%10d bytes $hash%032x")
      }.toList

    }

  }

}
