package me.chadrs.gtfstools.cli

import com.jakewharton.fliptables.FlipTable

import scala.reflect.ClassTag

object TablePrinter {

  private val toStringHideOptions: PartialFunction[Any, String] = {
    case Some(x) => x.toString
    case None    => ""
    case a: Any  => a.toString
  }

  /**
   * Print a table of case classes or tuples
   * TODO: remove fliptable
   */
  def printTable[T <: Product: ClassTag](
      headers: Seq[String],
      s: Seq[T],
      includedOnly: List[String]
  ) = {
    if (includedOnly.isEmpty) {
      // default include all
      FlipTable.of(
        headers.toArray,
        s.toArray.map(t => t.productIterator.toArray.map(toStringHideOptions))
      )
    } else {
      val includeIndices = includedOnly.map(h => headers.indexOf(h))
      FlipTable.of(includedOnly.toArray, s.toArray.map { t =>
        val a = t.productIterator.toArray
        includeIndices.map(a(_)).map(toStringHideOptions).toArray
      })
    }

  }

}
