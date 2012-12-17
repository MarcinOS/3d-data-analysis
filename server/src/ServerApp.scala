import common._
import java.awt.Color._
import akka.actor.{ActorRef, Actor, ActorSystem, Props}
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory

/**
 * Created by IntelliJ IDEA.
 * User: vijayannadi
 * Date: 12/14/12
 * Time: 11:16 PM
 * To change this template use File | Settings | File Templates.
 */

class ServerApp extends Bootable {
  //#setup
  val system = ActorSystem("ServerApplication", ConfigFactory.load.getConfig("server"))
  val actor = system.actorOf(Props[ServerActor], "server")
  //#setup

  type Path = java.awt.geom.GeneralPath
  var paths: List[PathEntry] = PathEntry(new Path, black) :: Nil


  def startup() {
  }

  def shutdown() {
    system.shutdown()
  }
}

class ServerActor extends Actor {
  var clients: List[ActorRef] = List.empty
  var pathMap: Map[ActorRef, List[PathEntry]] = Map.empty

  def receive = {
    case Register(name) ⇒ {
      sendHello(sender)
      clients = sender :: clients
      pathMap = pathMap.updated(sender, List.empty[PathEntry])
    }

    case AddPath(path) ⇒ {
      clients.foreach {
        _ ! path
      }
      if (pathMap.contains(sender)) {
        pathMap = pathMap.updated(sender, (path :: pathMap(sender)).reverse)
      } else {
        pathMap = pathMap.updated(sender, List.empty[PathEntry])
      }
    }

    case Clear => {
      println("Got Clear from " + sender)
      clearClientPaths(sender)
      // Send a hello to all clients
      clients.foreach {
        sendHello(_)
      }
    }

    case Bye => {
      println("Got bye from " + sender)
      clearClientPaths(sender)
      clients = clients.filterNot(_ == sender)
    }
  }

  def clearClientPaths(client: ActorRef) {
    pathMap = pathMap - client
  }

  def sendHello(client: ActorRef) {
    if (pathMap.isEmpty)
      client ! Hello(List.empty[PathEntry])
    else {
      val l = pathMap.foldLeft(List.empty[PathEntry])((acc, kv) => acc ++ kv._2)
      println("Sending hello " + l)
      client ! Hello(l)
    }
  }
}

object ServerApp extends App {
  new ServerApp
}
