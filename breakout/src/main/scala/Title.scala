package nicol
package examples.breakout

import input.Key._

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

