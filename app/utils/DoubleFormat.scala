package utils

import play.api.data.format.Formatter
import play.api.data.Mapping
import play.api.data.format.Formats._
import play.api.data.FormError
import play.api.data.Forms._
import play.api.data.validation.Constraint
import play.api.data.validation.Invalid
import play.api.data.validation.Valid
import play.api.data.validation.ValidationError
import play.api.data.validation.Constraints

//Play2.0/Scala doesn't provide a built-in double formatter for handling double input with forms
object DoubleFormat {

  /**
    * Default formatter for the `Double` type.
    */
  implicit def doubleFormat: Formatter[Double] = new Formatter[Double] {

    override val format = Some( "format.double", Nil )

    def bind( key: String, data: Map[String, String] ) = {
      stringFormat.bind( key, data ).right.flatMap { s =>
        scala.util.control.Exception.allCatch[Double]
          .either( java.lang.Double.parseDouble( s ) )
          .left.map( e => Seq( FormError( key, "error.double", Nil ) ) )
      }
    }

    def unbind( key: String, value: Double ) = Map( key -> value.toString )
  }

  /**
    * Constructs a simple mapping for a double field.
    *
    * For example:
    * {{{
    * Form("quantity" -> double)
    * }}}
    */
  val double: Mapping[Double] = of[Double]

  def double( min: Double = Double.MinValue, max: Double = Double.MaxValue ): Mapping[Double] = ( min, max ) match {
    case ( Double.MinValue, Double.MaxValue ) => double
    case ( min, Double.MaxValue )             => double verifying DoubleFormat.min( min )
    case ( Double.MinValue, max )             => double verifying DoubleFormat.max( max )
    case ( min, max )                         => double verifying ( DoubleFormat.min( min ), DoubleFormat.max( max ) )
  }

  /**
    * Defines a minimum value for `Double` values, i.e. the value must be greater than or equal to the constraint parameter
    *
    * '''name'''[constraint.min(minValue)]
    * '''error'''[error.min(minValue)]
    */
  def min( minValue: Double ): Constraint[Double] = Constraint[Double]( "constraint.min", minValue ) { o =>
    if ( o >= minValue ) Valid else Invalid( ValidationError( "error.min", minValue ) )
  }

  /**
    * Defines a maximum value constraint for `Double` values, i.e. value must be less than or equal to the constraint parameter
    *
    * '''name'''[constraint.max(maxValue)]
    * '''error'''[error.max(maxValue)]
    */
  def max( maxValue: Double ): Constraint[Double] = Constraint[Double]( "constraint.max", maxValue ) { o =>
    if ( o <= maxValue ) Valid else Invalid( ValidationError( "error.max", maxValue ) )
  }

}

// vim: set ts=4 sw=4 et:
