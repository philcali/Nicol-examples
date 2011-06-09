package nicol
package examples.breakout

import input.Key._
import math._
import geom._

// Next version of Nicol will support this as a BasicScene
// But I'm putting it here so that it's future compliant
trait FriendlyScene extends GameScene with SyncableScene with StandardRenderer

object App extends Game(Init("Breakout", 800, 600) >> Splash)

object Splash extends FriendlyScene {
  lazy val splash = font.Font("Arial", 32)

  val max = "Breakout".length
  val my = 300f
  val startingx = 320f
  val maxSpeed = 50f

  val accgravity = 0.9f

  var animating = true
  var index = 0 
  var location = (startingx, 0f)
  var fallSpeed = 0f

  def init = {
    animating = true
    index = 0
    initWorld
  } 

  def initWorld = {
    fallSpeed = 0f
    location = (startingx, 0f)
  }

  def update = {
    val text = "Breakout"
    val current = text.take(index).mkString
  
    val letter = text(index).toString

    if(animating) {
      splash.write(letter, pos = location)
      splash.write(current, pos = (startingx, my))
    }

    location = Vector.down * fallSpeed + location

    if (fallSpeed >= maxSpeed) fallSpeed = maxSpeed
    else fallSpeed += accgravity

    if (location._2 >= my) {
      index += 1
      initWorld
      location = (location._1 + splash.stringWidth(text.take(index).mkString), location._2)
    }

    if (index == max) animating = false

    sync
    
    if (!animating || char('\n')) Title
    else if (escape) End 
    else None 
  }
}

object Title extends FriendlyScene {
  lazy val big = font.Font("Arial", 32)

  val interval = 20
  var delay = 0

  var color = (1f, 1f, 1f)
  var step = 0.009f

  def update = {
    big.write("Breakout", pos = (320, 300), rgb = color)

    if (delay <= interval) {
      draw("Press Enter to Play", position = (330, 350))
    } else if (delay == interval + 10) {
      delay = 0
    }
    delay += 1
 
    val (r, g, b) = color
  
    if (r < 0 || r > 1) step = step * -1

    color = (r - step, g - step, b)
 
    sync

    if (escape) End
    else if (char('\n')) Start
    else None
  }
}

object Start extends FriendlyScene { 
  scene =>

  val grey = (0.8f, 0.8f, 0.8f)

  object World {
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

    def draw = {
      val center = AABox(x, y, 60, 20)
      val leftSide = Circle((x, y + 10), 10)
      val rightSide = Circle((x + 60, y + 10), 10)
      scene.draw(leftSide, rgb = (0.5f, 0.5f, 0))(FilledCircleRenderer)
      scene.draw(rightSide, rgb = (0.5f, 0.5f, 0))(FilledCircleRenderer)
      scene.draw(center, rgb = (0.5f, 1f, 0.3f))(FilledAABoxRenderer)
    }
  }

  object Ball {
    var atRest = true
    var x = Paddle.x + 30
    var y = Paddle.y - 7

    def init = {
      var atRest = true
      var x = Paddle.x + 30
      var y = Paddle.y - 5
    } 

    def draw = {
      scene.draw(Circle((x, y), 10))(FilledCircleRenderer)
    }
  }

  def update = {
    World.draw 
    Paddle.draw
    Ball.draw

    val d = if (left) Paddle.speed * -1 else if (right) Paddle.speed else 0
    
    Paddle.x += d 
    if (Ball.atRest) {
      Ball.x += d 
    }

    sync

    if (escape) End
    //else if(char('\n')) Paused
    else None
  }
}

object Paused extends FriendlyScene { 
  def update = {
    draw("Paused", position = (350, 300))

    if (escape) End
    else if (char('\n')) Start
    else None
  }
}
