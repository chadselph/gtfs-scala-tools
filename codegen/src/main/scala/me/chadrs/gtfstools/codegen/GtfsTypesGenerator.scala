package me.chadrs.gtfstools.codegen

import scala.meta.{Term, Type}
import scala.util.matching.Regex
import scala.meta._

object CodeGenUtil {
  /*
    Converts from a_name_like_this to aNameLikeThis
   */
  def camelCase(snake_string: String): String = {
    val first :: rest = snake_string.toLowerCase.replace(" ", "_").split("_").toList
    first + rest.map(_.capitalize).mkString("")
  }

  /*
    Converts from some_name_like_this to SomeNameLikeThis
   */
  def PascalCase(snake_string: String): String = camelCase(snake_string).capitalize

  implicit class StringOps(val s: String) extends AnyVal {
    def toClassName: Type.Name = Type.Name(PascalCase(s))
    def toClassNameTerm: Term.Name = Term.Name(PascalCase(s))
    def toClassNameType: Type.Name = Type.Name(PascalCase(s))
    def toMethodNameTerm: Term.Name = Term.Name(camelCase(s))
  }
}

trait GtfsTypesGenerator {

  trait GenerateFieldType {
    def generateClass: Defn.Class
    def generateCompanion: Defn.Object
  }

  import CodeGenUtil._

  val IdReferencing: Regex = """ID referencing (\w+)\.(\w+)""".r

  /**
   * @return for required params, the same type twice. For optional, first
   *         Option[ReturnType] and then ReturnType
   */
  def determineType(field: GtfsFieldSpec): (Type, Type) = {
    val rawType = field.`type` match {
      case IdReferencing(_, fkField)         => fkField.toClassName
      case _ if field.name.endsWith("_id")   => field.name.toClassName
      case "Text" | "Email" | "Phone number" => Type.Name("String")
      case "Date"                            => Type.Select(q"java.time", Type.Name("LocalDate"))
      // LocalTime doesn't work for the weird gtfs way of saying "next day" (24:02)
      case "Time"     => t"Time"
      case "Timezone" => Type.Select(q"java.time", Type.Name("ZoneId"))
      case "Float" | "Positive Float" | "Non-negative float" | "Non-negative Float" =>
        Type.Name("Double")
      case "Non-negative integer" | "Non-null Integer" | "Positive Integer" => t"Int"
      case "URL"                                                            => Type.Select(q"java.net", t"URI")
      case "Enum" =>
        field.name match {
          case _ if field.name == "transfers"    => t"String" // TODO: transfer type
          case _ if field.name.startsWith("is_") => t"Boolean"
          case _ if field.name.endsWith("day")   => t"Boolean"
          case _                                 => field.name.toClassName
        }
      case keep @ ("Latitude" | "Longitude" | "Language code" | "Color" | "Currency code") =>
        keep.toClassName
      case _other => t"String"
    }
    (if (field.required == Required) rawType else t"Option[$rawType]", rawType)
  }

  def generateClasses(input: Seq[GtfsFileSpec]): String

  def main(args: Array[String]): Unit = {
    println(
      GtfsSpecParser
        .runParser(Spec.text)
        .map(generateClasses)
        .fold(err => throw new Exception(err), identity)
    )
  }

}
