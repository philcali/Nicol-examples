package nicol
package examples.breakout

import input.Key._
import math._

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

