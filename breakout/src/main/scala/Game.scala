package nicol
package examples.breakout

// Next version of Nicol will support this as a BasicScene
// But I'm putting it here so that it's future compliant
trait FriendlyScene extends GameScene with SyncableScene with StandardRenderer

object App extends Game(Init("Breakout", 800, 600) >> Splash)
