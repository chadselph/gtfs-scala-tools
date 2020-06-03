package me.chadrs.gtfstools.bench

import java.util.concurrent.TimeUnit

import com.univocity.parsers.csv.CsvParserSettings
import me.chadrs.gtfstools.csv.{CsvFile, CsvParser, CsvReader, CsvRowViewer}
import me.chadrs.gtfstools.types.{StopTimes, StopTimesFileRow, TripId}
import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Mode, OutputTimeUnit, Scope, State}
import org.parboiled2.ParserInput

object CsvLoadingBenchmarks {

  val tripSearch: TripId = TripId("49414817-DEC19-D07CAR-3_Sunday")

}

@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Array(Mode.AverageTime))
class CsvLoadingBenchmarks {

  import CsvLoadingBenchmarks._

  @Benchmark
  def loadStrictStopTimes(): Unit = doLoadStrictStopTimes()

  @Benchmark
  def parboiledcsvparser(): Unit = getCsvFile()

  @Benchmark
  def sampleparboiledcsvparser(): Unit = getCsvFilePar2Sample()

  def doLoadLazyStopTimes(): Seq[StopTimesFileRow] = {
    CsvRowViewer
      .mapFile[StopTimesFileRow](getCsvFile())
      .filter(st => st.tripId.contains(tripSearch))
  }

  def doLoadStrictStopTimes(): Seq[CsvReader.Result[StopTimes]] = {
    CsvReader
      .parseAs[StopTimes](getCsvFile())
      .filter(st => st.map(_.tripId == tripSearch).getOrElse(false))
  }

  def getCsvFilePar2Sample(): CsvFile = {
    val s = getClass.getResourceAsStream("/stop_times.txt").readAllBytes()
    val out = Parboiled2CsvParser(ParserInput(s), ",").file.run()
    CsvFile(Seq(), out.get)
  }

  def getCsvFile(): CsvFile = {
    CsvParser
      .parseFile(getClass.getResourceAsStream("/stop_times.txt").readAllBytes())
      .getOrElse(throw new Exception("need file for benchmark!"))
  }

  @Benchmark
  def univocityParser() = {
    val parser = new com.univocity.parsers.csv.CsvParser(new CsvParserSettings())
    parser.parseAll(getClass.getResourceAsStream("/stop_times.txt"))
  }

}
