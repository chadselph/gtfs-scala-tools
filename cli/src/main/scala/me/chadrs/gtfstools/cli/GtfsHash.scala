package me.chadrs.gtfstools.cli

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpClient.Redirect
import java.net.http.HttpRequest

import better.files._
import java.net.http.HttpResponse.BodyHandlers
import java.security.{DigestInputStream, MessageDigest}

object GtfsHash {

  def main(args: Array[String]): Unit = {
    val uri = URI.create(args(0))
    val client = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build()
    val downloadedStream = client.send(HttpRequest.newBuilder(uri).GET().build(), BodyHandlers.ofInputStream())
    println(s"${downloadedStream.uri()}: ${downloadedStream.statusCode()}")
    println(downloadedStream.headers())
    val zipInput = downloadedStream.body().asZipInputStream
    zipInput.mapEntries { entry =>
      val digest = new DigestInputStream(zipInput, md5)
      val bytes = digest.transferTo(NullOutputStream)
      val hash = BigInt(1, digest.getMessageDigest.digest())
      println(f"${entry.getName}%20s ${entry.getTimeLocal}%20s $bytes%10d bytes $hash%032X")
    }.toList

  }

  val md5: MessageDigest = MessageDigest.getInstance("MD5")

}
