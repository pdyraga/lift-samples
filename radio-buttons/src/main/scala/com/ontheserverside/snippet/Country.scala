package com.ontheserverside.snippet

sealed trait Country

object Country {
  case object Poland extends Country
  case object Germany extends Country
  case object France extends Country
  case object Norway extends Country
  case object Sweden extends Country
  case object Finland extends Country
}

