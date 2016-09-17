package com.ontheserverside.snippet

import java.text.SimpleDateFormat
import java.util.Date
import com.ontheserverside.lib.FutureHelpers
import net.liftweb.util.Helpers._
import net.liftweb.http._

import scala.concurrent.ExecutionContext.Implicits.global

class HelloWorld {

  def render = {
    "#scala-future *" #> FutureHelpers.withCurrentSession {
      Thread.sleep(1000);
      S ? ("general-futureCompleted", date)
    }
  }

  private def date: String = {
    new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())
  }
}