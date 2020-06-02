package me.chadrs.gtfstools.codegen
import scala.meta._

/**
 * Generates classes that wrap a raw csv row (IndexSeq of Strings)
 * and don't do any validation until the last possible moment.
 * The idea is that if you never even look at a field, you might not
 * care it has an invalid value.
 */
object ScalaLazyParsingGtfsGenerator extends GtfsTypesGenerator {
  import CodeGenUtil._

  implicit class FileRowStringOps(val s: String) extends AnyVal {
    def fileRowCls: Type.Name = s.replace(".txt", "_file_row").toClassName
    def fileRowClsTerm: Term.Name = s.replace(".txt", "_file_row").toClassNameTerm
  }

  def generateGetter(gtfsFieldSpec: GtfsFieldSpec): Defn.Val = {
    val fieldName = Term.Name(camelCase(gtfsFieldSpec.name))
    val (fieldType, rawFieldType) = determineType(gtfsFieldSpec)
    val fieldNameLit = Lit.String(gtfsFieldSpec.name)
    val impl =
      if (gtfsFieldSpec.required == Required)
        q"""cursor.required[$fieldType]($fieldNameLit)"""
      else q"""cursor.optionally[$rawFieldType]($fieldNameLit)"""

    val returnType = t"Either[String, $fieldType]"
    val pattern = Pat.Var(fieldName)
    q"""lazy val $pattern: $returnType = $impl"""
  }

  def generateLazyClass(gtfsFileSpec: GtfsFileSpec): Defn.Class = {
    q"""
        class ${gtfsFileSpec.filename.fileRowCls}(
          cursor: me.chadrs.gtfstools.csv.CsvCursor
        ) {
          lazy val toMap: Map[String, String] = cursor.toMap

          def get(field: String): Option[String] = toMap.get(field)

          ..${gtfsFileSpec.fields.map(generateGetter).toList}
        }
       """
  }

  def generateCompanionObject(gtfsFileSpec: GtfsFileSpec): Defn.Object = {
    q"""
        object ${gtfsFileSpec.filename.fileRowClsTerm} {

          implicit val csvReader: CsvRowViewer[${gtfsFileSpec.filename.fileRowCls}] =
            (cursor: CsvCursor) => new ${gtfsFileSpec.filename.fileRowCls}(cursor)
        }
       """
  }

  override def generateClasses(input: Seq[GtfsFileSpec]): String = {
    q"""
    ..${input.map(generateLazyClass).toList}
    ..${input.map(generateCompanionObject).toList}
       """.syntax
  }
}
