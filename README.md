# Weather Simulator

To run a demo simulation and create fresh output with default parameters:

`sbt "run output.txt"`

A sample.txt is already provided in this repository.

To run unit test in Scala:

`sbt test`

To generate ScalaDoc:

`sbt doc`

## Demo

Demo.scala writes a text file (pipe-delimited columns) for 7 simulated days of 10 predefined stations.
The weather is incremented 4 times a day (approx 6 hours intervals, with jitter).
The jitter gives a feeling of ‘randomness’ to the simulation.
Each station is reported an average 2.2 times each day:
the Demo emits some missing, duplicated and corrupted values to simulate vagary of real data.

## Internal API

This toy model API intends to be flexible to allow for the implementation of
meteorological phenomena, but the default implementation is merely some crude
mock relationships/feedback for demo purposes.

Weather consists of a Grid and GridHistory.
Grid contains Cells, and the GridHistory contains Seq[Cells].

There are IATACodes, from which a WeatherStation can be made.
Weather can be accessed via the Grid, with WeatherStations as an alternative to latitude/longitude indexing.

WeatherOps provides a toStream/toIterator interface that leverages a Simulator,
which returns the next state of the Weather grid according to DefaultBehaviour.
Thus the weather simulation is evolved stepwise.

The current environmental model is contained in the DefaultBehaviour and Cell namespaces.

Some I/O utilities are provided for predefined Task input and output.

## Data Model

The model consists of a Grid laid out by longitude and latitude.
This determines the *spatial resolution* of the simulation.

The state of the model consists of the current grid and a collection of recent past grids (GridHistory).
The quantity of past grids determines the *smoothness* of the simulation.

Each grid Cell has a set of environmental attributes, representing features of the local Environmental Model.
Functions can be defined for these attribute, potentially taking a region of local cells as their input, and returning an updated cell as their output.
These results are accumulated to make the next state of each cell.
This determines the *sophistication* of the simulation.

The intent is that evolution functions can receive as their inputs: the current local cells, the past local cells, a time interval, and the independent cell attributes at the end of that interval (e.g. axial tilt of the earth).
They return the updated weather attributes for the current cell as their output.
This determines the *temporal resolution* and *kernel size* of the simulation.

To evolve the model, it needs to be seeded with an initial grid state plus the environmental functions.

For convenience, the API provides infinite stream and iterator interfaces,
which can be used with fixed- or variable-time intervals.
Thus the model can be stepped flexibly for analysis, display and export.

Aside:
Due to limited spatial resolution, monitoring stations placed within the same grid cell will return the same weather readings.
Avoid placing monitoring stations at extremities of the grid, due to boundary discontinuities (there is no wraparound in this model).

## Environmental and Weather Model

The model is currently very limited.
Most variation is driven by a day-night sun cycle, which is strongest at the equator and weakest at the poles.
It also adds some basic variability based on elevation.bmp and latitude.

The model environmental can contain independent/static factors, which force the dependent factors of the model weather:

* time
* solar radiation incidence (+ve for day, -ve for night)
* tilt of the earth’s axis (seasonal) - unimplemented
* height of terrain above sea level (+ve) and distribution of seas or lakes (-ve)
* static foliage (+ve), desert (zero) or ice (-ve), these don’t evolve here
* tidal height offset (moon) - unimplemented

The model environmental generates the (inter)dependent factors of the model weather:

* temperature (°C)
* pressure (hPa)
* humidity (rel%)
* wind - unimplemented
* clouds - unimplemented
* general weather condition (rain, snow, sunny)

Both independent and dependent factors must be seeded with initial conditions.
Defaults are provided in DefaultBehaviour and Cell, but you can use your own.

(Currently the simulation gets divergently inaccurate the longer it runs after the initial conditions.)

# Code style

I have mostly used standard Scala libraries so that the codebase is readable without knowledge of third-party libraries.
In a few cases I used libraries that are fairly readable without prior knowledge, like the Squants library to assist with typesafe physical dimensions.

