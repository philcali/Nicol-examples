package nicol
package examples.showcaser 

import input.Key._
import input.Mouse
import geom._
import math._
import scala.math.{sin, cos, abs, Pi}

object App extends Game(Init("Nicol Test", 800, 600) >> Main) 

object Main extends GameScene with SyncableScene with StandardRenderer {
  var textColor = (1f, 1f, 1f)

  val (x, y) = (400, 300)

  val increment = 0.05f

  def update = {
    val (mx, my) = Mouse.apply   
    val d = (Vector(mx, my).angle((x, y))) - (Pi).toFloat

    draw("Hello Nicol Test", position = (350, 300), rgb = textColor)
    draw("Press (Esc) to Exit", position = (325, 150))

    val (px, py) = (mx, abs(my - 600))
    val box = AABox(px - 25, py - 15, 50, 30)
    draw(box, rgb = (10, 250, 10))

    val spinner = Circle((
      x + (100 * cos(d)).toFloat,
      y + (100 * sin(d)).toFloat
    ), 10)

    draw(spinner, rgb = (250, 250, 210))(FilledCircleRenderer)

    if(spinner.bounds.collides((px.toInt, py.toInt))) {
      draw("Bang!", position = (375, 100), rgb = (1f, 0, 0))
    } else {
      draw("Hit the ball!", position = (350, 100))
    }

    textColor = textColor match {
      case (r, 1f, 1f) if r > 0f => (r - increment, 1f, 1f)
      case (_, g, 1f) if g > 0f => (0f, g - increment, 1f)
      case (0f, _, b) if b > 0f => (0f, 0f, b - increment)
      case (r, g, b) if r > 1f => (1f, 1f, 1f)
      case (r, g, b) => (r + increment, g + increment, b + increment)
    }
   
    sync
    if (escape) End else None
  }
}
