package nicol
package examples.pong

import input.Key._
import input.Mouse
import geom._
import math._

object App extends Game(Init("Nicol Test", 800, 600) >> Main)

object Main extends GameScene with SyncableScene with StandardRenderer {
  scene =>

  var points = 0

  def update = {
    val my = Mouse.apply._2

    Ball.update

    val paddle1 = AABox(100-25, my - 25, 10, 50)
    val paddle2 = AABox(700-25, my - 25, 10, 50)

    if (paddle1.intersects(Ball.bounds) || paddle2.intersects(Ball.bounds)) {
      Ball.bounce
      Ball.v = (Ball.v._1 * 1.5f, Ball.v._2 * 1.5f)
      points += 10
    }

    draw(points.toString, position = (300, 10))(ArialCenderedStringRenderer)

    draw(paddle1, rgb = (1, 1, 1))(FilledAABoxRenderer)
    draw(paddle2, rgb = (1, 1, 1))(FilledAABoxRenderer)

    if (Ball.pos.x < 0 || Ball.pos.x > 800) Ball.reset

    sync
    if (escape) End else None
  }

  object Ball {
    val circle = Circle((0, 0), 10)

    import scala.util.Random._

    var pos = Vector(400, 300)
    var v = (if (nextFloat - 0.5 < 0) -1f else 1f, nextFloat * 2 - 1)

    def reset = {
      pos = Vector(400, 300)
      v = (if (nextFloat - 0.5 < 0) -1f else 1f, nextFloat * 2 - 1)
    }

    def bounds = circle.transposed(pos.x, pos.y).bounds

    def bounce = {
      v = (-v._1, v._2)
    }

    def update = {
      pos += v
      if (Ball.pos.y < circle.radius || Ball.pos.y > 600 - circle.radius) v = (v._1, -v._2)
      scene.draw(circle, position = pos, rgb = (1, 0, 0))(FilledCircleRenderer)
    }
  }

}
