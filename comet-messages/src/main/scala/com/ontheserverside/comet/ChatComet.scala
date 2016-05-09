package com.ontheserverside.comet

import java.util.UUID

import com.ontheserverside.server._
import net.liftmodules.messagebus.{Unsubscribe, Subscribe, MessageBus}
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.{CometActor, CometListener, RenderOut, NamedCometActorTrait}

import scala.xml.Text

// used together with ListenerManager on server side (see ChatServer)
trait CometListenerExample extends CometListener {

  override protected def registerWith = ChatServer

  def handleGlobalMessage: PartialFunction[Any, Unit] = {
    case NewGlobalMessage(adminId) =>
      partialUpdate(SetHtml("new-global-msg", Text(s"Admin with id=$adminId sent a global message")))
  }
}

// handles message sent by NamedCometListener (see ChatServer)
trait NamedCometListenerExample extends CometActor {

  def handleUserJoinedRoomMessage: PartialFunction[Any, Unit] = {
    case NewUserJoinedRoom(userId) =>
      partialUpdate(SetHtml("user-joined-msg", Text(s"User with id=$userId has just joined chat")))
  }
}

// handles message sent by MessageBus
trait MessageBusExample extends CometActor {

  override def localSetup = {
    super.localSetup
    MessageBus ! Subscribe(this, ChatRoomTopic(this.name openOr ""))
  }

  override def localShutdown = {
    MessageBus ! Unsubscribe(this, ChatRoomTopic(this.name openOr ""))
    super.localShutdown
  }

  def handleUserLeftRoomMessage: PartialFunction[Any, Unit] = {
    case UserLeftRoom(userId) =>
      partialUpdate(SetHtml("user-left-msg", Text(s"User with id=$userId has just left room")))
  }
}

class ChatComet extends CometListenerExample with NamedCometListenerExample with NamedCometActorTrait with MessageBusExample {

  private[this] val instanceId = UUID.randomUUID.toString

  override def render: RenderOut = {
    ".instance-id *" #> instanceId
  }

  override def mediumPriority = handleGlobalMessage orElse handleUserJoinedRoomMessage orElse handleUserLeftRoomMessage
}
