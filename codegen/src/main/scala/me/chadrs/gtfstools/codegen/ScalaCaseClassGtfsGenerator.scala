package me.chadrs.gtfstools.codegen

import scala.meta._
import scala.meta.Term.{ForYield, Param}
import scala.util.matching.Regex


object ScalaCaseClassGtfsGenerator extends GtfsTypesGenerator {
  import CodeGenUtil._

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
    val name: String = camelCase(field.name)
    val (`type`: Type, rawType: Type) = determineType(field)
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


  // TODO: make pretty scaladocs for each attribute
  def generateClasses(input: Seq[GtfsFileSpec]) = {
    val fileTypes = input.map(GenerateFileType)
    q""" 
       ..${generateFieldTypes(input)}
       ..${fileTypes.map(_.generateClass).toList}
       ..${fileTypes.map(_.generateCompanion).toList}
       """.syntax
  }
}
