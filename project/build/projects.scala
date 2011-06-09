import sbt._

class NicolExamples(info: ProjectInfo) extends ParentProject(info) with IdeaProject {

  class NicolExample(info: ProjectInfo) extends LWJGLProject(info) with Nicol {
    override def nicolVersion = "0.1.0.1"

    override def mainClass = Some("nicol.examples.%s.App".format(name))
  }

  // Convenience for building projects
  def genProject(name: String) = project(name, name, new NicolExample(_) with IdeaProject)

  lazy val tictactoe = genProject("breakout")
  lazy val showcaser = genProject("showcaser")

  lazy val pong = genProject("pong")
  lazy val squares = genProject("squares")
}
