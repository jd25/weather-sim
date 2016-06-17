package weather.models.dependent

sealed abstract class Condition {
  def toString: String
}

/** Pre-defined weather conditions like Sunny. */
object Condition {

  /** Unconverged state. */
  case object Unknown extends Condition

  case object Cold extends Condition

  case object Mild extends Condition

  case object Cloudy extends Condition

  case object Sunny extends Condition

  case object Hot extends Condition

  case object Humid extends Condition

  case object Dry extends Condition

  case object Rain extends Condition

  case object Snow extends Condition

} // end of Condition

// vim: set ts=2 sw=2 et:
