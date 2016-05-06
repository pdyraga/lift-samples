package com.ontheserverside.server

import java.util.UUID

import net.liftweb.actor._
import net.liftweb.common.Full
import net.liftweb.http.{NamedCometListener, ListenerManager}
import net.liftweb.util._
  import Helpers._
import net.liftmodules.messagebus.{MessageBus, Topic, For}

case class ChatRoomTopic(val name: String) extends Topic

case class NewUserJoinedRoom(userId: String = UUID.randomUUID.toString)
case class UserLeftRoom(userId: String = UUID.randomUUID.toString)
case class NewGlobalMessage(adminId: String = UUID.randomUUID.toString)

trait ListenerManagerExample extends ListenerManager with LiftActor {
  case object GlobalMessageTick

  // just a dummy placeholder - it's required by ListenerManager
  val createUpdate: Any = "nothing"

  LAPinger.schedule(this, GlobalMessageTick, 5 seconds)

  def handleGlobalMessage: PartialFunction[Any, Unit] = {
    case GlobalMessageTick =>
      sendListenersMessage(NewGlobalMessage())
      LAPinger.schedule(this, GlobalMessageTick, 5 seconds)
  }
}

trait NamedCometListenerExample extends LiftActor {
  case object NewUserJoinedRoomTick

  LAPinger.schedule(this, NewUserJoinedRoomTick, 2 seconds)

  def handleNewUserJoinedRoom: PartialFunction[Any, Unit] = {
    case NewUserJoinedRoomTick =>
      val europeRoomMsg = NewUserJoinedRoom()
      NamedCometListener.getDispatchersFor(Full("chat-comet-europe")).map { dispatcher =>
        dispatcher.map(_ ! europeRoomMsg)
      }

      val northAmericaRoomMsg = NewUserJoinedRoom()
      NamedCometListener.getDispatchersFor(Full("chat-comet-north-america")).map { dispatcher =>
        dispatcher.map(_ ! northAmericaRoomMsg)
      }

      LAPinger.schedule(this, NewUserJoinedRoomTick, 2 seconds)
  }
}

trait MessageBusExample extends LiftActor {
  case object UserLeftRoomTick

  LAPinger.schedule(this, UserLeftRoomTick, 2 seconds)

  def handleUserLeftRoom: PartialFunction[Any, Unit] = {
    case UserLeftRoomTick =>
      MessageBus ! For(ChatRoomTopic("chat-comet-europe"), UserLeftRoom())
      MessageBus ! For(ChatRoomTopic("chat-comet-north-america"), UserLeftRoom())

      LAPinger.schedule(this, UserLeftRoomTick, 2 seconds)
  }
}


object ChatServer extends ListenerManagerExample with NamedCometListenerExample with MessageBusExample {
  override def mediumPriority = handleGlobalMessage orElse handleNewUserJoinedRoom orElse handleUserLeftRoom
}

