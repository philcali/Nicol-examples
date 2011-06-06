package nicol
package examples.squares

import input._
import math._
import geom._

object App extends Game(Init("Squares", 500, 600) >> Main.Paused)

object Main {
  var points = 0

  import Key._

  object Paused extends GameScene with SyncableScene with StandardRenderer {
    scene =>

    def update = {
      draw(points.toString, position = (250, 10))(ArialCenderedStringRenderer)
      draw("Click for start", position = (250, 300))(ArialCenderedStringRenderer)

      sync

      if (Mouse(Mouse.Left)) {
        new Play
      } else if (escape) {
        End
      } else {
        None
      }
    }
  }

  import util.Random._

  class Play extends GameScene with SyncableScene with StandardRenderer {
    scene =>

    points = 0
    var ticks = 0

    var squares: Seq[Square] = Seq.fill(10)(new Goodie) ++ Seq.fill(5)(new Baddie)

    Player.reset

    def update = {
      var end = false
      ticks += 1

      if (ticks % 60 == 0) squares +:= new Goodie
      if (ticks % 100 == 0) squares +:= new Baddie

      squares.foreach(s => {
        if (s.intersects(Player)) {
          s match {
            case g: Goodie => points += 20 - g.size
            case b: Baddie => end = true
          }
          s.reset
        }
        s.update
      })
      Player.update

      squares.foreach(_.draw)

      Player.draw

      draw(points.toString, position = (250, 10))(ArialCenderedStringRenderer)
      sync
      if (escape || end) Paused else None
    }


    trait Direction {
      val factor: Vector
    }

    object Direction {
      def random = nextInt(4) match {
        case 0 => Up
        case 1 => Down
        case 2 => Left
        case 3 => Right
      }
    }

    case object Up extends Direction {
      val factor = Vector.up
    }

    case object Down extends Direction {
      val factor = Vector.down
    }

    case object Left extends Direction {
      val factor = Vector.left
    }

    case object Right extends Direction {
      val factor = Vector.right
    }

    abstract class Square(val size: Int, val direction: Direction, rgb: (Float, Float, Float)) {
      var pos: Vector = Vector.zero
      reset

      def reset = direction match {
        case Up => pos = Vector(nextFloat * 500, 600 + size)
        case Down => pos = Vector(nextFloat * 500, -size)
        case Left => pos = Vector(500 + size, nextFloat * 600)
        case Right => pos = Vector(-size, nextFloat * 600)
      }

      def bounds = AABox(pos.x - size, pos.y - size, size, size)

      def update = {
        pos += direction.factor * (20 / size.toFloat)
        direction match {
          case Up => if (pos.y < 0) reset
          case Down => if (pos.y > 600) reset
          case Left => if (pos.x < 0) reset
          case Right => if (pos.x > 500) reset
        }
      }

      def draw = scene.draw(bounds, rgb = this.rgb)(FilledAABoxRenderer)

      def intersects(that: Square) = bounds.intersects(that.bounds)
    }

    class Goodie extends Square(nextInt(10) + 5, Direction.random, (0, 1, 0))

    class Baddie extends Square(nextInt(10) + 5, Direction.random, (1, 0, 0))

    object Player extends Square(20, Up, (0, 0, 1)) {
      override def reset = {
        pos = Vector(250, 300)
      }

      override def update = {
        val (mx, my) = Mouse.apply
        pos = Vector(mx, my)
      }
    }

  }

}