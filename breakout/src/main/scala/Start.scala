package nicol
package examples.breakout

import input.Key._
import math._
import geom._

import scala.math.{abs, min}

object Start extends FriendlyScene { 
  scene =>

  object World {
    val grey = (0.8f, 0.8f, 0.8f)

    val leftSide = AABox(0, 0, 25, 600)
    val rightSide = AABox(525, 0, 275, 600)
    val topSide = AABox(25, 0, 525, 25) 

    def draw = {
      scene.draw(leftSide, rgb = grey)(FilledAABoxRenderer)
      scene.draw(rightSide, rgb = grey)(FilledAABoxRenderer)
      scene.draw(topSide, rgb = grey)(FilledAABoxRenderer)
    } 
  }

  object Paddle {
    var x = 225f
    var y = 550f

    var speed = 7f

    def paddle = { 
      val center = AABox(x, y, 60, 20)
      val leftSide = Circle((x, y + 10), 10)
      val rightSide = Circle((x + 60, y + 10), 10)
      (leftSide, center, rightSide)
    }

    def inBounds = {
      val (l, c, r) = paddle
      !l.bounds.intersects(World.leftSide) &&
      !r.bounds.intersects(World.rightSide)
    }

    def draw = {
      val (l, c, r) = paddle
      scene.draw(l, rgb = (0.5f, 0.5f, 0))(FilledCircleRenderer)
      scene.draw(r, rgb = (0.5f, 0.5f, 0))(FilledCircleRenderer)
      scene.draw(c, rgb = (0.5f, 1f, 0.3f))(FilledAABoxRenderer)
    }
  }

  object Ball {
    var atRest = true
    var x = Paddle.x + 30
    var y = Paddle.y - 8
    
    var speed = 4f
    var v: Vector = Vector(0, -1)

    def init = {
      atRest = true
      x = Paddle.x + 30
      y = Paddle.y - 8
    } 

    def ball = Circle((x, y), 10)

    def draw = {
      scene.draw(ball)(FilledCircleRenderer)
    }

    def fire = {
      v = Vector(1.5f, -1.5f)
      y = 537
      atRest = false
    }

    def checkBall(b: Circle): Vector = {
      if (b.bounds.intersects(World.topSide)) {
        (v.x, v.y * -1)
      } else if (b.bounds.intersects(World.leftSide) || 
                 b.bounds.intersects(World.rightSide)) {
        (v.x * -1, v.y)
      } else if (b.bounds.intersects(Paddle.paddle._2)) {
        val center = Paddle.x + 30
        val diff = x - center
        val mag = if (diff == 0) 0 
          else if (diff > 0) 0.5f + diff / 15
          else -0.5f + diff / 15
        (mag, v.y * -1)
      } else {
        v
      }
    }

    def update = if (!atRest) {
      val b = ball
      v = checkBall(ball)

      val ballbounds = b.bounds
      v = Bricks.bricks.find(br => ballbounds.intersects(br.box)) match {
        case Some(brick) => 
          Bricks.bricks -= brick
          val box = brick.box

          // Where did the ball strike? 
          // I'm sure there's a better way to do this 
          val points = List(
            (ballbounds.left, ballbounds.top),
            (ballbounds.right, ballbounds.top),
            (ballbounds.left, ballbounds.bottom),
            (ballbounds.right, ballbounds.bottom)
          )

          // Found out which points intersected
          val contained = points.filter { p =>
            val (px, py) = p
            (px >= box.left && px <= box.right) && (py >= box.top && py <= box.bottom)
          }

          // If there are two points, then it was a "flat" hit...
          // Flat hits just reverse the magnitude
          val s = contained.size
          if (s > 1) {
            // left or right side means horizontal shift
            val point = contained.head
            val lrs = contained.foldLeft(true)(_ && _._1 == point._1)
            if (lrs) (v.x * -1, v.y) else (v.x, v.y * -1)
          } else {
            val (px, py) = contained.head
            val fromX = min(abs(px - box.left), abs(px - box.right))
            val fromY = min(abs(py - box.top), abs(py - box.bottom))

            if (min(fromX, fromY) == fromX) (v.x * -1, v.y) else (v.x, v.y * -1) 
          }
          
          //(v.x, v.y * -1)
        case None => v
      }

      val mag = v * speed

      y = y + mag.y 
      x = x + mag.x
      
      if (y > 600) init 
    }
  }

  class Brick(pos: (Float, Float), 
              color: (Float, Float, Float) = (0.5f, 0, 0)) {
    val (x, y) = pos
    val box = AABox(x, y, 49, 24)
    
    def draw = {
      scene.draw(box, rgb = color)(FilledAABoxRenderer)
    }  
  }

  object Bricks {
    val bricks = collection.mutable.ListBuffer[Brick]()

    init   

    def init {
      (0 until 10) foreach { i =>
        bricks += new Brick((25 + (i * 50), 50))
        bricks += new Brick((25 + (i * 50), 100))
        bricks += new Brick((25 + (i * 50), 150))
        bricks += new Brick((25 + (i * 50), 175))
      }

      (0 until 3) foreach { i =>
        bricks += new Brick((25 + (1 * 50), 25 + ((i * 2) * 25))) 
        bricks += new Brick((25 + (2 * 50), 25 + ((i * 2) * 25))) 
        bricks += new Brick((25 + (3 * 50), 25 + ((i * 2) * 25))) 
        bricks += new Brick((25 + (6 * 50), 25 + ((i * 2) * 25))) 
        bricks += new Brick((25 + (7 * 50), 25 + ((i * 2) * 25))) 
        bricks += new Brick((25 + (8 * 50), 25 + ((i * 2) * 25))) 
      }
    }
 
    def draw = {
      bricks.foreach(_.draw)
    }
  }

  def update = {
    Bricks.draw
    Ball.draw
    World.draw 
    Paddle.draw

    val d = if (left) Paddle.speed * -1 else if (right) Paddle.speed else 0
    
    Paddle.x += d
    val inBounds = Paddle.inBounds

    if(!inBounds) Paddle.x = Paddle.x + d * -1 

    if (Ball.atRest && inBounds) {
      Ball.x += d 
    }

    if (space && Ball.atRest) Ball.fire 

    Ball.update

    sync

    if (Bricks.bricks.length == 0) {
      Bricks.init
      Ball.init
      Title
    } else if (escape) End
    else None
  }
}
