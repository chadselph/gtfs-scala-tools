package me.chadrs.gtfstools.codegen

import scala.meta._
import scala.meta.Term.{ForYield, Param}
import scala.util.matching.Regex

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
  }
}

object GenerateGtfsTypes {
  import CodeGenUtil._

  val IdReferencing: Regex = """ID referencing (\w+)\.(\w+)""".r

  trait GenerateFieldType {
    def generateClass: Defn.Class
    def generateCompanion: Defn.Object
  }

  case class GenerateEnumWrapper(name: String, wraps: Type) extends GenerateFieldType {
    def generateClass =
      q"""case class ${name.toClassName}(toValue: $wraps) extends AnyVal {
        override def toString: String = {
           toValue.toString
         }
       }"""

    def generateCompanion: Defn.Object =
      q""" object ${name.toClassNameTerm} {
         implicit val csvFromString: CsvFromString[${name.toClassName}] =
          implicitly[CsvFromString[$wraps]].map(${name.toClassNameTerm}.apply)
       } """
  }
  //   implicit val routeIdFromString: CsvFromString[RouteId] = stringFromString.contramap(RouteId)
  case class GenerateIdWrapper(name: String) extends GenerateFieldType {
    def generateClass =
      q"case class ${name.toClassName}(override val toString: String) extends AnyVal"
    def generateCompanion =
      q"""object ${name.toClassNameTerm} {
            implicit val csvFromString: CsvFromString[${name.toClassName}] =
              CsvFromString.stringFromString.map(${name.toClassNameTerm}.apply)
          }"""
  }

  case class FieldParam(field: GtfsFieldSpec) {
    val name: String = CodeGenUtil.camelCase(field.name)
    val rawType: Type = determineType(field)
    val `type`: Type = if (field.required == Required) rawType else t"Option[$rawType]"
    def param: Param = Param(Nil, Term.Name(name), Some(`type`), None)
    def generator(selector: Term) = {
      val expr =
        if (field.required == Required)
          q"""$selector.required[$rawType](${Lit.String(field.name)})"""
        else
          q"""$selector.optionally[$rawType](${Lit.String(field.name)})"""
      Enumerator.Generator(Pat.Var(Term.Name(name)), expr)
    }
    def term: Term = Term.Name(name)
  }

  case class GenerateFileType(spec: GtfsFileSpec) {

    private val className = spec.filename.capitalize.replace(".txt", "")
    private val params = spec.fields.map(FieldParam).toList

    def generateClass: Defn.Class = {
      q"""case class ${className.toClassName} (..${params.map(
        _.param
      )}, extra: Map[String, String] = Map.empty)"""
    }

    def generateDocString: String = {
      // TODO: document parameters
      s"""/**
         | * A row from ${spec.filename}
         | */""".stripMargin
    }

    def generateCompanion: Defn.Object = {
      val cursor = q"c"
      val applyTerms = params.map(_.term)
      val gens = params.map(_.generator(cursor))
      val forComp = ForYield(gens, q"${className.toClassNameTerm}.apply(..$applyTerms)")
      val fieldNames = spec.fields.map(f => Lit.String(f.name)).toList
      q"""object ${className.toClassNameTerm} {
         // implicit CsvReader.
         implicit val csvReader: CsvReader[${className.toClassName}] = ($cursor: CsvCursor) => $forComp
         val Fields: Seq[String] = Seq(..$fieldNames)
       }"""
    }
  }

  def generateFieldTypes(input: Seq[GtfsFileSpec]): List[Defn] = {
    val foundFieldTypes = input
      .flatMap(_.fields)
      .collect {
        case GtfsFieldSpec(name, "Enum", _, _)
            if !name.endsWith("day") && !name.startsWith("is_") && name != "transfers" =>
          GenerateEnumWrapper(name, t"Int")
        case GtfsFieldSpec(_, IdReferencing(_, fkField), _, _)    => GenerateIdWrapper(fkField)
        case GtfsFieldSpec(name, _, _, _) if name.endsWith("_id") => GenerateIdWrapper(name)
      }
      .toSet
      .toList
    val extraFieldTypes = List(
      GenerateEnumWrapper("Longitude", t"Double"),
      GenerateEnumWrapper("Latitude", t"Double"),
      GenerateEnumWrapper("Color", t"String"),
      GenerateEnumWrapper("Language Code", t"String"),
      GenerateEnumWrapper("Currency Code", t"String")
    )
    (foundFieldTypes ++ extraFieldTypes)
      .flatMap(cls => Seq(cls.generateClass, cls.generateCompanion))
  }

  def determineType(field: GtfsFieldSpec): Type = {
    field.`type` match {
      case IdReferencing(_, fkField)         => fkField.toClassName
      case _ if field.name.endsWith("_id")   => field.name.toClassName
      case "Text" | "Email" | "Phone number" => Type.Name("String")
      case "Date"                            => Type.Select(q"java.time", Type.Name("LocalDate"))
      // TODO: LocalTime doesn't work for the weird gtfs way of saying "next day" (24:02)
      case "Time"                            => Type.Select(q"java.time", Type.Name("LocalTime"))
      case "Timezone"                        => Type.Select(q"java.time", Type.Name("ZoneId"))
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
  }

  // TODO: make pretty scaladocs for each attribute
  def generateClasses(input: Seq[GtfsFileSpec]) = {
    val fileTypes = input.map(GenerateFileType)
    q""" 
       ..${generateFieldTypes(input)}
       ..${fileTypes.map(_.generateClass).toList}
       ..${fileTypes.map(_.generateCompanion).toList}
       """.syntax
  }

  def main(args: Array[String]): Unit = {
    println(GtfsSpecParser.runParser(Spec.text).map(GenerateGtfsTypes.generateClasses).getOrElse(""))
  }

}
