package com.ontheserverside.snippet

import net.liftweb.http.{S, NamedCometActorSnippet}

class Chat extends NamedCometActorSnippet {
  val cometClass = "ChatComet"

  val name = "chat-comet-" + S.param("name").getOrElse("")
}
