import sbt._

import Keys._
import Defaults.defaultSettings

object NicalExamples extends Build {

  val generalSettings = defaultSettings ++ Nicol.engineSettings ++ Seq (
    organization := "com.github.philcali",
    version := "0.1.0"
  )

  def genProject(name: String) = Project(
    name,
    file(name),
    settings = generalSettings ++ Seq (
      mainClass in (Compile, run) := Some("nicol.examples.%s.App".format(name)) 
    )
  ) 

  lazy val examples = Project (
    "nicol-examples",
    file("."),
    settings = generalSettings
  ) 

  lazy val breakout = genProject("breakout")
  lazy val showcaser = genProject("showcaser")
  
  lazy val pong = genProject("pong")
  lazy val squares = genProject("squares")
}
