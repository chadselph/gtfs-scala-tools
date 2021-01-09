package me.chadrs.gtfstools.codegen
import scala.meta._
import ScalaLazyParsingGtfsGenerator.FileRowStringOps

object ScalaCatsValidatorsGenerator extends GtfsTypesGenerator {

  import CodeGenUtil._

  override def generateClasses(input: Seq[GtfsFileSpec]): String = {
    input
      .map { spec =>
        val filename = spec.filename.stripSuffix(".txt")
        val fieldTerms = spec.fields.toList.map(_.name.toMethodNameTerm)
        q"""def ${filename.toMethodNameTerm}(csv: ${spec.filename.fileRowCls}): ValidationResult[${filename.toClassNameType}] = {
            (..${fieldTerms.map(generateValidated)}).mapN {
              case (..${fieldTerms.map(Pat.Var(_))})=>
                ${filename.toClassNameTerm}(..$fieldTerms)
            }
          }"""
          .toString()
      }
      .mkString("\n")
  }

  def generateValidated(field: Term.Name) = {
    q"""csv.$field.toValidatedNec"""
  }
}
