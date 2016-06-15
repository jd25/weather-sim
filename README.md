# Weather Sim

## Environmental model

This model consists of grid arranged by longitude and latitude.
(Thus, monitoring stations placed within the same grid cell will return the same results.)
Avoid placing monitoring stations at the edges of the longitude/latitude grid due to boundary discontinuities (no wraparound in this model).

There are independent and static factors, which force the dependent factors of the model:

* time
* solar radiation incidence (+ve for day, -ve for night)
* tilt of the earth’s axis (seasonal)
* height of terrain above sea level (+ve) and distribution of seas or lakes (-ve)
* static foliage (+ve), desert (zero) or ice (-ve), these don’t evolve
* tidal height offset (moon)

The (inter)dependent factors are:

* temperature (°C)
* pressure (hPa)
* humidity (rel%)
* wind
* clouds
* general weather condition (rain, snow, sunny)

Both independent and dependent factors must be seeded with initial conditions.
Defaults are provided, but you can change these.

Obviously, the simulation gets progressively more inaccurate the longer it runs after the initial conditions.

# Code style

I have mostly used standard Scala libraries so that the codebase is readable without knowledge of third-party libraries.
In a few cases I used libraries that are fairly readable without prior knowledge, like the Squants library to assist with typesafe physical dimensions.

