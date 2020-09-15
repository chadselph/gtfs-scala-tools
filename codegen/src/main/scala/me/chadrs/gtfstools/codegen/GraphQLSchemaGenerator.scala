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
  ) {

    /**
     * If, for example, [[file]] has a field like "route_id", this adds a field caleld "route"
     * that resolves the corresponding route row from that file.
     */
    def toJoinField = {
      val (name, isList) = field match {
        case "shape_id" =>
          // shape_id is a special case where we have a one-to-many join
          ("shape", true)
        case "parent_station" =>
          // hack to avoid name collision
          ("parent_station_stop", false)
        case _ =>
          (field.stripSuffix("_id"), false)
      }
      val listGetter: Term =
        q"""t.ctx.filterFileBy[${referencedFile.fileRowCls}](
         ${referencedFile.toLitString},
         ${referencedField.toLitString},
         t.value.toMap.apply(${field})
         ).getOrElse(Nil)"""
      val (fieldType, getter) =
        if (isList) (q"ListType(${referencedFile.toSchemaVarName})", q"t => $listGetter")
        else (q"OptionType(${referencedFile.toSchemaVarName})", q"t => $listGetter.headOption")
      q"""Field($name, $fieldType, Some(${Lit.String(
        fieldSpec.description
      )}), resolve = $getter) """
    }

    /**
     * If, for example [[file]] has a field like "route_id", this adds a field on routes that
     * will get all the corresponding rows from [[file]]. This make it possible to do queries like
     *
     * route("38") {
     *   trips {
     *    ..
     *   }
     * }
     *
     */
    def toReverseJoinField = {
      val name =
        if (field == referencedField) Lit.String(file.stripSuffix(".txt"))
        // disambiguate when we have two references to the same file in the same class
        else Lit.String(field.stripSuffix("id") + file.stripSuffix(".txt"))
      val fieldType = file.toSchemaVarName
      // TODO: handle t.value.toMap(ref.field) doesn't have a value
      val description =
        s"Rows from $file where $field matches this object's $referencedField.".toLitString
      val getter: Term =
        q""" t => t.ctx.filterFileBy[${file.fileRowCls}](
         ${file.toLitString},
         ${field.toLitString},
         t.value.toMap.apply($referencedField)
         ).getOrElse(Nil)"""
      q"""Field($name, ListType($fieldType), Some($description), resolve = $getter) """
    }

  }

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
      spec.fields.map(forField).toList ++ referencing.map(_.toJoinField) ++ referencedBy.map(
        _.toReverseJoinField
      )
    val code =
      q"""implicit lazy val $schemaVarName: ObjectType[GtfsContext, ${spec.filename.fileRowCls}]  = ObjectType(
         $schemaName, $description, () => fields[GtfsContext, ${spec.filename.fileRowCls}](
           ..$fields
         ))"""
    code.toString()
  }

  def forField(field: GtfsFieldSpec): Term = {
    // TODO: actually determine type
    // TODO: enums for enum types
    val name = Lit.String(field.name)
    val getter: Term = q""" _.value.toMap.get($name) """
    q"""Field($name, OptionType(StringType), Some(${Lit.String(
      field.description
    )}), resolve = $getter) """
  }

}
