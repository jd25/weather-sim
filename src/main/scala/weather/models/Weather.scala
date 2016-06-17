package weather.models

/**
 * An iterable model. See [[WeatherOps]] for operations and [[DefaultBehaviour]] for
 * default behavioural logic.
 */
final case class Weather(grid: Grid, history: GridHistory,
  independentLogic: Set[Simulator.IndependentLogic],
  dependentLogic: Seq[Simulator.DependentLogic],
  randomness: util.Random)

/** @see [[DefaultBehaviour]] */
object Weather {

  /**
   * Make an evolving weather model from a grid’s initial conditions
   * only (no historical data).
   * @see [[DefaultBehaviour]] for implicits.
   */
  def ofGrid(grid: Grid)(implicit
    independentLogic: Set[Simulator.IndependentLogic],
    dependentLogic: Seq[Simulator.DependentLogic],
    randomness: util.Random): Weather = {
    val history = grid.cells.map(_.map(Vector(_)))
    Weather(grid, GridHistory(history), independentLogic, dependentLogic, randomness)
  }

} // end of Weather

// vim: set ts=2 sw=2 et:
