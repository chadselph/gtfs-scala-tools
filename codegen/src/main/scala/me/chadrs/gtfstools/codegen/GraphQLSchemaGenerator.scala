package me.chadrs.gtfstools.codegen
import scala.meta._
import CodeGenUtil._
import ScalaLazyParsingGtfsGenerator.FileRowStringOps
import me.chadrs.gtfstools.codegen.GraphQLSchemaGenerator.Connections

object PrintConnections extends GtfsTypesGenerator {
  override def generateClasses(input: Seq[GtfsFileSpec]): String =
    Connections(input).connections
      .map(c => s"${c.field} in ${c.file} => ${c.referencedField} in ${c.referencedFile}")
      .mkString("\n")
}
object GraphQLSchemaGenerator extends GtfsTypesGenerator {

  implicit class SchemaNaming(val s: String) extends AnyVal {
    def toSchemaName: String = camelCase(s.stripSuffix(".txt"))
    def toSchemaVarName: Term.Name = Term.Name(toSchemaName + "Schema")
    def toSchemaLitString: Lit.String = Lit.String(toSchemaName)
    def toLitString: Lit.String = Lit.String(s)
  }

  case class ForeignRef(
      field: String,
      file: String,
      referencedFile: String,
      referencedField: String,
      fieldSpec: GtfsFieldSpec
  )

  case class Connections(input: Seq[GtfsFileSpec]) {
    val connections = input.flatMap(
      file =>
        file.fields.collect {
          case f @ GtfsFieldSpec(name, IdReferencing(fkTable, fkField), _, _) =>
            ForeignRef(name, file.filename, fkTable + ".txt", fkField, f)
        }
    )

    def getForeignRefs(f: GtfsFileSpec): Seq[ForeignRef] = {
      connections.filter(_.file == f.filename)
    }

    def getForeignRefedBy(f: GtfsFileSpec): Seq[ForeignRef] = {
      connections
        .filter(_.referencedFile == f.filename)
        .filter(_.file != f.filename) // exclude 2nd instance of circular references
    }
  }

  override def generateClasses(input: Seq[GtfsFileSpec]): String = {
    val connections = Connections(input)
    input
      .map(t => forFileType(t, connections.getForeignRefedBy(t), connections.getForeignRefs(t)))
      .mkString("\n")
  }

  def forFileType(
      spec: GtfsFileSpec,
      referencedBy: Seq[ForeignRef],
      referencing: Seq[ForeignRef]
  ): String = {
    val schemaName = spec.filename.toSchemaLitString
    val schemaVarName = Pat.Var(spec.filename.toSchemaVarName)
    val description = Lit.String(spec.description)
    val fields =
      spec.fields.map(forField).toList ++ referencing.map(foreignKey) ++ referencedBy.map(oneToMany)
    val code =
      q"""implicit lazy val $schemaVarName: ObjectType[GtfsRepo, ${spec.filename.fileRowCls}]  = ObjectType(
         $schemaName, $description, () => fields[GtfsRepo, ${spec.filename.fileRowCls}](
           ..$fields
         ))"""
    code.toString()
  }

  def foreignKey(ref: ForeignRef): Term = {
    val name =
      if (ref.field == "parent_station")
        Lit.String("parent_station_stop") // hack for naming conflict
      else Lit.String(ref.field.stripSuffix("_id"))
    val fieldType = ref.referencedFile.toSchemaVarName
    // TODO: handle t.value.toMap(ref.field) doesn't have a value
    val getter: Term =
      q""" t => t.ctx.parseAndFilter[${ref.referencedFile.fileRowCls}](
         ${ref.referencedFile.toLitString},
         ${ref.referencedField.toLitString},
         t.value.toMap.apply(${ref.field})
         ).getOrElse(Nil).headOption"""
    q"""Field($name, OptionType($fieldType), Some(${Lit.String(
      ref.fieldSpec.description
    )}), resolve = $getter) """
  }

  // Get the reverse of the references, for example so we can do routes { trips { ... } }
  def oneToMany(ref: ForeignRef): Term = {
    val name =
      if (ref.field == ref.referencedField) Lit.String(ref.file.stripSuffix(".txt"))
      // disambiguate when we have two references to the same file in the same class
      else Lit.String(ref.field.stripSuffix("id") + ref.file.stripSuffix(".txt"))
    val fieldType = ref.file.toSchemaVarName
    // TODO: handle t.value.toMap(ref.field) doesn't have a value
    val description =
      s"Rows from ${ref.file} where ${ref.field} matches this object's ${ref.referencedField}.".toLitString
    val getter: Term =
      q""" t => t.ctx.parseAndFilter[${ref.file.fileRowCls}](
         ${ref.file.toLitString},
         ${ref.field.toLitString},
         t.value.toMap.apply(${ref.referencedField})
         ).getOrElse(Nil)"""
    q"""Field($name, ListType($fieldType), Some($description), resolve = $getter) """

  }

  def forField(field: GtfsFieldSpec): Term = {
    // TODO: actually determine type
    // TODO: enums for enum types
    // TODO: foreign keys
    val name = Lit.String(field.name)
    val getter: Term = q""" _.value.toMap.get($name) """
    q"""Field($name, OptionType(StringType), Some(${Lit.String(
      field.description
    )}), resolve = $getter) """
  }

}
