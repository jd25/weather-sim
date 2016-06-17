package weather.models

import java.time.Duration

/**
 * The heart of the Weather Simulator. Note, strict Durations are used
 * here, which will impart a gradual drift to the simulation when there
 * are leap seconds etc (this was intended to add some natural noise to
 * the simulation).
 * @see [[DefaultBehaviour]] for demo logic.
 * @todo expanding logic to include randomness.
 */
object Simulator {

  type IndependentLogic = (Cell, Duration) => Cell
  type DependentLogic = (Cell, Duration, GridHistory, Grid) => Cell

  def simulate(weather: Weather, interval: Duration): Weather =
    if (interval.isZero || interval.isNegative) weather else next(weather, interval)

  /**
   * Main workhorse. The independent environment logic is applied first.
   * Its output is then fed to the dependent weather logic.
   * @todo implement GridHistory!!!!
   */
  protected def next(weather: Weather, interval: Duration): Weather = {
    val independentLogic = applyIndependentLogic(weather.independentLogic, interval) _
    // val step = weather.grid.cells.par.map(_.map(independentLogic)).seq
    val step = weather.grid.cells.map(_.map(independentLogic))

    val dependentLogic = applyDependentLogic(weather.dependentLogic, interval) _
    val cells = step.map(_.map(dependentLogic(_, GridHistory.empty/*@todo*/, Grid(step))))

    weather.copy(grid = Grid(cells))
  }

  protected[weather] def applyIndependentLogic(logic: TraversableOnce[IndependentLogic], interval: Duration)(cell: Cell): Cell =
    logic.foldLeft(cell) { case (cell, logic) => logic(cell, interval) }

  protected[weather] def applyDependentLogic(logic: TraversableOnce[DependentLogic], interval: Duration)(cell: Cell, history: GridHistory, future: Grid): Cell =
    logic.foldLeft(cell) { case (cell, logic) => logic(cell, interval, history, future) }

} // end of Simulator

// vim: set ts=2 sw=2 et:
