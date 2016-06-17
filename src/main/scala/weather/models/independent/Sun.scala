package weather.models.independent

/** @see https://en.wikipedia.org/wiki/Solar_cycle */
final case class Sun(intensityOffset: Double)

object Sun {

  /**
   * Currently, the Sun is not modelled.
   * Thus the intensity offset is always zero.
   */
  object Constant {

    @SuppressWarnings(Array("UnusedMethodParameter"))
    def forDate(when: java.time.ZonedDateTime): Option[Sun] =
      Some(Sun(0))

  }

} // end of Sun

// vim: set ts=2 sw=2 et:
