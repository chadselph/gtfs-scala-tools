package me.chadrs.gtfstools.codegen
import scala.meta._
import CodeGenUtil._
import ScalaLazyParsingGtfsGenerator.FileRowStringOps

object GraphQLSchemaGenerator extends GtfsTypesGenerator {
  override def generateClasses(input: Seq[GtfsFileSpec]): String = {
    input.map(forFileType).mkString("\n")
  }

  def forFileType(spec: GtfsFileSpec): String = {
    val schemaName = Lit.String(camelCase(spec.filename.replace(".txt", "")))
    val schemaVarName = Pat.Var(Term.Name(schemaName.value + "Schema"))
    val description = Lit.String(spec.description)
    val fields = spec.fields.map(forField).toList
    val code =
      q"""implicit val $schemaVarName = ObjectType(
         $schemaName, $description, fields[Unit, ${spec.filename.fileRowCls}](
           ..$fields
         ))"""
    code.toString()
  }

  def forField(field: GtfsFieldSpec): Term = {
    // TODO: actually determine type
    // TODO: enums for enum types
    // TODO: foreign keys
    val name = Lit.String(field.name)
    val getter: Term = q""" _.value.toMap.get($name) """
    q""" Field($name, OptionalType(StringType), Some(${Lit.String(
      field.description
    )}), resolve = $getter) """
  }

}
