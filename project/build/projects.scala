import sbt._

class NicolExamples(info: ProjectInfo) extends ParentProject(info) {

  class NicolExample(info: ProjectInfo) extends LWJGLProject(info) with Nicol {
    override def mainClass = Some("nicol.examples.%s.App".format(name))
  }

  // Convenience for building projects
  def genProject(name: String) = project(name, name, new NicolExample(_))

  lazy val tictactoe = genProject("tictactoe") 
}
